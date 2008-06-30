#!/usr/bin/python2.4
# --------------------------------------------------------
# Transforms conll08 corpus into the beast format
# --------------------------------------------------------
# Script: conll082beast
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
Usage: conll082beast.py [options] conll_corpus
   OUTPUT FILES
   -s|--splits num        Number of splits [0]  
   -o|--output filename   Filename of the the data file [outstd]
   -d|--def    filename   Filename of the definition file [outstd]


   FORMATING
   -S|--strict            Use strict types on generarion of types [Off]
   -O|--open       File   Uses open format
   -M|--mst_label  File   Uses the MST labelling (conll06 format)

   MODE
   -N|--none              Generate a NONE role for each token pair without role label
   -T|--test              Testing mode

   UTTERANCES
   -b|--begin  num        Starts printing from num line [0]
   -t|--total  num        Total number of utterance to transform [0]
   -r|--randomize         Randomize the corpus [off]

   -v|--verbose           Verbose output

   -h|--help              Prints this message
'''



try:                                
    opts, args = getopt.getopt(sys.argv[1:],"vhrSTO:s:o:d:b:t:M:",["verbose","splits=",
        "output=","def=","strict","open=","begin=","total=","randomize","help","test",
        "mst_label="])
except getopt.GetoptError:           
    usage()                          
    sys.exit(2)                     

if len(args)!=1:
    usage()
    sys.exit(1)

# Option initialization
filename=args[0]

output = None
deff   = None
ini=0
total=0
strict = False
randomize = False
nsplits    = 0
open_fmt_file= None
none = "NOT"
verbose = False
test=False
mst_files=None

for opt,val in opts:
    if opt in ("-h","--help"):
        usage()
        sys.exit(0)
    elif opt in ("-s","--splits"):
        nsplits=int(val)
    elif opt in ("-o","--output"):
        output=val;
    elif opt in ("-d","--def"):
        deff=val;
    elif opt in ("-b","--begin"):
        ini=int(val)
    elif opt in ("-t","--total"):
        total=int(val)
    elif opt in ("-s","--strict"):
        strict=True
    elif opt in ("-r","--randomize"):
        randomize=True
    elif opt in ("-n","--none"):
        none="ALL"
    elif opt in ("-O","--open"):
        open_fmt_file= val
    elif opt in ("-M","--mst_file"):
        mst_files= val.split('@')
    elif opt in ("-v","--verbose"):
        verbose=True
    elif opt in ("-T","--test"):
        test=True


preds=[]

# ---------------------------------------------------------------------
# preds contains the functions to use to extract the features from each
# utterance.

# Single predicates:
preds.append((single_preds,[("word",["Int","Word"],"form"),
                            ("lemma",["Int","Lemma"],"lemma"),
                            ("gpos",["Int","Gpos"],"gpos"),
                            ("ppos",["Int","Ppos"],"ppos"),
                            ("sform",["Int","Sform"],"s_form"),
                            ("slemma",["Int","Slemma"],"s_lemma"),
                            ("sppos",["Int","Sppos"],"s_ppos")
                            ]))
# Corsed pos tag (this is only the first letter)
preds.append((cpos,("cpos",["Int","Cpos"])))

# Dependency predicate 
# preds.append((dependency,("dep",["Int","Int","Dependency"])))
# It extracts: 
#   head
#   dep_rel
#   link
if not test:
    preds.append((dependency_link,("dep",["Int","Int","Dependency"],"head","dep_rel","link")))

# SRL predicates
# Extracts roles
#preds.append((role,("role",["Int","Int","Role"])))
#preds.append((role_none,("role",["Int","Int","Role"],"ALL")))
if not test:
    preds.append((role_argument_label,[("role",["Int","Int","Role"],"ALL"),
        ("isArgument",["Int"]),
        ("hasLabel",["Int","Int"])]))

# Extracts frames
#preds.append((frame,("frame",["Int","Frame"])))
if not test:
    preds.append((frame_lemma,[("isPredicate",["Int"]),
                           ("frameLabel",["Int","FrameLabel"])]))

preds.append((voice,("voice",["Int","Voice"])))
preds.append((possiblePredicate,("possiblePredicate",["Int"])))
preds.append((possibleArgument,("possibleArgument",["Int"])))

# Preidcates used inf the open format
if not open_fmt_file is None:
    # Simple predicates for dependency parses
    preds.append((dependency_link,("m_dep",["Int","Int","MDependency"],"malt_head","malt_dep_rel","m_link")))

    # Path features for depency predicates
    preds.append((depfeatures,("m_path",["Int","Int","MPath"],"malt_head","malt_dep_rel",
                               "m_frame",["Int","MFrame"])))

    # Name entity simple predicates
    preds.append((ne,("ne_shared",["Int","NEshared"],"ne_03")))
    preds.append((ne,("ne_bbn",["Int","NEbbn"],"ne_bbn")))

    # WNet predicates
    preds.append((wnet,("wnet",["Int","WNet"],"wnet")))
                            
# Predicates for mst labellings
if not mst_files is None:
    preds.append((dependency_link,("mst_dep",["Int","Int","MDependency"],"mst_head","mst_dep_rel","mst_link")))

    # Path features for depency predicates
    preds.append((depfeatures,("mst_path",["Int","Int","MPath"],"mst_head","mst_dep_rel",
                               "mst_frame",["Int","MFrame"])))

# -------------------------------------------------------------
# Main section
# Prepare splits
corpus = Conll08Corpus(filename,open_fmt_file)
bsig = TheBeastSignature(strict)
sntcs=range(corpus.size())


mst_snts=[]
if mst_files != None:
    chg_map(["mst_head","mst_dep_rel"])
    for mst_file in mst_files:
        chnk=[]
        for line in open(mst_file):
            line=line.strip()            
            if len(line)>0:
                bits=line.split("\t")
                chnk.append((bits[-2],bits[-1]))                
            else:
                chnk.append(("ROOT","0"))                
                mst_snts.append(chnk)
                chnk=[]



if randomize:
    random.seed()
    sntcs=random(sntcs)

if nsplits==0:    
    splits=[(sntcs,output,deff)]
else:
    ln=len(sntcs)/nsplits
    splits=[]
    for s in range(nsplits):
        train=[]
        test=[]
        for i in range(len(sntcs)):
            if s*ln<=i and i<(s+1)*ln:
                test.append(i)
            else:
                train.append(i)
        splits.append((train,train,output+".train."+s,deff+".train."+str(s)))
        splits.append((test,test,output+".test."+s,deff+".test."+str(s)))

    for i in range(len(splits)/2):
        if (len(splits[2*i][0])+len(splits[2*i+1][0]))!=len(sntcs):
            print >> 'Error during spliting'
            sys.exit()

if verbose:
    i=0;
    for s in splits:
        print >> sys.stderr, "Split",i
        print >> sys.stderr, " Starts:",s[0][:10]
        print >> sys.stderr, " Ends:",s[0][-10:]
        i+=1



if verbose:
    print >> sys.stderr, "Total of splits: ",len(splits)
nsplit=0
for split,outputn,deffn in splits:
# loops for splits
    nsplit+=1
    if verbose:
        print >> sys.stderr, "Entering split:",nsplit
    if outputn == None:
        output = sys.stdout
    else:
        output = open(outputn,"w")


    num=0
    num_total=0
    id=0
    for cs in corpus:
    # Main loop for sentences
        # If sntc in the split
        
        if not id in split:
            continue
        id+=1


        if num < ini:
            continue
        num+=1


        if verbose:
            if num_total%(len(split)/ 50) == 0:
                print >> sys.stderr, ".",

        if total>0 and num_total>=total:
            break
        num_total+=1

        # A beast sentece
        print >> output, ">>" 

        if mst_files != None:
            cs.addChnk(mst_snts[id-1])
    
        bs=TheBeastIntc(bsig)
        for f,args in  preds:
            f(bsig,cs,bs,args)
    
        print >> output, bs

    if verbose:
        print >> sys.stderr, ""

    if deffn == None:
        deff = sys.stdout
    else:
        deff = open(deffn,"w")

    bsig.print2file(deff)



