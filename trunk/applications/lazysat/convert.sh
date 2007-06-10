#!/bin/bash

dir=$1
mln=$2
ext=$3

for db in $dir/*$ext; do
java -Xmx500m -cp ../../classes/production thebeast.util.alchemy.AlchemyConverter \
  $mln \
  $db \
  $db.types.pml \
  $db.predicates.pml \
  $db.atoms
done;
