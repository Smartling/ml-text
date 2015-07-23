#!/bin/bash
set -e

BASEDIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

SRCDIR=${1:-data}
TODIR=${2:-model}
TMPDIR="tmp.$TODIR"
KEYWORDS=${3:-$TODIR/ml.keywords}
EXTRACTOR=$BASEDIR/${4:-../build/install/tools/bin/tools}
SVMGRID="$BASEDIR/svm-tools/grid.py"

mkdir -p "$TODIR"
cp "$KEYWORDS" "$TODIR/ml.keywords"

for D in $SRCDIR/*/
do
  L=`basename "${D}"`
  [[ $L =~ ^(predict)$ ]] && continue
  echo $L

  mkdir -p $TMPDIR

  find "$SRCDIR/$L" -type f -print0 | xargs -0 "$EXTRACTOR" extract "$KEYWORDS" > "$TMPDIR/$L.csv"
done

find "$TMPDIR" -type f -name *.csv -print0 \
  | xargs -0 "$BASEDIR/preprocess.py" \
  | "$BASEDIR/csv2libsvm.py" \
  | "$BASEDIR/split.py" "${TMPDIR}/train" "${TMPDIR}/test"

mv labels.txt "${TODIR}/ml.labels"

pushd "${TMPDIR}"

svm-scale -s range train > train.scaled
svm-scale -r range test > test.scaled

P=( $( "$SVMGRID" \
  -svmtrain $(which svm-train) \
  -gnuplot null -out null \
  -log2c -5,5,1 \
  -log2g -4,0,1 \
  -v 5 \
  -m 300 \
  train.scaled | tail -n 1 ) )

echo "C=${P[0]} g=${P[1]}"

svm-train -s 0 -t 2 -b 1 -q -e 0.001 -c ${P[0]} -g ${P[1]} train.scaled train.model
svm-predict -b 1 test.scaled train.model predictions.txt

popd

cp "$TMPDIR/train.model" "$TODIR/ml.model"
cp "$TMPDIR/range" "$TODIR/ml.scale"
