#!/bin/bash

mstparser=$PWD/scripts/runmstparser.sh
original_train=$PWD/$1
original_dev=$PWD/$2
splits=$3
tmpDir=/tmp/mst_$(basename $original_train)
train_prefix=$tmpDir/mst_$(basename $original_train)
dev_prefix=$tmpDir/mst_$(basename $original_dev)

mstparams=$4 $5 $6

#create tmp dir on node
if [ -d $tmpDir ]; then
    rm -r $tmpDir
fi
mkdir $tmpDir

splitfiles_prefix=${tmpDir}/train_${splits}splits
conll06_train=${train_prefix}.gold.conll06
conll06_train_guess=${train_prefix}.${splits}splits.guess.conll06
conll06_dev=${dev_prefix}.gold.conll06
conll06_dev_guess=${dev_prefix}.guess.conll06

# convert dev set
echo "Converting dev set"
scala -cp target/classes org.riedelcastro.CoNLL09To06 /dev/null 0 \
    < ${original_dev} \
    > ${conll06_dev}

# convert training set
echo "Converting training set and creating $splits splits"
scala -cp target/classes org.riedelcastro.CoNLL09To06 $splitfiles_prefix $splits \
    < ${original_train} \
    > ${conll06_train}

oldDir=$PWD
cd $tmpDir

# training splits
echo "Started to train splits"
if [ -a ${conll06_train_guess} ]; then
    rm ${conll06_train_guess}
fi
for train in ${splitfiles_prefix}*.train ; do
    prefix=${train%.*}
    model=$prefix.model
    test=$prefix.test
    result=$prefix.result

    echo Train: $train >&2
    echo Prefix: $prefix >&2
    echo Model: $model >&2
    echo Test: $test >&2
    echo Result: $result >&2

    sh $mstparser train train-file:$train model-name:$model test test-file:$test output-file:$result $mstparams
    cat $result >> ${conll06_train_guess}
done

# eval train guess and gold
echo "Evaluating train guess vs gold guess"
perl $oldDir/scripts/eval07.pl -g ${conll06_train} -s ${conll06_train_guess} > ${conll06_train_guess}.eval

model=${conll06_train}.model

# final training on full train set and testing on dev set
echo "Training on full train set and applying to test set, saving model in ${model}"

sh $mstparser train train-file:${conll06_train} model-name:$model test test-file:${conll06_dev} output-file:${conll06_dev_guess} $mstparams

# eval dev guess and gold
echo "Evaluating dev guess vs gold"
perl $oldDir/scripts/eval07.pl -g ${conll06_dev} -s ${conll06_dev_guess} > ${conll06_dev_guess}.eval

# remove forrest files
rm *.forest

# go back to old dir
cd $oldDir

# timestamp for result dir
timestamp=$(date +"%m-%d-%y-%H-%M-%S")
resultdir=mst_$(basename $original_train)_$timestamp

# move results to current directory
echo Moving result directory to directory $resultdir
mv $tmpDir $resultdir