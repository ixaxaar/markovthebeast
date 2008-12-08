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
Usage: syncoConll05Conll08.py conll05_words conll05_props conll08 
   OUTPUT FILES
   -o|--output  filename   Filename of the the data file [outstd]

   -v|--verbose           Verbose output

   -h|--help              Prints this message
'''



try:                                
    opts, args = getopt.getopt(sys.argv[1:],"vho:i",["verbose","help","output="])
except getopt.GetoptError:           
    usage()                          
    sys.exit(2)                     

if len(args)!=3:
    usage()
    sys.exit(1)

# Option initialization
filename=args[2]
conll05_props=args[1]
conll05_words=args[0]

# RE
re_num=re.compile(r'\d+(,\d+)*(\.\d+)?')
re_num2=re.compile(r'\d+')

output = None
ini=0
total=0
verbose=False


for opt,val in opts:
    if opt in ("-h","--help"):
        usage()
        sys.exit(0)
    elif opt in ("-o","--output"):
        output=val;
        none="ALL"
    elif opt in ("-v","--verbose"):
        verbose=True


utts=[]
utt=[]
for line in open(conll05_words):
    line=line.strip()
    if len(line)>0:
        utt.append(line)
    else:
        utts.append(utt)
        utt=[]
if len(utt)>0:
    utts.append(utt)


srls=[]
srl=[]
for line in open(conll05_props):
    line=line.strip()
    if len(line)>0:
        srl.append(line)
    else:
        srls.append(srl)
        srl=[]
if len(srl)>0:
    srls.append(srl)

utts_gs=[]
for line in open(filename):
    line=line.strip()
    if len(line)>0:
        bits=line.split()
        utt.append(bits[1])
    else:
        utts_gs.append(utt)
        utt=[]
if len(utt)>0:
    utts_gs.append(utt)



len_sig=3
len_tries=10
num=0
num_total=0
id=-1
miss=0
mst_id=0
for cs in utts:
    # If sntc in the split
    id+=1
    
    
    if verbose:
        if len(utts)/50>0 and num_total%(len(utts)/ 50) == 0:
            print >> sys.stderr, ".",
    num_total+=1

    # Syncronises the mst corpus with the conll05
    try:
        ref=" ".join(cs[:3])
        ref=re_num.sub('<num>',ref)
        #print  "aaaa", ref, "\n", utts_gs[mst_id]
        chnk_ref=" ".join(utts_gs[mst_id][0:len_sig])
        chnk_ref=re_num.sub('<num>',chnk_ref)
    
        #print chnk_ref
        if chnk_ref != ref:
            #try nexts in mst_file
            print >> sys.stderr, "**--%s--(%s)"%(ref,id-1), "\n", " --%s--(%s)"%(" ".join(utts_gs[mst_id]),mst_id)
            found=False
            for t in range(len_tries):
                chnk_ref=" ".join(utts_gs[mst_id][0:len_sig])
                chnk_ref=re_num.sub('<num>',chnk_ref)
                if chnk_ref == ref:
                    found=True
                    mst_id+=t
                    break
            if not found:
                print >> sys.stderr, "*--%s--(%s)"%(ref,id-1), "\n", " --%s--(%s)"%(" ".join(utts_gs[mst_id]),mst_id)
                continue
        mst_id+=1
    except IndexError:
        print id,mst_id
        print len(cs), len(utts_gs[mst_id])
        continue


    # A beast sentece
    print >> output, "\n".join(srls[id])
    print >> output, "" 

