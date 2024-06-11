#include <exam_mpi/cli_app.hpp>
#include <boost/mpi.hpp>

namespace exam_mpi {

	static constexpr int MAIN_RANK = 0;
	constexpr auto FROM_TASK_THREAD_TAG = 2;

	size_t comm_size(const boost::mpi::communicator& world) {
		return static_cast<size_t>(world.size());
	}

	int calc_local_sum(const boost::mpi::communicator& world, const std::vector<int>& vec) {
		const auto chunk_size = vec.size() / comm_size(world);

		const auto start_shift = world.rank() * chunk_size;
		const auto end_shift = std::min(start_shift + chunk_size, vec.size());

		const auto start_it = std::next(std::cbegin(vec), static_cast<long>(start_shift));
		const auto end_it = std::next(std::cbegin(vec), static_cast<long>(end_shift));

		return std::accumulate(start_it, end_it, 0, std::plus());
	}

	void calc_global_sum(const boost::mpi::communicator& world, const std::vector<int>& vec) {
		if(world.rank() == MAIN_RANK) {

			auto global_sum = calc_local_sum(world, vec);
			for(auto i = 1; i < world.size(); ++i) {
				auto local_sum = 0;
				world.recv(i, FROM_TASK_THREAD_TAG, local_sum);
				global_sum += local_sum;
			}
			std::cout << global_sum;

		} else {
			world.send(MAIN_RANK, FROM_TASK_THREAD_TAG, calc_local_sum(world, vec));
		}
	}

	void main_logic(const std::vector<int>& vec) {

		const auto env = boost::mpi::environment();
		const auto world = boost::mpi::communicator();

		const auto needed_procs_num = std::min(static_cast<size_t>(world.size()), vec.size());
		const auto is_needed = world.rank() < needed_procs_num;
		const auto local_world = world.split(is_needed);

		if(not is_needed) {
			return;
		}

		calc_global_sum(world, vec);
	}

	CliApp::CliApp() : CLI::App("exam_mpi") {
		add_option("--vector,-v", vec, "Vector of elments")
			->required(true);

		parse_complete_callback([this] {
			main_logic(vec);
		});
	}
}