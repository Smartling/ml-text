#!/bin/bash
set -e

BASEDIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

MODELDIR=${1:-model}
TEST=${2:-testfile}
EXTRACTOR=$BASEDIR/${3:-../build/install/tools/bin/tools}

MODEL=$MODELDIR/ml.model
RANGE=$MODELDIR/ml.scale
KEYWORDS=$MODELDIR/ml.keywords

"$EXTRACTOR" extract "$KEYWORDS" "$TEST" \
  | "$BASEDIR/csv2libsvm.py" single > "$TEST.1"

svm-scale -r "$RANGE" "$TEST.1" > "$TEST.2"
svm-predict -b 1 "$TEST.2" "$MODEL" "$TEST.3"

cat "$TEST.3"
grep "^$(tail -n 1 $TEST.3 | cut -f 1 -d ' ') " "$MODELDIR/ml.labels"

rm "$TEST.1" "$TEST.2" "$TEST.3"
