#include <exam_mpi/cli_app.hpp>
#include <boost/mpi.hpp>
#include <boost/serialization/vector.hpp>

namespace exam_mpi {

	static constexpr int MAIN_RANK = 0;
	static constexpr int MAX_SIZE = 10000;
	static constexpr int FROM_MAIN_THREAD_TAG = 1;
	static constexpr int FROM_CHILD_THREAD_TAG = 2;


	using Matrix = std::vector<std::vector<int>>;

	template<typename T>
	void print(const std::vector<T>& vec) {
		for(const auto v : vec) {
			std::cout << v << ", ";
		}
	}

	void print(const Matrix& mat) {
		for(const auto& row : mat) {
			print(row); std::printf("\n");
		}
	}

	Matrix generate_matrix(const size_t side_size) {
		auto mat = Matrix(side_size, std::vector<int>(side_size));
		for(auto& v : mat) {
			std::generate(std::begin(v), std::end(v),
			[] { return std::rand() % MAX_SIZE; } );
		}
		return mat;
	}

	namespace calc_average_vector {

		int calc_child_ranks_num(const boost::mpi::communicator& world) {
			return world.size() - 1;
		}

		static void main_rank_send_side_size(const boost::mpi::communicator& world, const Matrix& mat) {
			for(auto i = 1; i < world.size(); ++i) {
				world.send(i, FROM_MAIN_THREAD_TAG, mat.size());
			}
		}

		static size_t child_rank_recv_side_size(const boost::mpi::communicator& world) {
			size_t size = 0;
			world.recv(MAIN_RANK, FROM_MAIN_THREAD_TAG, size);
			return size;
		}

		template<typename T>
		static void index_rank_iterator(const boost::mpi::communicator& world, const size_t side_size, T&& callable) {
			const auto child_ranks_num = calc_child_ranks_num(world);
			for(size_t i = 0; i < side_size; ++i) {
				const auto child_index = static_cast<int>((i % child_ranks_num) + 1);
				callable(child_index, i);
			}
		}

		static double calc_average(const std::vector<int>& v) {
			return static_cast<double>(std::accumulate(std::begin(v), std::end(v), 0, std::plus<>()))
				/ static_cast<double>(v.size());
		}

		void child_rank(const boost::mpi::communicator& world) {
			const auto side_size = child_rank_recv_side_size(world);


			index_rank_iterator(world, side_size,
				[&world](const int child_index, const int mat_index) {
					if(child_index == world.rank()) {
						auto row = std::vector<int>();
						world.recv(MAIN_RANK, FROM_MAIN_THREAD_TAG, row);
						world.send(MAIN_RANK, FROM_CHILD_THREAD_TAG, calc_average(row));
					}
				}
			);
		}

		std::vector<double> main_rank(const boost::mpi::communicator& world, const Matrix& mat) {

			main_rank_send_side_size(world, mat);

			auto send_requests = std::vector<boost::mpi::request>();
			auto recv_requests = std::vector<boost::mpi::request>();
			auto average_values = std::vector<double>();

			send_requests.reserve(mat.size());
			recv_requests.reserve(mat.size());
			average_values.resize(mat.size());

			index_rank_iterator(world, mat.size(),
				[&](const int child_index, const int mat_index) {
				send_requests.emplace_back(world.isend(child_index, FROM_MAIN_THREAD_TAG, mat[mat_index]));
				recv_requests.emplace_back(world.irecv(child_index, FROM_CHILD_THREAD_TAG, average_values[mat_index]));
			});

			boost::mpi::wait_all(std::begin(send_requests), std::end(send_requests));
			boost::mpi::wait_all(std::begin(recv_requests), std::end(recv_requests));

			return average_values;
		}

	}

	void calc_c_array(const boost::mpi::communicator& world, const size_t side_size) {
		if(world.rank() == 0) {
			const auto first_mat = generate_matrix(side_size);
			const auto second_mat = generate_matrix(side_size);

			std::cout << "Matrices:\n";
			print(first_mat); std::printf("\n");
			print(second_mat); std::printf("\n");

			auto first_averages = calc_average_vector::main_rank(world, first_mat);
			auto second_averages = calc_average_vector::main_rank(world, second_mat);

			std::cout << "Averages:\n";
			print(first_averages); std::printf("\n");
			print(second_averages); std::printf("\n");

			for(size_t i = 0; i < first_averages.size(); ++i) {
				first_averages[i] *= second_averages[i];
			}

			std::cout << "Result:\n";
			print(first_averages);
			std::printf("\n");
		} else {

			calc_average_vector::child_rank(world);
			calc_average_vector::child_rank(world);

		}
	}

	void main_logic(const size_t side_size) {

		const auto env = boost::mpi::environment();
		const auto world = boost::mpi::communicator();

		const auto needed_procs_num = std::min(static_cast<size_t>(world.size()), side_size);
		const auto is_needed = world.rank() < needed_procs_num;
		const auto local_world = world.split(is_needed);

		if(not is_needed) {
			return;
		}

		calc_c_array(local_world, side_size);
	}

	CliApp::CliApp() : CLI::App("exam_mpi") {
		add_option("--size,-s", size, "side length of square matrix")
			->required(true);

		parse_complete_callback([this] {
			main_logic(size);
		});
	}
}