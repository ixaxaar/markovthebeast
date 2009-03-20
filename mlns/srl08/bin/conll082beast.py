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
   --output_mst            Use extra deps for output


   FEATURES
   -f|--features   type   Specify the features to extract
                          original  : the features for Share Task
                          mst_based : the features for Share Task (it
                          doens't include malt deps, but mst) [Default]
                          johanson  : the features from Johanson and Nugues 2008
                             (shared task)

   FORMATING
   -S|--strict            Use strict types on generarion of types [Off]
   -O|--open       File   Uses open format
   -M|--mst_label  File   Uses the MST labelling (conll06 format)
   -P|--ppos_label  File  Uses the PPOS labelling (conll06 format)
   --mst_conll08          Dependencies in 2008 format
   -H|--no_hyphens        It compress the hyphens into one token (as in 2005 format)
   --possible_ON          possiblePredicate based on actual predicate
   --possible_preds       possiblePredicate based on predicate from pred_list (for pipeline system)
   --preds_list    File   preds list to replace base on file (stage 2 of pipeline)
   --per_predicate        Turns on one predicate per instance

   
   MODE
   -N|--none              Generate a NONE role for each token pair without role label
   -G|--gold_deps         Use gold dependencies for mst dependecies
   -T|--test              Testing mode
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
    opts, args = getopt.getopt(sys.argv[1:],"vhrGSHLTO:s:o:d:b:t:P:M:5:6:7:8:f:",["verbose","splits=",
        "output=","def=","strict","open=","begin=","total=","randomize","help","test","conll05=","conll06=","conll08=","conll07=","possible_ON","possible=","preds_list=","s1","s2","s3","per_predicate","mst_conll08","possible_preds","ppos_label=",
        "mst_label=","no_hyphens","output_mst","propbank","nombank","gold_deps","features="])
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
mst_conll08=False
propbank=False
nombank=False


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
    elif opt in ("-S","--strict"):
        strict=True
    elif opt in ("-r","--randomize"):
        randomize=True
    elif opt in ("--possible_ON",):
        impossible.switch_only()
    elif opt in ("--possible_preds",):
        impossible.use_preds()
# Depreciated: use pred_list option
#    elif opt in ("--possible",):
#        preds_file=val
#        impossible.use_preds()
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
preds.append((frame_lemma,[("isPredicate",["Int"]),
                           ("frameLabel",["Int","FrameLabel"])]))



preds.append((voice,("voice",["Int","Voice"])))
preds.append((possiblePredicate,("possiblePredicate",["Int"],[])))
preds.append((possibleArgument,("possibleArgument",["Int"])))

# Preidcates used inf the open format
if features==0 and not open_fmt_file is None:
    # Simple predicates for dependency parses
    preds.append((dependency_link,("m_dep",["Int","Int","MDependency"],"malt_head","malt_dep_rel","m_link")))

    # Path features for depency predicates
    preds.append((depfeatures,("m_path",["Int","Int","MPath"],"malt_head","malt_dep_rel","m_frame",["Int","MFrame"])))

    # Name entity simple predicates
    preds.append((ne,("ne_shared",["Int","NEshared"],"ne_03")))
    preds.append((ne,("ne_bbn",["Int","NEbbn"],"ne_bbn")))

    # WNet predicates
    preds.append((wnet,("wnet",["Int","WNet"],"wnet")))
                            
# Predicates for mst labellings
if (features==1 or features==0) and (not mst_files is None or gold_deps):
    if gold_deps:
        preds.append((dependency_link,("mst_dep",["Int","Int","MDependency"],"head","dep_rel","mst_link")))

        # Path features for depency predicates
        preds.append((depfeatures,("mst_path",["Int","Int","MPath"],"head","dep_rel",
                                   "mst_frame",["Int","MFrame"])))
    else:
        preds.append((dependency_link,("mst_dep",["Int","Int","MDependency"],"mst_head","mst_dep_rel","mst_link")))

        # Path features for depency predicates
        preds.append((depfeatures,("mst_path",["Int","Int","MPath"],"mst_head","mst_dep_rel",
                                   "mst_frame",["Int","MFrame"])))


# Predicates from Johanson (2008 Shared task)
if features==2 and (not mst_files is None or gold_deps):
    # Mst info (including: function feature)
    if gold_deps:
        preds.append((dependency_link,("mst_dep",["Int","Int","MDependency"],"head","dep_rel","mst_link")))
        preds.append((j08_childSets,([("childDepSet",["Int","DepSet"],["dep_rel"]),
                                      ("childWordSet",["Int","WordSet"],["form"]),
                                      ("childWordDepSet",["Int","WordDepSet"],["dep_rel","form"]),
                                      ("childPosSet",["Int","PosSet"],["ppos"]),
                                      ("childPosDepSet",["Int","PosDepSet"],["dep_rel","ppos"])
                                 ],"")))

        # Path features for depency predicates
        preds.append((j08_l_and_r,("leftToken",["Int","Int"],
                                   "rightToken",["Int","Int"],"")))

        # Relative paths
        preds.append((j08_relpath,("relPath",["Int","Int","RelPath"],"",[("RelPath","")])))

        preds.append((j08_verbChains,("verbChainHasSubj",["Int"],
                                      "controllerHasObj",["Int"],"")))

        preds.append((j08_subCat,("depSubCat",["Int","SubCat"],"")))

    else:
        preds.append((dependency_link,("mst_dep",["Int","Int","MDependency"],"mst_head","mst_dep_rel","mst_link")))
        preds.append((j08_childSets,([("childDepSet",["Int","DepSet"],["mst_dep_rel"]),
                                      ("childWordSet",["Int","WordSet"],["form"]),
                                      ("childWordDepSet",["Int","WordDepSet"],["mst_dep_rel","form"]),
                                      ("childPosSet",["Int","PosSet"],["ppos"]),
                                      ("childPosDepSet",["Int","PosDepSet"],["mst_dep_rel","ppos"])
                                 ],"mst_")))

        # Path features for depency predicates
        preds.append((j08_l_and_r,("leftToken",["Int","Int"],
                                   "rightToken",["Int","Int"],"mst_")))

        # Relative paths
        preds.append((j08_relpath,("relPath",["Int","Int","RelPath"],"mst_",[("RelPath","mst_")])))

        preds.append((j08_verbChains,("verbChainHasSubj",["Int"],
                                      "controllerHasObj",["Int"],"mst_")))

        preds.append((j08_subCat,("depSubCat",["Int","SubCat"],"mst_")))

# -------------------------------------------------------------
# Main section
# Prepare splits
corpus = Conll08Corpus(filename,open_fmt_file,mst_files,hyphens=(not no_hyphens),gold_deps=gold_deps)
bsig = TheBeastSignature(strict)
sntcs=range(corpus.size())

# Signature of the sentence
len_sig=3
len_tries=5

preds_possible=[]
preds_replace=[]


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

if mst_conll08:
    mst_ixs=(8,9)
else:
    mst_ixs=(6,7)


# Reads p
ppos_snts=[]
if ppos_files != None:
    for ppos_file in ppos_files:
        ppos=[]
        utt=[]
        collapse=False
        size_collapse=0
        first_collapse=0
        break_collapse=[]
        for line in open(ppos_file):
            line=line.strip()
            if len(line)>0:
                bits=line.split("\t")
                ppos.append((bits[1],bits[4]))
                # Stores a signature for the utterance (dep_rel)
                u=bits[1].replace('-lrb-','(').replace('-rrb-',')')
                utt.append(u)
            else:
                if not slashes:
                    changed=True
                    while changed:
                        changed=False
                        for i in range(len(ppos)):
                            p=ppos[i][1]
                            if p == 'CC' and ppos[i][0]=="/":
                                del ppos[i:i+2]
                                changed=True
                                skip=i+1
                                break
                            
                ppos.append(("_","R"))
                utt.append("ROOT")
                if len_sig==-1:
                    ref=" ".join(utt)
                else:
                    ref=" ".join(utt[:len_sig])
                
                
                ppos_snts.append((ref,[y for x,y in ppos]))
                utt=[]
                ppos=[]

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
                chnk.append((bits[mst_ixs[0]],bits[mst_ixs[1]]))
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
                #cs.replace('ppos',chnk_ppos)
                if not gold_deps:
                    cs.addChnk(chnk)
                else:
                    chnkg=[(w['head'],w['dep_rel']) for w in cs]
                    cs.addChnk(chnkg)
                mst_id+=1
            except IndexError:
                print id,mst_id
                print len(cs), len(mst_snts[mst_id])
                mst_id+=1
                print cs
                continue


        if ppos_files != None:
            # Syncronises the ppos corpus with the conll05 
            try:
                if len_sig==-1:
                    ref=cs.utt(0,"form")
                else:
                    ref=cs.utt(len_sig,"form")
                ref=re_num.sub('<num>',ref)
                
                ppos_ref,ppos=ppos_snts[ppos_id]
                ppos_ref=re_num.sub('<num>',ppos_ref)
            
                if ppos_ref != ref:
                    print >> sys.stderr, "*--%s--(%s)"%(ref,id-1), "", " --%s--"%(ppos_ref)
                    found=False
                    for t in range(len_tries):
                        ppos_ref,ppos=ppos_snts[ppos_id+t]
                        ppos_ref=re_num.sub('<num>',ppos_ref)
                        if ppos_ref == ref:
                            found=True
                            ppos_id+=t
                            break
                    if not found:
                        print >> sys.stderr, "**--%s--(%s)"%(ref,id-1), "", " --%s--"%(ppos_ref)
                        continue
                cs.replace("ppos",ppos)
                ppos_id+=1
            except IndexError:
                print id,ppos_id
                print len(cs), len(ppos_snts[ppos_id])
                ppos_id+=1
                print cs
                continue

   
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



