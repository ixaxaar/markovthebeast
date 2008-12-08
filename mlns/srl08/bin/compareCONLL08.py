#!/usr/bin/python2.4
# --------------------------------------------------------
# Compare two corpus 
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
Usage: conll082beast.py [options] conll_goal conll_file1 conll_file2 [conll_files]
   OUTPUT FILES
   -o|--output  filename   Filename of the the data file [outstd]

   -v|--verbose           Verbose output

   -h|--help              Prints this message
'''



try:                                
    opts, args = getopt.getopt(sys.argv[1:],"vh:o",["verbose","output=","help"])
except getopt.GetoptError:           
    usage()                          
    sys.exit(2)                     

if len(args)<3:
    usage()
    sys.exit(1)

# Option initialization
gs_filename=args[0]
filenames = args[1:]

# RE
re_num=re.compile(r'\d+(,\d+)*(\.\d+)?')
re_num2=re.compile(r'\d+')

output = None
verbose = False

for opt,val in opts:
    if opt in ("-h","--help"):
        usage()
        sys.exit(0)
    elif opt in ("-o","--output"):
        output=val;
    elif opt in ("-v","--verbose"):
        verbose=True


def getSemDepsConll08(preds,args):
    semDeps=[]
    for i in range(len(preds)):
        # position predicate
        p_i=int(preds[i][0])
        # sense
        bits=preds[i][1].split('.')
        # Semdep for sense
        semDeps.append((int(p_i),0,bits[1]))
        for a_i,arg in args[i]:
            # SemDep for argument
            semDeps.append((p_i,int(a_i),arg))
    return semDeps

def getScores(tp,fp,fn):
    if tp>0:
        pre=tp*100.0/(tp+fp)
        reca=tp*100.0/(tp+fn)
    else:
        pre=0.0
        reca=0.0
    if pre>0.0 and reca>0.0:
        f1=2.0*pre*reca/(pre+reca)
    else:
        f1=0.0
    return pre,reca,f1
        
def eval(gs,sys):
    per_utt=[]
    t_tp=0
    t_fp=0
    t_fn=0
    for utt_ix in range(len(gs)):
        sds_gs=gs[utt_ix]
        sds_sys=sys[utt_ix]
        tp=0
        fn=0
        fp=0
        for sd_gs in sds_gs:
            if sd_gs in sds_sys:
                tp+=1
            else:
                fn+=1
        fp=len(sds_sys)-tp
        t_tp+=tp
        t_fp+=fp
        t_fn+=fn
        per_utt.append(getScores(tp,fp,fn))
    return getScores(t_tp,t_fp,t_fn),per_utt 

def preds_args(s1,gs):
    preds=[]
    args=[]
    for s in s1:
        if s[1]==0:
            if s in gs:
                preds.append((s[0],s[2],True))
            else:
                preds.append((s[0],s[2],False))
        else:
            if s in gs:
                args.append((s[0],s[1],s[2],True))
            else:
                args.append((s[0],s[1],s[2],False))
    return preds,args
    
    
    
def mix(s1,s2):
    preds=[]
    args=[]
#    s1_t=([(s[0],s[1],False) for s in s1[0] if s[2]],[(s[0],s[1],s[2],False) for s in s1[1] if s[1][2]])

    for pred_2 in s2[0]:
        if not pred_2[2] and not pred_2 in s1:
            preds.append(pred_2)
    for arg_2 in s2[1]:
        if not arg_2[3] and not arg_2 in s1:
            args.append(arg_2)
    
    return preds,args

        
# -------------------------------------------------------------
# Main section

if verbose:
    print >> sys.stderr, "Loading %s"%gs_filename

corpus_gs = Conll08Corpus(gs_filename)

score_str="""
    Scores for %s,
     Precision : %3.2f
     Recall    : %3.2f
     F1        : %3.2f
"""
    


semDeps_gs=[]
sntcs=[]
for sntc in corpus_gs:
    sntcs.append(sntc)
    semDeps_gs.append(getSemDepsConll08(sntc.preds,sntc.args))


scores=[]
semDeps=[]
for i in range(len(filenames)):
    if verbose:
        print >> sys.stderr, "Loading %s"%filenames[i]

    corpus= Conll08Corpus(filenames[i])
    semDep=[]
    for sntc in corpus:
        semDep.append(getSemDepsConll08(sntc.preds,sntc.args))

    scores.append(eval(semDeps_gs,semDep))
    semDeps.append(semDep)

    print >> output, score_str%(filenames[i],scores[-1][0][0],scores[-1][0][1],scores[-1][0][2])


for i in range(len(filenames)):
    for j in range(i+1,len(filenames)):
        if verbose:
            print >> output, "Comparing %s and %s" % (filenames[i],filenames[j])
        
        total_sntcs=0
        for ix in range(len(sntcs)):
            if scores[i][1][ix][2] > scores[j][1][ix][2]:
                total_sntcs+=1
                if verbose:
                    print >> output, ">> Sentence %d"%(ix+1)
                    print >> output, sntcs[ix]
                    print >> output, "GS labelling"
                    for ii in range(len(sntcs[ix].preds)):
                        bits=sntcs[ix].preds[ii][1].split('.')
                        ix_pred=int(sntcs[ix].preds[ii][0])
                        print >> output, " %d-%s.%s"%(ix_pred,sntcs[ix][ix_pred]["lemma"],bits[1])

                        for a_i,arg in sntcs[ix].args[ii]:
                            # SemDep for argument
                            print >> output, " %d-%s,%s-%s,%s"%(ix_pred,sntcs[ix][ix_pred]["lemma"],a_i,sntcs[ix][int(a_i)]["lemma"],arg)
                            

                    print >> output, "system1 f1: %3.2f, system2: f1 %3.2f" % (scores[i][1][ix][2],scores[j][1][ix][2])
                    pa1=preds_args(semDeps[i][ix],semDeps_gs[ix])
                    pa2=preds_args(semDeps[j][ix],semDeps_gs[ix])
                    preds_e,args_e=mix(pa1,pa2)
                    if len(preds_e) > 0:
                        print >> output, "Errores with predicates"
                        for pred,sense,v in preds_e:
                            print >> output, " %d-%s.%s"%(pred,sntcs[ix][pred]["lemma"],sense)
                    if len(args_e) > 0:
                        print >> output, "Errores with arguments"
                        for pred,arg,argl,v in args_e:
                            print >> output, " %d-%s,%d-%s,%s"%(pred,sntcs[ix][pred]["lemma"],arg,sntcs[ix][arg]["lemma"],argl)
                    
                    print >> output, ''

        print >> output, "Total sentences with errors: %s" % total_sntcs



