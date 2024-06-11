cmake_minimum_required(VERSION 3.10)
project(exam_mpi)

set(CONAN_EXTERNAL "${CMAKE_CURRENT_SOURCE_DIR}/conan_external")
include(${CONAN_EXTERNAL}/generators/conan_toolchain.cmake)

set(CMAKE_CXX_STANDARD 17)

include_directories(/usr/lib/x86_64-linux-gnu/openmpi/include)
include_directories(/usr/lib/x86_64-linux-gnu/openmpi/include/openmpi)
link_directories(/usr/lib/x86_64-linux-gnu/openmpi/lib)

set(HEADERS
        include/exam_mpi/cli_app.hpp
)

set(SOURCES
        main.cpp
        src/cli_app.cpp
)

add_executable(${PROJECT_NAME}
        ${HEADERS}
        ${SOURCES}
)

target_include_directories(${PROJECT_NAME} PRIVATE include)
target_link_libraries(${PROJECT_NAME}
        PUBLIC
        mpi_cxx
        mpi
        boost_mpi
)

find_package(CLI11 REQUIRED)

target_link_libraries(${PROJECT_NAME}
        PRIVATE
        CLI11::CLI11
)
