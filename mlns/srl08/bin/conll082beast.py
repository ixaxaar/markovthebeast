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
   -s|--splits  num        Number of splits [0]  
   -o|--output  filename   Filename of the the data file [outstd]
   -d|--def     filename   Filename of the definition file [outstd]
   -5|--conll05 filename   Filename of the conll05 format file 
   -6|--conll06 filename   Filename of the conll06 format file
   -7|--conll07 filename   Filename of the conll07 format file 
   -8|--conll08 filename   Filename of the conll08 format file
   --output_mst            Use mst deps for output


   FORMATING
   -S|--strict            Use strict types on generarion of types [Off]
   -O|--open       File   Uses open format
   -M|--mst_label  File   Uses the MST labelling (conll06 format)
   -H|--no_hyphens        It compress the hyphens into one token (as in 2005 format)
   --possible_ON          possiblePredicate based on actual predicate

   MODE
   -N|--none              Generate a NONE role for each token pair without role label
   -G|--gold_deps         Use gold dependencies for mst dependecies
   -T|--test              Testing mode

   UTTERANCES
   -b|--begin  num        Starts printing from num line [0]
   -t|--total  num        Total number of utterance to transform [0]
   -r|--randomize         Randomize the corpus [off]

   -v|--verbose           Verbose output

   -h|--help              Prints this message
