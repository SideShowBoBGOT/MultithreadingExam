import os

procs_num = 5
size = 17

os.system(f'mpiexec -np {procs_num} cmake-build-debug/exam_mpi --size {size}')