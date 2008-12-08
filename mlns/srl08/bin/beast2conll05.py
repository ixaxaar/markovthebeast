#!/usr/bin/python2.4
# --------------------------------------------------------
# Transforms the beast format into conll 2005
# --------------------------------------------------------
# Script: conll05 beast
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
Usage: beast2conll08 [options] beast_corpus
   OUTPUT FILES
   -o|--output filename   Filename of the the data file [outstd]

   FORMAT
   -p|--possible          Use possible predicate as the indexes for frames


   UTTERANCES
   -b|--begin  num        Starts printing from num line [0]
   -t|--total  num        Total number of utterance to transform [0]

   -v|--verbose           Prints the verbose

   -h|--help              Prints this message
'''



try:                                
    opts, args = getopt.getopt(sys.argv[1:],"hpvs:o:b:t:l:",["output=","l2p"
        ,"begin=","total=","help","verbose","possible"])
except getopt.GetoptError:           
    usage()                          
    sys.exit(2)                     

if len(args)!=1:
    usage()
    sys.exit(1)

# Option initialization
filename=args[0]

output = sys.stdout
ini=0
total=0
l2p_flnm=None
verbose=False
possible=False

for opt,val in opts:
    if opt in ("-h","--help"):
        usage()
        sys.exit(0)
    elif opt in ("-o","--output"):
        output=open(val,"w")
    elif opt in ("-b","--begin"):
        ini=int(val)
    elif opt in ("-t","--total"):
        total=int(val)
    elif opt in ("-l","--l2f"):
        l2p_flnm = val
    elif opt in ("-p","--possible"):
        possible = True
    elif opt in ("-verbose","--verbose"):
        verbose=True


corpus = TheBeastCorpus(filename)

preds=["lemma","gpos","ppos","sform","slemma","sppos"]


l2p={}
if not l2p_flnm is None:
    for line in open(l2p_flnm):
        line = line.strip()
        if len(line) > 0:
            bits = line.split()
            l2p[bits[1]+"."+bits[0]+"."+bits[2]]=bits[3]


def replace_lemma(lemma,sppos):
    try:
        return l2p[lemma+"."+sppos]
    except KeyError:
        return lemma
id=0
for utt in corpus:
    if verbose:
        if id%(corpus.size()/ 50) == 0:
            print >> sys.stderr, ".",
    id+=1
 
    lines=[]
    words=dict([(i,w[1:-1])for i,w in utt['word']])
    for ix in [str(i) for i in range(1,len(words))]:
        lines.append([ix,words[ix]])

    for pred in preds:
        eles=dict([(i,w[1:-1])for i,w in utt[pred]])
        if len(eles) ==0:
            eles=dict([(i,"_")for i,w in utt['word']])
        for i in range(1,len(words)):
            lines[i-1].append(eles[str(i)])

    lemmas=dict([(x,y[1:-1]) for x,y in utt['slemma']])
    spposs=dict([(x,y[1:-1]) for x,y in utt['sppos']])

    deps=dict([(i,(j,r[1:-1])) for j,i,r in utt['mst_dep']])
    if len(deps) ==0:
            deps=dict([(i,("_","_"))for i,w in utt['word']])

    for i in range(1,len(words)):
        lines[i-1].append(deps[str(i)][0])
        lines[i-1].append(deps[str(i)][1])

    try:
        if possible:
            iframes=[int(x[0]) for x in  utt['possiblePredicate']]
        else:
            iframes=[int(x[0]) for x in  utt['isPredicate']]
    except KeyError:

        iframes=[]

    try:
        frameLabels = dict([(i,j[1:-1]) for i,j in utt['frameLabel'] \
                if int(i) in iframes])
    except KeyError:
        frameLabels = {}

    try:
        frames = dict([(str(i),replace_lemma(lemmas[str(i)],words[str(i)]+'.'+spposs[str(i)])+"."+frameLabels[str(i)]) for i in iframes])
    except KeyError:
        frames = dict([(str(i),replace_lemma(lemmas[str(i)],words[str(i)]+'.'+spposs[str(i)])+"._") for i in iframes])


    for i in range(1,len(words)):
        try:
            lines[i-1].append(frames[str(i)])
        except KeyError:
            lines[i-1].append("_")
 
    try:
        roles=dict([((i,j),r[1:-1]) for i,j,r in utt['role'] if r!= '"NONE"'])
    except KeyError:
        roles={}



    iframes.sort()

    for j in range(1,len(words)):
        for i in iframes:
            try:
                lines[j-1].append(roles[(str(i),str(j))])
            except KeyError:
                lines[j-1].append("_")

    n_lines=[]
    for line in lines:
        n_lines.append("\t".join(line))


    sntc=Conll08Sntc(n_lines)
    print >> output,sntc.conll05()


    
    
    print >> output, ""




