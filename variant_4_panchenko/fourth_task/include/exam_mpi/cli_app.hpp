#pragma once
#include <CLI/CLI.hpp>

namespace exam_mpi {
	class CliApp final : public CLI::App {
		public:
		CliApp();

		public:
		std::vector<int> vec;
	};
}
