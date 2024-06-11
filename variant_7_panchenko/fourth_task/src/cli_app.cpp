#include <exam_mpi/cli_app.hpp>
#include <boost/mpi.hpp>
#include <boost/serialization/vector.hpp>

namespace exam_mpi {

	static constexpr int MAIN_RANK = 0;
	static constexpr int MAX_SIZE = 100000000;

	void main_logic(const size_t array_size) {

		const auto env = boost::mpi::environment();
		const auto world = boost::mpi::communicator();

		auto received_value = std::vector<int>();
		if(world.rank() == 0) {
			auto mat = std::vector<std::vector<int>>(world.size(), std::vector<int>(array_size));
			for(auto& v : mat) {
				std::generate(std::begin(v), std::end(v),
				[] { return std::rand() % MAX_SIZE; } );
			}
			boost::mpi::scatter(world, mat, received_value, MAIN_RANK);
			std::sort(std::begin(received_value), std::end(received_value));

			auto min_values = std::vector<int>();
			boost::mpi::gather(world, received_value[0], min_values, MAIN_RANK);
			for(const auto min_value : min_values) {
				std::cout << min_value << "\n";
			}
		} else {
			boost::mpi::scatter(world, received_value, MAIN_RANK);
			std::sort(std::begin(received_value), std::end(received_value));
			boost::mpi::gather(world, received_value[0], MAIN_RANK);
		}
	}

	CliApp::CliApp() : CLI::App("exam_mpi") {
		add_option("--size,-s", size, "Size of generated vector")
			->required(true);

		parse_complete_callback([this] {
			main_logic(size);
		});
	}
}