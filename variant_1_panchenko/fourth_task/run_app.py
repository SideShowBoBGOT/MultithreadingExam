import os
import random

procs_num = 5
numbers = [random.randint(0, 100) for _ in range(1000)]
numbers_str = ' '.join([f'{n}' for n in numbers])

print('Sum in MPI:')

os.system(f'mpiexec -np {procs_num} cmake-build-release/exam_mpi --vector {numbers_str}')

print()

print(f'Sum in Python: {sum(numbers)}')
