#!/usr/bin/env python
import sys

class Converter:
    labels = []

    def index_label(self, label):
        n = -1
        if label in self.labels:
            n = self.labels.index(label)
        else:
            n = len(self.labels)
            self.labels.append(label)
        return n

    def process(self, label, features):
        n = self.index_label(label)
        ff = ['%d:%s' % (i+1,x) for i,x in enumerate(features)]
        s = ' '.join([str(n)] + ff)
        print(s)

    def dump_labels(self, fn):
        with open(fn, 'w') as f:
            for i,n in enumerate(self.labels):
                f.write('%d %s\n' % (i,n))

    def process_input(self, f):
        for ln in f:
            ff = ln.strip().split(',')
            self.process(ff[0], ff[1:])
        self.dump_labels('labels.txt')

    def process_single(self, f):
        for ln in f:
            ff = ln.strip().split(',')
            ff = ['%d:%s' % (i+1,x) for i,x in enumerate(ff[1:])]
            s = ' '.join([str(0)] + ff)
            print(s)

if __name__ == '__main__':

    p = Converter()
    if len(sys.argv) < 2:
        p.process_input(sys.stdin)
    else:
        p.process_single(sys.stdin)
