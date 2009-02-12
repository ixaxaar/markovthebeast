#!/bin/bash

original_train=$1
original_dev=$2
conll06_train=${original_train}.gold.conll06
conll06_dev=${original_dev}.gold.conll06
conll06_dev_guess=${original_dev}.guess.conll06

# convert dev set
echo "Converting dev set"
scala -cp target/classes org.riedelcastro.CoNLL09To06 /tmp/Dummy 0 \
    < ${original_dev} \
    > ${conll06_dev}

# convert training set
echo "Converting training set"
scala -cp target/classes org.riedelcastro.CoNLL09To06 /tmp/Dummy 0 \
    < ${original_train} \
    > ${conll06_train}

# final training on full train set and testing on dev set
model=${conll06_train}.model
echo "Training on full train set and applying to test set, saving model in ${model}"

ksdep -t -it 700 -m $model < ${conll06_train}
ksdep -m $model < ${conll06_dev} > ${conll06_dev_guess}

# eval dev guess and gold
echo "Evaluating dev guess vs gold"
perl scripts/eval07.pl -g ${conll06_dev} -s ${conll06_dev_guess} > ${conll06_dev_guess}.eval
