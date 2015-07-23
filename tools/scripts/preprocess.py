#!/usr/bin/env python
import csv
import sys
from os.path import basename,splitext

class Processor:

    def process_row(self, row, label):
        return [label] + row[1:]

    def process(self, fn, label):
        with open(fn, 'r') as f:
            r = csv.reader(f)
            for row in r:
                features = self.process_row(row, label)
                s = ','.join(features)
                print(s)

    def process_files(self, list):
        for fn in list:
            label = splitext(basename(fn))[0]
            self.process(fn, label)

if __name__ == '__main__':

    if len(sys.argv) < 2:
        print('Intermediate processing of features')
        print('usage: preprocess.py file.csv ...')
        exit()

    p = Processor()
    p.process_files(sys.argv[1:])
