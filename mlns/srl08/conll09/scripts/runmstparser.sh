#!/bin/bash

mstdir=/opt/mstparser
classpath=$mstdir:$mstdir/lib/trove.jar 

java -classpath $classpath -Xmx1800m mstparser.DependencyParser $@