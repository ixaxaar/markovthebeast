#!/bin/bash

dir=$1
experiment=$2
home=$PWD
atomslink=$home/link.atoms
typeslink=$home/link.types.pml
predicateslink=$home/link.predicates.pml

cd $dir;

for file in *.atoms; do
echo $file
stem=${file:0:(${#file}-6)}
atoms=$stem.atoms
types=$stem.types.pml
predicates=$stem.predicates.pml
cd $home
rm $atomslink $typeslink $predicateslink
ln -s $dir/$atoms $atomslink
ln -s $dir/$types $typeslink
ln -s $dir/$predicates $predicateslink
echo $stem > $dir/$stem.$experiment.out
thebeast $experiment.pml >> $dir/$stem.$experiment.out
cd $dir

done;
