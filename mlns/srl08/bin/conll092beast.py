#!/usr/bin/python2.4
# --------------------------------------------------------
# Transforms conll09 corpus into the beast format
# --------------------------------------------------------
# Script: conll092beast
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
Usage: conll092beast.py [options] conll_corpus
   OUTPUT FILES
   -s|--splits  num        Number of splits [0]  
   -o|--output  filename   Filename of the the data file [outstd]
   -d|--def     filename   Filename of the definition file [outstd]

   FEATURES
   -f|--features   type   Specify the features to extract
                          original  : the features for Share Task
                          mst_based : the features for Share Task (it
                          doens't include malt deps, but mst) [Default]
                          johanson  : the features from Johanson and Nugues 2008
                             (shared task)

   FORMATING
   -S|--strict            Use strict types on generarion of types [Off]
   -O|--open        File  Uses open format
   -M|--deps_label  File  Uses extra dependency labelling  (conll06 format)
   -L|--language    Lang  Specifies the language (useful for voice predicate)
   --deps_conll08         Conll08 format for the deps_label file 
   --possible_OFF         possiblePredicate based on heuristic for predicate
   --possible_preds       possiblePredicate based on predicate from pred_list (for pipeline system)
   --preds_list    File   preds list to replace base on file (stage 2 of pipeline)
   --per_predicate        Turns on one predicate per instance
   
   MODE
   -G|--gold_deps         Use gold dependencies for mst
   -T|--test              Testing mode
   -m|--max_lengh         Maximum length of the utterance
   --s1                   Use stage 1
   --s2                   Use stage 2
   --s3                   Use stage 3
   --propbank             Only outputs propbank predicates (based on the GPOS column)
   --nombank              Only outputs nombank predicates (based on the GPOS column)

   UTTERANCES
   -b|--begin  num        Starts printing from num line [0]
   -t|--total  num        Total number of utterance to transform [0]
   -r|--randomize         Randomize the corpus [off]

   -v|--verbose           Verbose output

   -h|--help              Prints this message
'''

try:                                
    opts, args = getopt.getopt(sys.argv[1:],"GTbrvhSOL:t:M:s:o:d:f:m:",[
    "splits=","output=","def=","features=","strict","open=","max_length=","deps_label=","possible_OFF","possible_preds","pred_list=","per_predicate","gold_deps","test","s1","s2","s3","propbank","nombank","begin=","total=","randomize","verbose","help","deps_conll08","language="])
except getopt.GetoptError:           
    usage()                          
    sys.exit(2)                     

if len(args)!=1:
    usage()
    sys.exit(1)

# Option initialization
filename=args[0]

# RE
re_num=re.compile(r'\d+(,\d+)*(\.\d+)?')
re_num2=re.compile(r'\d+')

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
ppos_files=None
conll05=None
conll06=None
conll07=None
conll08=None
no_hyphens=False
per_predicate=False
slashes=False
output_mst=False
gold_deps=False
stage=None
preds_list=None
# Original set Shared task
features=1
deps_conll08=False
propbank=False
nombank=False
language="english"
max_length=0

impossible.switch_filler_col()
impossible.switch_argument_ppos()

for opt,val in opts:
    if opt in ("-h","--help"):
        usage()
        sys.exit(0)
    elif opt in ("-s","--splits"):
        nsplits=int(val)
    elif opt in ("-f","--features"):
        if val == "original":
            features=0
        elif val == "mst_based":
            features=1
        elif val == "johanson":
            features=2
    elif opt in ("-o","--output"):
        output=val;
    elif opt in ("-m","--max_length"):
        max_length=int(val);
    elif opt in ("-d","--def"):
        deff=val;
    elif opt in ("-L","--language"):
        language=val;
    elif opt in ("-b","--begin"):
        ini=int(val)
    elif opt in ("-t","--total"):
        total=int(val)
    elif opt in ("-5","--conll05"):
        conll05=val
    elif opt in ("-6","--conll06"):
        conll06=val
    elif opt in ("-7","--conll07"):
        conll07=val
    elif opt in ("-8","--conll08"):
        conll08=val
    elif opt in ("-S","--strict"):
        strict=True
    elif opt in ("-r","--randomize"):
        randomize=True
    elif opt in ("--possible_OFF",):
        impossible.default()
    elif opt in ("--possible_preds",):
        impossible.use_preds()
    elif opt in ("--preds_list",):
        preds_list=val
    elif opt in ("-n","--none"):
        none="ALL"
    elif opt in ("-O","--open"):
        open_fmt_file= val
    elif opt in ("-M","--mst_label"):
        mst_files= val.split('@')
    elif opt in ("-P","--ppos_label"):
        ppos_files= val.split('@')

    elif opt in ("--mst_conll08",):
        mst_conll08=True
    elif opt in ("-v","--verbose"):
        verbose=True
    elif opt in ("-T","--test"):
        test=True
    elif opt in ("-H","--no_hyphens",):
        no_hyphens=True
    elif opt in ("-G","--gold_deps",):
        gold_deps=True
    elif opt in ("--output_mst",):
        output_mst=True
    elif opt in ("--per_predicate",):
        per_predicate=True
    elif opt in ("--s2",):
        labels_list=[">isPredicate"]
        stage="s2"
    elif opt in ("--s3",):
        stage="s3"
#        labels_list=[">isPredicate",">hasLabel",">role",">possiblePredicate"]
        labels_list=[">isPredicate",">isArgument",">hasLabel",">role",">possiblePredicate"]
    elif opt in ("--propbank",):
        propbank=True
    elif opt in ("--nombank",):
        nombank=True
        
# Features extraction before we replace some predicated by the predicates in
# preds_list
preds=[]
# Features extraction after we replace some predicated by the predicates in
# preds_list
pred_after=[]


# ---------------------------------------------------------------------
# preds contains the functions to use to extract the features from each
# utterance.

#ID FORM LEMMA PLEMMA POS PPOS FEAT PFEAT HEAD PHEAD DEPREL PDEPREL FILLPRED PRED APREDs 
# Single predicates:
preds.append((single_preds,[("word",["Int","Word"],"form"),
#                            ("lemma",["Int","Lemma"],"lemma"),
                            ("plemma",["Int","Lemma"],"plemma"),
#                            ("pos",["Int","Gpos"],"pos"),
                            ("ppos",["Int","Ppos"],"ppos")
                            ]))
# Corsed pos tag (this is only the first letter)
preds.append((cpos,("cpos",["Int","Cpos"],"ppos")))

# Features
preds.append((feats,("feature",["Int","Feat","Fval"],"pfeat")))

preds.append((possiblePredicate,("isPredicate",["Int"],[])))
preds.append((possibleArgument,("possibleArgument",["Int"])))


if language == "english":
    preds.append((voice,("voice",["Int","Voice"],"")))
    preds.append((sense,[("sense",["Int","Sense"])]))
elif language == "german":
    preds.append((voice_german,("voice",["Int","Voice"])))
    impossible.impossibleArgumentPOS = set(["ROOT","$,","$.","$(","ITJ","PAV","PPOSS","PRELAT","VAPP","VMPP"])
    preds.append((sense,[("sense",["Int","Sense"])]))
elif language == "spanish":
    preds.append((voice_catalan_spanish,("voice",["Int","Voice"])))
    impossible.impossibleArgumentPOS = set(["ROOT","f"])
    preds.append((sense,[("sense",["Int","Sense"])]))
elif language == "catalan":
    preds.append((voice_catalan_spanish,("voice",["Int","Voice"])))
    impossible.impossibleArgumentPOS = set(["ROOT","f","i"])
    preds.append((sense,[("sense",["Int","Sense"])]))
elif language == "czech":
    preds.append((voice_czech,("voice",["Int","Voice"])))
    impossible.impossibleArgumentPOS = set(["`","=","|",";","!","?","/","\'","\"",")","[","]","&","+"])
    impossible.switch_argument_plemma()
elif language == "chinese":
    preds.append((no_voice,("voice",["Int","Voice"])))
    impossible.impossibleArgumentPOS = set(["ROOT","ADADV","FW","PUADV"])
    preds.append((sense,[("sense",["Int","Sense"])]))
elif language == "japanese":
    preds.append((no_voice,("voice",["Int","Voice"])))
    impossible.impossibleArgumentPOS = set(["ROOT",",",".","(",")","CC","CO","MD","PFA","PFV","PPF","PPM","SFJP"])


# DEP base predicates
preds.append((dependency_link,("p1_dep",["Int","Int","MDependency"],"phead","pdep_rel","p1_link")))
# Path features for depency predicates
preds.append((depfeatures,("p1_path",["Int","Int","MPath"],"phead","pdep_rel",
                           "p1_frame",["Int","MFrame"])))


#HIDDEN predicates
# Role based predicates
preds.append((role_argument_label,[("role",["Int","Int","Role"],"ALL"),
                                   ("isArgument",["Int"]),
                                   ("hasRole",["Int","Int"])]))

# Frame based predicates



 

## -------------------------------------------------------------
# Main section
# Prepare splits
corpus = Conll09Corpus(filename,gold_deps=gold_deps)
bsig = TheBeastSignature(strict)
sntcs=range(corpus.size())

# Signature of the sentence
len_sig=3
len_tries=5

preds_possible=[]
preds_replace=[]


# Reads predicates from a beast file, which would be used instead of the
# generated by this script (used with pipeline configuration, the output on a
# file becomes the input of another
if preds_list:
    state=0
    ll=""
    for line in open(preds_list):
        line=line.strip()
        if state==0:
            if line.startswith('>>'):
                preds_replace.append(dict([(ele[1:],[]) for ele in labels_list]))
                preds_possible.append([])
                ll=""
            elif line in labels_list:
                ll=line[1:]       
                state=1
        elif state==1:
            if not line.startswith('>'):
                try:
                    preds_replace[-1][ll].append(tuple(line.split()))
                except KeyError:
                    preds_replace[-1][ll]=[tuple(line.split())]
                if ll in ["isPredicate"]:
                    preds_possible[-1].append((line,1))
            else:
                state=0
                if len(preds_replace) == 0:
                    preds_replace=empty_preds

# Format of the file with the extra dependencies, default is 06
if deps_conll08:
    mst_ixs=(8,9)
else:
    mst_ixs=(6,7)

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
            split_ini=s*ln
            if s+1 != nsplits:
                split_end = (s+1)*ln
            else:
                split_end= len(sntcs)
            if split_ini<=i and i<split_end:
                test.append(i)
            else:
                train.append(i)
        if output:
            splits.append((train,output+".train."+s,deff+".train."+str(s)))
            splits.append((test,output+".test."+s,deff+".test."+str(s)))
        else:
            splits.append((train,None,None))
            splits.append((test,None,None))

    for i in range(len(splits)/2):
        if (len(splits[2*i][0])+len(splits[2*i+1][0]))!=len(sntcs):
            print >> 'Error during spliting'
            sys.exit()

if verbose:
    i=0;
    for s in splits:
        print >> sys.stderr, "Split",i,":",s[1],s[2]
        print >> sys.stderr, " Starts:",s[0][:10]
        print >> sys.stderr, " Ends:",s[0][-10:]
        i+=1


if verbose:
    print >> sys.stderr, "Total of splits: ",len(splits)
nsplit=0
mst_id=0
ppos_id=0

if len(splits) == 1:
    if not conll05 is None:
        conll05f = [open(conll05,"w")]

    if not conll06 is None:
        conll06f = [open(conll06,"w")]

    if not conll07 is None:
        conll07f = [open(conll07,"w")]

    if not conll08 is None:
        conll08f = [open(conll08,"w")]
else:
    conll05f=[]
    conll06f=[]
    conll07f=[]
    conll08f=[]
    for i in range(len(splits)/2):
        if not conll05 is None:
            conll05f.append(open(conll05+"."+str(i),"w"))
            conll05f.append(open(conll05+".test."+str(i),"w"))

        if not conll06 is None:
            conll06f.append(open(conll06+"."+str(i),"w"))
            conll06f.append(open(conll06+".test."+str(i),"w"))

        if not conll07 is None:
            conll07f.append(open(conll07+"."+str(i),"w"))
            conll07f.append(open(conll07+".test."+str(i),"w"))

        if not conll08 is None:
            conll08f.append(open(conll08+"."+str(i),"w"))
            conll08f.append(open(conll08+".test."+str(i),"w"))
           

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
    miss=0
    for cs in corpus:
    # Main loop for sentences
        # If sntc in the split
        
        if not id in split:
            id+=1
            continue
        id+=1

    
        if max_length > 0 and len(cs)>max_length:
            continue

        if num < ini:
            continue
        num+=1


        if verbose:
            if len(split)/50>0 and num_total%(len(split)/ 50) == 0:
            
                print >> sys.stderr, ".",

        if total>0 and num_total>=total:
            break

        if mst_files != None and mst_id > len(mst_snts):
            break

        num_total+=1

        # A beast sentece
        print >> output, ">>" 

        if len(preds_possible) > 0:
            extra_preds=dict(preds_possible[id-1])
        else:
            extra_preds=None

        bs=TheBeastIntc(bsig)

        if nombank:
            cs.eliminate("ppos","V")
       
        if propbank:
            cs.eliminate("ppos","N")

        for f,args in  preds: 
            f(bsig,cs,bs,args,extra_preds)

        if preds_list:
            for ll,vals in preds_replace[id-1].iteritems():
                bs.replace(ll,vals) 

        if stage=="s2":
           bs.replace('possiblePredicate',preds_replace[id-1]["isPredicate"])

        if stage=="s3":
           bs.replace('possibleArgument',preds_replace[id-1]["isArgument"])


        if not per_predicate:
            print >> output, bs
        else:
            try:
                srl_preds=bs.intc['possiblePredicate']
                flag_=False
                for srl_pred in srl_preds:
                    bs.replace("possiblePredicate",[srl_pred])
                    if flag_:
                        print >> output, ">>" 
                    print >> output, bs
                    flag_=True
            except KeyError:
                #print >> output, bs
                pass

        if len(splits)==0:
            nsplit=1

        if not conll05 is None:
            if output_mst:
                print >> conll05f[nsplit-1], cs.conll05_mst()
                print >> conll05f[nsplit-1], ""
            else:
                print >> conll05f[nsplit-1], cs.conll05()
                print >> conll05f[nsplit-1], ""


        if not conll06 is None:
            if output_mst:
                print >> conll06f[nsplit-1], cs.conll06_mst()
                print >> conll06f[nsplit-1], ""
            else:
                print >> conll06f[nsplit-1], cs.conll06()
                print >> conll06f[nsplit-1], ""
 

        if not conll07 is None:
            if output_mst:
                print >> conll07f[nsplit-1], cs.conll07_mst()
                print >> conll07f[nsplit-1], ""
            else:
                print >> conll07f[nsplit-1], cs.conll07()
                print >> conll07f[nsplit-1], ""
 

        if not conll08 is None:
            if output_mst:
                print >> conll08f[nsplit-1], cs.conll08_mst()
                print >> conll08f[nsplit-1], ""
            else:
                print >> conll08f[nsplit-1], cs.conll08()
                print >> conll08f[nsplit-1], ""
 

    if verbose:
        print >> sys.stderr, ""

    if deffn == None:
        deff = sys.stdout
    else:
        deff = open(deffn,"w")

    bsig.print2file(deff)



