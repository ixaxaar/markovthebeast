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

   -l|--list_size n       Size of the list to display
   -t|--train   filename  Filename of the training data

   -U|--support           Evaluate support chains

   -v|--verbose           Verbose output

   -h|--help              Prints this message
'''



try:                                
    opts, args = getopt.getopt(sys.argv[1:],"vUho:l:t:",["train","verbose","list_size=","output=","help","support"])
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
support = False
size=10
training=None

for opt,val in opts:
    if opt in ("-h","--help"):
        usage()
        sys.exit(0)
    elif opt in ("-o","--output"):
        output=val;
    elif opt in ("-v","--verbose"):
        verbose=True
    elif opt in ("-U","--support"):
        support=True
    elif opt in ("-l","--list_size"):
        size=int(val)
    elif opt in ("-t","--training"):
        training=val


def inc(dict,k):
    try:
        dict[k]+=1
    except KeyError:
        dict[k]=1


def sort_dic(adict):
    items = adict.values()
    keys  = adict.keys()
    new=zip(items,keys)
    new.sort()
    return new

def count_dic(dict):
    size=0
    for k,v in dict.iteritems():
        size+=v
    return size

def get_val(dict,v):
    try:
        return dict[v]
    except KeyError:
        return 0

def take(l,s,gs=None,tr=None):
    if not gs:
        if not tr:
            return "\n".join(["%d - %s"%(t,v) for t,v in l[-s:]])
        else:
            return "\n".join(["%d/ /%d - %s"%(t,get_val(tr,v),v) for t,v in l[-s:]])
    else:
        if not tr:
            return "\n".join(["%d/%d - %s"%(t,get_val(gs,v),v) for t,v in l[-s:]])
        else:
            return "\n".join(["%d/%d/%d - %s"%(t,get_val(gs,v),get_val(tr,v),v) for t,v in l[-s:]])



def getSemDepsConll08(preds,args):
    semDeps=[]
    for i in range(len(preds)):
        # position predicate
        p_i=int(preds[i][0])
        # sense
        bits=preds[i][1].split('.')
        if support or (len(bits)>1 and bits[1]!="SU"):
            # Semdep for sense
            semDeps.append((int(p_i),0,bits[1]))
            for a_i,arg in args[i]:
                # SemDep for argument
                if support or arg!="SU":
                    semDeps.append((p_i,int(a_i),arg))
    return semDeps

def getScores(tp,fp,fn,em=0,par=0,total=0,em_prop=0,par_prop=0,total_prop=0):
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

    if total==0:
        return pre,reca,f1
    else:
        pre_em=(em*100.0)/par
        reca_em=(em*100.0)/total
        f1_em=2.0*pre_em*reca_em/(pre_em+reca_em)
        pre_prop=(em_prop*100.0)/par_prop
        reca_prop=(em_prop*100.0)/total_prop
        f1_prop=2.0*pre_prop*reca_prop/(pre_prop+reca_prop)

        return pre,reca,f1,pre_prop,reca_prop,f1_prop,pre_em,reca_em,f1_em

def get_props(sds):
    props=[]
    tmp=[]
    first=True
    for prop in sds:
        if prop[1]==0 and not first:
            props.append(tmp)
            tmp=[]
            tmp.append(prop)
        else:
            tmp.append(prop)
        first=False
    return props

def eval(gs,sys):
    per_utt=[]
    t_tp=0
    t_fp=0
    t_fn=0
    em=0
    par=0
    em_prop=0
    par_prop=0
    total_prop=0
    for utt_ix in range(len(gs)):
        sds_gs=gs[utt_ix]
        sds_sys=sys[utt_ix]
        tp=0
        fn=0
        fp=0
        
        for sd_gs in sds_gs:
            if sd_gs[1]==0:
                n_prop=True
            if sd_gs in sds_sys:
                tp+=1
            else:
                fn+=1
        fp=len(sds_sys)-tp
        t_tp+=tp
        t_fp+=fp
        t_fn+=fn
        # exact match
        if fp==0 and fn==0:
            em+=1
        if len(sds_sys) > 0 or len(sds_gs)==0:
            par+=1
        # perferct proposition
        sys_props=get_props(sds_sys)
        gs_props=get_props(sds_gs)

        for prop in gs_props:
            if prop in sys_props:
                em_prop+=1
        total_prop+=len(gs_props)
        par_prop+=len(sys_props)

        per_utt.append(getScores(tp,fp,fn))
    return getScores(t_tp,t_fp,t_fn,em,par,len(gs),em_prop,par_prop,total_prop),per_utt,\
                    (t_tp,t_fp,t_fn,em,par,len(gs),em_prop,par_prop,total_prop)

def preds_args(s1,gs):
    preds=[]
    args=[]
    for s in s1:
        if s[1]==0:
            if s in gs:
                if support or not s[2]=="SU":
                    preds.append((s[0],s[2],True))
            else:
                if support or not s[2]=="SU":
                    preds.append((s[0],s[2],False))
        else:
            if s in gs:
                if support or not s[2]=="SU":
                    args.append((s[0],s[1],s[2],True))
            else:
                if support or not s[2]=="SU":
                    args.append((s[0],s[1],s[2],False))
    return preds,args
    
    
# Compares two labellings and finds what is different from one     
def mix(s1,s2):
    # False positives
    preds=[]
    args=[]
    # False negatives
    preds_=[]
    args_=[]
    # Confusion
    con_preds=[]
    con_args=[]
    con_preds_=[]
    con_args_=[]
    # Extra proposed
    extra_preds=[]
    extra_args=[]
    extra_preds_=[]
    extra_args_=[]

#
#    s1_t=([(s[0],s[1],False) for s in s1[0] if s[2]],[(s[0],s[1],s[2],False) for s in s1[1] if s[1][2]])

    
    # False positives
    for pred_2 in s2[0]:
        if not pred_2[2] and not pred_2 in s1[0]:
            preds.append(pred_2)
            s=False
            for pred_1 in s1[0]:
                if pred_1[0]==pred_2[0] and pred_1[2]:
                    con_preds.append((pred_1[0],"%s/%s"%(pred_1[1],pred_2[1])))
                    s=True
                    break
            if not s:
                extra_preds.append((pred_2[0],pred_2[1]))
    for arg_2 in s2[1]:
        if not arg_2[3] and not arg_2 in s1[1]:
            args.append(arg_2)
            s=False
            for arg_1 in s1[1]:
                if arg_1[0]==arg_2[0] and arg_1[1]==arg_2[1] and arg_1[3]:
                    con_args.append((arg_1[0],"%s/%s"%(arg_1[2],arg_2[2])))
                    s=True
                    break
            if not s:
                extra_args.append((arg_2[0],arg_2[2]))


    # False negatives
    for pred_1 in s1[0]:
        if pred_1[2] and not pred_1 in s2[0]:
            preds_.append(pred_1)
            s=False
            for pred_2 in s2[0]:
                if pred_1[0]==pred_2[0] and pred_1[2]:
                    con_preds_.append((pred_1[0],"%s/%s"%(pred_1[1],pred_2[1])))
                    s=True
                    break
            if not s:
                extra_preds_.append((pred_1[0],pred_1[1]))

    for arg_1 in s1[1]:
        if arg_1[3] and not arg_1 in s2[1]:
            args_.append(arg_1)
            s=False
            for arg_2 in s2[1]:
                if arg_1[0]==arg_2[0] and arg_1[1]==arg_2[1] and arg_1[3]:
                    con_args_.append((arg_1[0],"%s/%s"%(arg_1[2],arg_2[2])))
                    s=True
                    break
            if not s:
                extra_args_.append((arg_1[0],arg_1[2]))

    return preds,args,preds_,args_,con_preds,con_args,extra_preds,extra_args,con_preds_,con_args_,extra_preds_,extra_args_


        
# -------------------------------------------------------------
# Main section

if verbose:
    print >> sys.stderr, "Loading %s"%gs_filename

corpus_gs = Conll08Corpus(gs_filename)

score_str="""
    Semantic dependecies scores
     Precision : %3.2f
     Recall    : %3.2f
     F1        : %3.2f
    Perfect propositions
     Precision : %3.2f
     Recall    : %3.2f
     F1        : %3.2F
    Exact match scores
     Precision : %3.2f
     Recall    : %3.2f
     F1        : %3.2F
"""
    
GS_preds={}
GS_args={}
GS_roles={}
GS_proles={}

TR_preds=None
TR_args=None
TR_roles=None
TR_proles=None

if training:
    TR_preds={}
    TR_args={}
    TR_roles={}
    TR_proles={}

    corpus_tr = Conll08Corpus(training)

    for sntc in corpus_tr:
        sds=getSemDepsConll08(sntc.preds,sntc.args)
        for sd in sds:
            if sd[1] == 0:
                inc(TR_preds,"%s.%s"%(sntc[sd[0]]["s_lemma"],sd[2]))
            else:
                inc(TR_args,"%s"%sntc[sd[1]]["s_lemma"])
                inc(TR_proles,"%s-%s"%(sntc[sd[0]]["s_lemma"],sd[2]))
                inc(TR_roles,"%s"%sd[2])



semDeps_gs=[]
sntcs=[]
for sntc in corpus_gs:
    sntcs.append(sntc)
    semDeps_gs.append(getSemDepsConll08(sntc.preds,sntc.args))
    for sd in semDeps_gs[-1]:
        if sd[1] == 0:
            inc(GS_preds,"%s.%s"%(sntc[sd[0]]["s_lemma"],sd[2]))
        else:
            inc(GS_args,"%s"%sntc[sd[1]]["s_lemma"])
            inc(GS_proles,"%s-%s"%(sntc[sd[0]]["s_lemma"],sd[2]))
            inc(GS_roles,"%s"%sd[2])




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

    print >> output, "Scores for:%s\n"% filenames[i], score_str%scores[-1][0]
    print >> output, "tp=%s,fp=%s,fn=%s"%(scores[-1][2][0],scores[-1][2][1],scores[-1][2][2])

E_preds={}
E_roles={}
E_args={}

Ex_senses_={}
Ex_preds_={}
Ex_roles_={}
Ex_proles_={}

Ex_senses={}
Ex_preds={}
Ex_roles={}
Ex_proles={}

C_senses={}
C_preds={}
C_roles={}
C_proles={}
C_role={}

C_senses_={}
C_preds_={}
C_roles_={}
C_proles_={}
C_role_={}


E_preds_={}
E_roles_={}
E_args_={}


for i in range(len(filenames)):
    for j in range(i+1,len(filenames)):
        print >> output, "Comparing %s and %s" % (filenames[i],filenames[j])
        
        total_sntcs=0
        for ix in range(len(sntcs)):
            if True: #> scores[j][1][ix][2]:
                total_sntcs+=1
                if verbose:
                    print >> output, ">> Sentence %d"%(ix+1)
                    print >> output, sntcs[ix]
                    print >> output, "GS labelling"
                    for ii in range(len(sntcs[ix].preds)):
                        bits=sntcs[ix].preds[ii][1].split('.')
                        ix_pred=int(sntcs[ix].preds[ii][0])
                        if support or bits[1]!="SU":
                            print >> output, " %d-%s.%s"%(ix_pred,sntcs[ix][ix_pred]["s_lemma"],bits[1])

                        for a_i,arg in sntcs[ix].args[ii]:
                            # SemDep for argument
                            print >> output, " %d-%s,%s-%s,%s"%(ix_pred,sntcs[ix][ix_pred]["s_lemma"],a_i,sntcs[ix][int(a_i)]["s_lemma"],arg)
                            

                    print >> output, "system1 f1: %3.2f, system2: f1 %3.2f" % (scores[i][1][ix][2],scores[j][1][ix][2])
                pa1=preds_args(semDeps[i][ix],semDeps_gs[ix])
                pa2=preds_args(semDeps[j][ix],semDeps_gs[ix])
                preds_e,args_e,preds_e_,args_e_,preds_c,args_c,preds_ex,args_ex,preds_c_,args_c_,preds_ex_,args_ex_=mix(pa1,pa2)
                if len(preds_e) > 0 or len(preds_e_)>0:
                    if verbose:
                        print >> output, "Errores with predicates"
                    for pred,sense,v in preds_e:
                        inc(E_preds,"%s.%s"%(sntcs[ix][pred]["s_lemma"],sense))
                        if verbose:
                            print >> output, " %d-%s.%s"%(pred,sntcs[ix][pred]["s_lemma"],sense)
                    for pred,sense,v in preds_e_:
                        inc(E_preds_,sntcs[ix][pred]["s_lemma"]+"."+sense)
                        if verbose:
                            print >> output, "-%d-%s.%s"%(pred,sntcs[ix][pred]["s_lemma"],sense)
                    for pred,senses in preds_c:
                        inc(C_senses,senses)
                        inc(C_preds,"%s-%s"%(sntcs[ix][pred]["s_lemma"],senses))
                    for pred,senses in preds_ex:
                        inc(Ex_senses,senses)
                        inc(Ex_preds,"%s-%s"%(sntcs[ix][pred]["s_lemma"],senses))
                    for pred,senses in preds_c_:
                        inc(C_senses_,senses)
                        inc(C_preds_,"%s-%s"%(sntcs[ix][pred]["s_lemma"],senses))
                    for pred,senses in preds_ex_:
                        inc(Ex_senses_,senses)
                        inc(Ex_preds_,"%s-%s"%(sntcs[ix][pred]["s_lemma"],senses))

                if len(args_e) > 0 or len(args_e_)>0:
                    if verbose:
                        print >> output, "Errores with arguments"
                    for pred,arg,argl,v in args_e:
                        inc(E_args,sntcs[ix][arg]["s_lemma"])
                        inc(E_roles,argl)
                        if verbose:
                            print >> output, " %d-%s,%d-%s,%s"%(pred,sntcs[ix][pred]["s_lemma"],arg,sntcs[ix][arg]["s_lemma"],argl)
                    for pred,senses in args_c:
                        inc(C_roles,senses)
                        bits=senses.split('/')
                        inc(C_role,bits[1])
                        inc(C_proles,"%s-%s"%(sntcs[ix][pred]["s_lemma"],senses))
                    
                    for pred,senses in args_ex:
                        inc(Ex_roles,senses)
                        inc(Ex_proles,"%s-%s"%(sntcs[ix][pred]["s_lemma"],senses))

                    for pred,senses in args_c_:
                        inc(C_roles_,senses)
                        bits=senses.split('/')
                        inc(C_role_,bits[0])
                        inc(C_proles_,"%s-%s"%(sntcs[ix][pred]["s_lemma"],senses))
                    
                    for pred,senses in args_ex_:
                        inc(Ex_roles_,senses)
                        inc(Ex_proles_,"%s-%s"%(sntcs[ix][pred]["s_lemma"],senses))

                    for pred,arg,argl,v in args_e_:
                        inc(E_args_,sntcs[ix][arg]["s_lemma"])
                        inc(E_roles_,argl)
                        if verbose:
                            print >> output, "-%d-%s,%d-%s,%s"%(pred,sntcs[ix][pred]["s_lemma"],arg,sntcs[ix][arg]["s_lemma"],argl)
                if verbose: 
                    print >> output, ''


        print >> output, "Total sentences: %s" % total_sntcs
        print >> output, "False postives:"
        print >> output, " Predicates with errors: %d/%d \n%s" %(count_dic(E_preds),
                len(E_preds),take(sort_dic(E_preds),size,GS_preds,TR_preds))
        print >> output, " Arguments with errors: %d/%d \n%s" %(count_dic(E_args),
                len(E_args),take(sort_dic(E_args),size,GS_args,TR_args))
        print >> output, " Roles with errors: %d/%d \n%s" %(count_dic(E_roles),
                len(E_roles),take(sort_dic(E_roles),size,GS_roles,TR_roles))

        print >> output, "Confused labels:"
        print >> output, " Predicates confused:%d/%d\n%s"%(count_dic(C_preds),len(C_preds),take(sort_dic(C_preds),size))
        print >> output, " Senses confused: %d/%d\n%s"%(count_dic(C_senses),len(C_senses),take(sort_dic(C_senses),size))

        print >> output, " Roles confused: %d/%d \n%s"%(count_dic(C_role),len(C_role),take(sort_dic(C_role),size))
        print >> output, " Pair of roles confused: %d/%d \n%s"%(count_dic(C_roles),len(C_roles),take(sort_dic(C_roles),size))
        print >> output, " Senses and roles confused: %d/%d\n%s"%(count_dic(C_proles),len(C_proles),take(sort_dic(C_proles),size))

        print >> output, "Extra labels:"
        print >> output, " Predicates extra:%d/%d\n%s"%(count_dic(Ex_preds),len(Ex_preds),take(sort_dic(Ex_preds),size))
        print >> output, " Senses extra: %d/%d\n%s"%(count_dic(Ex_senses),len(Ex_senses),take(sort_dic(Ex_senses),size))
        print >> output, " Roles extra: %d/%d \n%s"%(count_dic(Ex_roles),len(Ex_roles),take(sort_dic(Ex_roles),size))
        print >> output, " Senses and roles extra: %d/%d\n%s"%(count_dic(Ex_proles),len(Ex_proles),take(sort_dic(Ex_proles),size))
 


        print >> output, "False Negatives:"
        print >> output, " Predicates missing: %d/%d \n%s"%(count_dic(E_preds_),
                len(E_preds_),take(sort_dic(E_preds_),size,GS_preds,TR_preds))
        print >> output, " Arguments missing: %d/%d \n%s" %(count_dic(E_args_),
                len(E_args_),take(sort_dic(E_args_),size,GS_args,TR_args))
        print >> output, " Roles missing: %d/%d \n%s" %(count_dic(E_roles_),
                len(E_roles_),take(sort_dic(E_roles_),size,GS_roles,TR_roles))


        print >> output, "Confused labels:"
        print >> output, " Predicates confused:%d/%d\n%s"%(count_dic(C_preds_),
                len(C_preds_),take(sort_dic(C_preds_),size))
        print >> output, " Senses confused: %d/%d\n%s"%(count_dic(C_senses_),
                len(C_senses_),take(sort_dic(C_senses_),size))

        print >> output, " Roles confused: %d/%d \n%s"%(count_dic(C_role_),
                len(C_role_),take(sort_dic(C_role_),size))
        print >> output, " Pair of roles confused: %d/%d\n%s"%(count_dic(C_roles_),
                len(C_roles_),take(sort_dic(C_roles_),size))
        print >> output, " Senses and roles confused:%d/%d\n%s"%(count_dic(C_proles_),
                len(C_proles_),take(sort_dic(C_proles_),size))

        print >> output, "Extra labels:"
        print >> output, " Predicates extra:%d/%d\n%s"%(count_dic(Ex_preds_),
                len(Ex_preds_),take(sort_dic(Ex_preds_),size))
        print >> output, " Senses extra: %d/%d\n%s"%(count_dic(Ex_senses_),
                len(Ex_senses_),take(sort_dic(Ex_senses_),size))
        print >> output, " Roles extra: %d/%d \n%s"%(count_dic(Ex_roles),
                len(Ex_roles_),take(sort_dic(Ex_roles_),size))
        print >> output, " Senses and roles extra:%d/%d\n%s"%(count_dic(Ex_proles_),
                len(Ex_proles_),take(sort_dic(Ex_proles_),size))
 

