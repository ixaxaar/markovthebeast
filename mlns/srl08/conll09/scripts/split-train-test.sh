#!/bin/bash

original_train=$1
original_dev=$2
splits=$3
splitfiles_prefix=${original_train}_${splits}
conll06_train=${original_train}.gold.conll06
conll06_train_guess=${original_train}.${splits}splits.guess.conll06
conll06_dev=${original_dev}.gold.conll06
conll06_dev_guess=${original_dev}.guess.conll06

# convert dev set
echo "Converting dev set"
scala -cp target/classes org.riedelcastro.CoNLL09To06 /tmp/Dummy 0 \
    < ${original_dev} \
    > ${conll06_dev}

# convert training set
echo "Converting training set and creating $splits splits"
scala -cp target/classes org.riedelcastro.CoNLL09To06 $splitfiles_prefix $splits \
    < ${original_train} \
    > ${conll06_train}

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

    ksdep -t -m $model < $train
    ksdep -m $model < $test > $result
    cat $result >> ${conll06_train_guess}
done

# eval train guess and gold
echo "Evaluating train guess vs gold guess"
perl scripts/eval07.pl -g ${conll06_train} -s ${conll06_train_guess} > ${conll06_train_guess}.eval

model=${conll06_train}.model

# final training on full train set and testing on dev set
echo "Training on full train set and applying to test set, saving model in ${model}"

ksdep -t -m $model < ${conll06_train}
ksdep -m $model < ${conll06_dev} > ${conll06_dev_guess}

# eval dev guess and gold
echo "Evaluating dev guess vs gold"
perl scripts/eval07.pl -g ${conll06_dev} -s ${conll06_dev_guess} > ${conll06_dev_guess}.eval
