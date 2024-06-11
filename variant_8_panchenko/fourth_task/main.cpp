#include <exam_mpi/cli_app.hpp>

int main(int argc, char *argv[]) {
	auto cli = exam_mpi::CliApp();
	CLI11_PARSE(cli, argc, argv);
	return 0;
}