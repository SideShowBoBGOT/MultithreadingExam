#include <exam_mpi/cli_app.hpp>
#include <boost/mpi.hpp>

namespace exam_mpi {

	static constexpr int MAX_SIZE = 100;

	void main_logic(const size_t array_size) {

		const auto env = boost::mpi::environment();
		const auto world = boost::mpi::communicator();

		auto vec = std::vector<int>(array_size);
		std::generate(std::begin(vec), std::end(vec),
			[] { return std::rand() % MAX_SIZE; } );

		const auto local_sum = std::accumulate(std::begin(vec), std::end(vec), 0, std::plus<>());
		auto sum = 0;
		boost::mpi::all_reduce(world, local_sum, sum, std::plus<>());
		if(world.rank()==0) {
			std::cout << "Sum: " << sum << "\n";
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