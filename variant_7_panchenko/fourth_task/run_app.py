import os

procs_num = 5
size = 1000

os.system(f'mpiexec -np {procs_num} cmake-build-release/exam_mpi --size {size}')