#!/bin/bash

conll=$1
gold=/tmp/gold.conll06
guess=/tmp/guess.conll06
eval=$1.conll-predicted.eval

scala -cp target/classes org.riedelcastro.CoNLL09To06 /tmp/Dummy 0 gold \
    < ${conll} \
    > ${gold}

scala -cp target/classes org.riedelcastro.CoNLL09To06 /tmp/Dummy 0 guess \
    < ${conll} \
    > ${guess}

perl scripts/eval07.pl -g ${gold} -s ${guess} 

