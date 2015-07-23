#!/usr/bin/env python
import sys
from numpy.random import shuffle


def split(lines, fn1, fn2):
    shuffle(lines)
    h = int( len(lines) // (100 / 30))

    with open(fn1, 'w') as f:
        for ln in lines[h:]:
            f.write(ln)

    with open(fn2, 'w') as f:
        for ln in lines[0:h]:
            f.write(ln)


if __name__ == '__main__':

    if len(sys.argv) < 3:
        print('Splits dataset into train and test data')
        print('usage: split.py [stdin] <train> <test>')
        exit()

    lines = [ln for ln in sys.stdin]
    split(lines, sys.argv[1], sys.argv[2])
