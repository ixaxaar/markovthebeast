#!/bin/bash

files=$@

for train in $files; do
    prefix=${train%.*}
    echo Train: $train >&2
    echo Prefix: $prefix >&2
    model=$prefix.model
    test=$prefix.test
    result=$prefix.result
    ksdep -t -m $model < $train
    ksdep -m $model < $test > $result
    cat $result
done
