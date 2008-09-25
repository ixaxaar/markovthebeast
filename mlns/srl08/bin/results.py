#!/usr/bin/python2.4
# --------------------------------------------------------
# Extracts results from a log of the beast
# --------------------------------------------------------
# Script: results
# --------------------------------------------------------
# Ivan Vladimir Meza-Ruiz
# 2008/Edinburgh
# --------------------------------------------------------

# * Program arguments control

import sys
import os
import re 
import getopt
from corpus import *
from features import *
import random


def usage():
    print '''\
Usage: results.py logfile
   OUTPUT FILE label
   -o|--output filename   Filename of the the data file [outstd]

   -v|--verbose           Prints the verbose

   -h|--help              Prints this message
'''



try:                                
    opts, args = getopt.getopt(sys.argv[1:],"hvo:",["output="])
except getopt.GetoptError:           
    usage()                          
    sys.exit(2)                     

if len(args)==0:
    usage()
    sys.exit(1)

if len(args)==1:
    labels=["F1"]
else:
    labels=args[1:]

# Option initialization
filename=args[0]

output = sys.stdout
verbose=False

for opt,val in opts:
    if opt in ("-h","--help"):
        usage()
        sys.exit(0)
    elif opt in ("-o","--output"):
        output=open(val,"w")
    elif opt in ("-verbose","--verbose"):
        verbose=True

results=[]
result={}

state=0 # Out of results section
re_sep=re.compile('-------------------------')
re_metric=re.compile('^(\w+)\s+(\d.\d+)')
re_predicate=re.compile('^\w+$')
for line in open(filename):
    line=line.strip()
    if state==0:
        m = re_sep.match(line)
        if m:
            state=1 # It found new section of results
            type=prev_line
            result={}
            result[type]={}
    elif state==1:
        m = re_metric.match(line)
        if m:
            result[type][m.group(1)]=float(m.group(2))
        else:
            m = re_predicate.match(line)
            if m:
                type=m.group()
                result[type]={}
            else:
                m = re_sep.match(line)
                if not m:
                    results.append(result)
                    result={}
                    state=0
    prev_line=line



def get_history(label,metric,results):
    return [res[label][metric] for res in results]



for l in labels:
    
    print >> output, l, "scores"
    for label in results[0].keys():
        print >> output, "%20s"%label,"\t:", ",".join(["%1.3f"%x for x in\
            get_history(label,l,results)])






        