'''



try:                                
    opts, args = getopt.getopt(sys.argv[1:],"vhrGSHLTO:s:o:d:b:t:M:5:6:7:8:",["verbose","splits=",
        "output=","def=","strict","open=","begin=","total=","randomize","help","test","conll05=","conll06=","conll08=","conll07=","possible_ON",
        "mst_label=","no_hyphens","output_mst","gold_deps"])
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
conll05=None
conll06=None
conll07=None
conll08=None
no_hyphens=False
slashes=False
output_mst=False
gold_deps=False

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
    elif opt in ("-5","--conll05"):
        conll05=val
    elif opt in ("-6","--conll06"):
        conll06=val
    elif opt in ("-7","--conll07"):
        conll07=val
    elif opt in ("-8","--conll08"):
        conll08=val
    elif opt in ("-s","--strict"):
        strict=True
    elif opt in ("-r","--randomize"):
        randomize=True
    elif opt in ("--possible_ON"):
        impossible.switch_only()
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
    elif opt in ("-H","--no_hyphens"):
        no_hyphens=True
    elif opt in ("-G","--gold_deps"):
        gold_deps=True
    elif opt in ("--output_mst"):
        output_mst=True




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
#if not test:
#    preds.append((dependency_link,("dep",["Int","Int","Dependency"],"head","dep_rel","link")))

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
#if not open_fmt_file is None:
    # Simple predicates for dependency parses
    #preds.append((dependency_link,("m_dep",["Int","Int","MDependency"],"malt_head","malt_dep_rel","m_link")))

    # Path features for depency predicates
    #preds.append((depfeatures,("m_path",["Int","Int","MPath"],"malt_head","malt_dep_rel","m_frame",["Int","MFrame"])))

    # Name entity simple predicates
    #preds.append((ne,("ne_shared",["Int","NEshared"],"ne_03")))
    #preds.append((ne,("ne_bbn",["Int","NEbbn"],"ne_bbn")))

    # WNet predicates
    #preds.append((wnet,("wnet",["Int","WNet"],"wnet")))
                            
# Predicates for mst labellings
if not mst_files is None:
    preds.append((dependency_link,("mst_dep",["Int","Int","MDependency"],"mst_head","mst_dep_rel","mst_link")))

    # Path features for depency predicates
    preds.append((depfeatures,("mst_path",["Int","Int","MPath"],"mst_head","mst_dep_rel",
                               "mst_frame",["Int","MFrame"])))

# -------------------------------------------------------------
# Main section
# Prepare splits
corpus = Conll08Corpus(filename,open_fmt_file,mst_files,hyphens=(not no_hyphens),gold_deps=gold_deps)
bsig = TheBeastSignature(strict)
sntcs=range(corpus.size())

# Signature of the sentence
len_sig=3
len_tries=5


mst_snts=[]
if mst_files != None:
    chg_map(["mst_head","mst_dep_rel"])
    for mst_file in mst_files:
        chnk=[]
        ppos=[]
        utt=[]
        collapse=False
        size_collapse=0
        first_collapse=0
        break_collapse=[]
        for line in open(mst_file):
            line=line.strip()
            if len(line)>0:
                bits=line.split("\t")
                chnk.append((bits[6],bits[7]))
                if bits[4]=="_":
                    ppos.append((bits[1],bits[3]))
                else:
                    ppos.append((bits[1],bits[4]))
                # Stores a signature for the utterance (dep_rel)
                u=bits[1].replace('-lrb-','(').replace('-rrb-',')')
                utt.append(u)
            else:
                if not slashes:
                    changed=True
                    while changed:
                        changed=False
                        skip=0
                        n_chnk=[]
                        inside=False
                        for i in range(len(chnk)):
                            p=ppos[i][1]
                            if p == 'CC' and ppos[i][0]=="/":
                                n_chnk+=chnk[i+2:]
                                del ppos[i:i+2]
                                changed=True
                                skip=i+1
                                break
                            else:
                                n_chnk.append(chnk[i])
                        if changed:
                            for i in range(len(n_chnk)):
                                if int(n_chnk[i][0])>skip+2:
                                    n_chnk[i]=(str(int(n_chnk[i][0])-2),n_chnk[i][1])
                        chnk=n_chnk
                            

                chnk.append(("0","ROOT"))
                ppos.append(("_","R"))
                utt.append("ROOT")
                size_collapse=0
                if len_sig==-1:
                    ref=" ".join(utt)
                else:
                    ref=" ".join(utt[:len_sig])
                
                if len(break_collapse)>0:
                    mm=[]
                    cc=0
                    for i in range(len(chnk)):
                        if i in break_collapse:
                            mm.append((cc,i))
                            mm.append((cc+1,i))
                            cc+=2
                        else:
                            mm.append((cc,i))
                            cc+=1
                    mmm=dict(mm)
                    for i in range(len(chnk)):
                        chnk[i]=(str(mmm[int(chnk[i][0])]),chnk[i][1])

                
                mst_snts.append((chnk,ref,[ y for x,y in ppos]))
                utt=[]
                chnk=[]
                ppos=[]



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
mst_id=0

if not conll05 is None:
    conll05f = open(conll05,"w")

if not conll06 is None:
    conll06f = open(conll06,"w")

if not conll07 is None:
    conll07f = open(conll07,"w")

if not conll08 is None:
    conll08f = open(conll08,"w")


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
            continue
        id+=1


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

        if mst_files != None:
            # Syncronises the mst corpus with the conll05
            try:
                if len_sig==-1:
                    ref=cs.utt(0,"form")
                else:
                    ref=cs.utt(len_sig,"form")
                ref=re_num.sub('<num>',ref)
#                print >> sys.stderr,"aaaa", ref, "\n", mst_snts[id-1][1]
                chnk,chnk_ref,chnk_ppos=mst_snts[mst_id]
                chnk_ref=re_num.sub('<num>',chnk_ref)
            
                #print chnk_ref
                if chnk_ref != ref:
                    #try nexts in mst_file
                    print >> sys.stderr, "*--%s--(%s)"%(ref,id-1), "\n", " --%s--(%s)"%(mst_snts[mst_id][1],mst_id)
                    found=False
                    for t in range(len_tries):
                        chnk,chnk_ref,chnk_ppos=mst_snts[mst_id+t]
                        chnk_ref=re_num.sub('<num>',chnk_ref)
                        if chnk_ref == ref:
                            found=True
                            mst_id+=t
                            break
                    if not found:
                        print >> sys.stderr, "*--%s--(%s)"%(ref,id-1), "\n", " --%s--(%s)"%(mst_snts[mst_id][1],mst_id)
                        continue
                cs.replace('ppos',chnk_ppos)
                if not gold_deps:
                    cs.addChnk(chnk)
                else:
                    chnkg=[(w['head'],w['dep_rel']) for w in cs]
                    cs.addChnk(chnkg)
                mst_id+=1
            except IndexError:
                print id
                print len(cs), len(mst_snts[mst_id])
                print cs
                sys.exit(1)

    
        # A beast sentece
        print >> output, ">>" 

        bs=TheBeastIntc(bsig)
        for f,args in  preds:
            f(bsig,cs,bs,args)
    
        print >> output, bs

        if not conll05 is None:
            if output_mst:
                print >> conll05f, cs.conll05_mst()
                print >> conll05f, ""
            else:
                print >> conll05f, cs.conll05()
                print >> conll05f, ""


        if not conll06 is None:
            if output_mst:
                print >> conll06f, cs.conll06_mst()
                print >> conll06f, ""
            else:
                print >> conll06f, cs.conll06()
                print >> conll06f, ""
 

        if not conll07 is None:
            if output_mst:
                print >> conll07f, cs.conll07_mst()
                print >> conll07f, ""
            else:
                print >> conll07f, cs.conll07()
                print >> conll07f, ""
 

        if not conll08 is None:
            if output_mst:
                print >> conll08f, cs.conll08_mst()
                print >> conll08f, ""
            else:
                print >> conll08f, cs.conll08()
                print >> conll08f, ""
 

    if verbose:
        print >> sys.stderr, ""

    if deffn == None:
        deff = sys.stdout
    else:
        deff = open(deffn,"w")

    bsig.print2file(deff)



