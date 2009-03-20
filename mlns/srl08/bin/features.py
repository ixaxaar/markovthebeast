#!/usr/bin/python2.4
# --------------------------------------------------------
# Library of funtions to extract predicates from sntc of conll
# --------------------------------------------------------
# --------------------------------------------------------
# Ivan Vladimir Meza-Ruiz
# 2008/Edinburgh
# --------------------------------------------------------

from corpus import *

# Evaluates based on the POS tag of a predicate
def default_predicate(w,args,extra):
    return not w['ppos'] in Impossible.impossiblePredicatePOS 

# Evaluates based on the POS tags of an argument
def default_argument(w,args):
    return not w['s_ppos'] in Impossible.impossibleArgumentPOS

def argument_plemma(w,args):
    return not w['plemma'] in Impossible.impossibleArgumentPOS

def default_argument_ppos(w,args):
    return not w['ppos'] in Impossible.impossibleArgumentPOS

def filler_col(w,args,extra):
    if w["fillpred"] != '_':
        return True
    else:
        return False

def only_predicate(w,args,extra):
    try:
        pred=args[w['id']]
    except KeyError:
        pred=None
    val= not pred is None
    return val

def use_predicates(w,args,extra):
    try:
        pred=extra[w['id']]
    except KeyError:
        pred=None
    val= not pred is None
    return val


f_pred=default_predicate
f_arg=default_argument

class Impossible:
    impossiblePredicatePOS = set([
"``",
"_",
",",
":",
".",
"''",
"(",
")",
"$",
"#",
"CC",
"CD",
"DT",
"EX",
"FW",
"IN",
"JJ",
"JJR",
"JJS",
"LS",
"MD",
"NN",
"NNP",
"NNPS",
"NNS",
"PDT",
"POS",
"PRP",
"PRP$",
"RB",
"RBR",
"RBS",
"RP",
"SYM",
"TO",
"UH",
"WDT",
"WP",
"WP$",
"WRB",
"R",
"ROOT"])
    impossibleArgumentPOS = set(["DT", "ROOT",".",",","''","``",":",";", "$"])

    def  __init__(self):
        self.test_pred=default_predicate
        self.test_arg=default_argument

    def default(self):
        self.test_pred=default_predicate

    def switch_argument_ppos(self):
        self.test_arg=default_argument_ppos

    def switch_argument_plemma(self):
        self.test_arg=argument_plemma

    def switch_filler_col(self):
        self.test_pred=filler_col

    def switch_only(self):
        self.test_pred=only_predicate

    def use_preds(self):
        self.test_pred=use_predicates
 
     
impossible=Impossible()


def writeFrameAsAtoms(sig, bs, frameName, frameType,all, labels = True):
    for start in all[0].keys():
        neighbors = all[0][start]
        result = "\""
        ##print start
        ##print neighbors
        sorted = neighbors.keys()
        sorted.sort()
        #neighbors.keys().sort()
        for end in sorted:
            result += ("<", ">")[end < start]
            edge = neighbors[end][0]
            result += ("^", "v")[edge[0] == end]
            if labels:
                result += edge[2]
        
        result += "\""

        bs.addPred(frameName, (str(start), result))
        sig.addType(frameType[1], result)

def writePathsAsAtoms(sig, bs, predName, typeName, allSizes,label=True):
    for start in allSizes.keys():
        for end in allSizes[start].keys():
            for path in allSizes[start][end]:
                asString = pathAsString(start, path, label)
                bs.addPred(predName, (str(start), str(end), asString))
                sig.addType(typeName, asString)

def writePathsLengths(sig, bs, predName, allSizes):
    for start in allSizes.keys():
        for end in allSizes[start].keys():
            for path in allSizes[start][end]:
                bs.addPred(predName, (str(start), str(end), str(len(path))))

def combinePaths(all):
    allSizes = {}
    for pathsForSize in all:
        for start in pathsForSize.keys():
            if not allSizes.has_key(start):
                allSizes[start] = {}
            
            for end in pathsForSize[start].keys():
                if not allSizes[start].has_key(end):
                    allSizes[start][end] = []
                
                allSizes[start][end].append(pathsForSize[start][end])
            
        
    
    return allSizes

def single_preds(sig,cs,bs,args,extra):
    for name,type,attr in args:
           sig.addDef(name,type)

    for w in cs:
        for name,type,attr in args:
            val=normalize_entry(w[attr])
            bs.addPred(name,(w["id"],val))
            sig.addType(type[1],val)

def getDeps(sntc,label):
    deps=[(w["id"],w[label+"head"]) for w in sntc]
    tree={}
    
    for d,h in deps:
        try:
            tree[d].append(h)
        except KeyError:
            tree[d]=[h]
    return tree


def getTree(sntc,label):
    deps=[(w["id"],w[label+"head"]) for w in sntc]
    tree={}
    
    for d,h in deps:
        try:
            tree[h].append(d)
        except KeyError:
            tree[h]=[d]
    return tree

def get_span(tree,id,terminals=[],id_ori=-1):
    if id_ori==-1:
        id_ori=id
    try:
        tree[id]
    except KeyError:
        return terminals
    for d in tree[id]:
        if d!=id: # and d!=id_ori:
            terminals.append(d)
            terminals=get_span(tree,d,terminals,id_ori)
    return terminals


def j08_l_and_r(sig,cs,bs,args,extra):
    lt,ltt,rt,rtt,label=args
    sig.addDef(lt,ltt)
    sig.addDef(rt,rtt)

    tree=getTree(cs,label)
    for w in cs:
        terminals=get_span(tree,w["id"],[])
        if len(terminals) == 0:
            terminals=[w["id"]]
        terminals=[int(t) for t in terminals]
        terminals.sort()
        bs.addPred(lt,(w["id"],cs[terminals[0]]["id"]))
        bs.addPred(rt,(w["id"],cs[terminals[-1]]["id"]))


def getSet(cs,tree,labels):
    vals=[]
    for w in cs:
        try:
            childs=tree[w["id"]]
            set={}
            for c in childs:
                label=[]
                for l in labels:
                    label.append(cs[int(c)][l])
                set["-".join(label)]=1
            set=set.keys()
            set.sort()
            val="-".join(set)
            vals.append((w["id"],normalize_entry(val)))
        except KeyError:
            pass
    return vals


def j08_childSets(sig,cs,bs,argss,extra):
    argss,label=argss
    tree=getTree(cs,label)
    for args in argss:
        name,type,labels=args
        sig.addDef(name,type)
        vals=getSet(cs,tree,labels)        
        for t in vals:            
            bs.addPred(name,t)
            sig.addType(type[1],t[1])

def switch(x):
    if x.startswith(">"):
        return "<"
    elif x.startswith("<"):
        return ">"
    else:
        return x

def reverse(p):
    n=[switch(x) for x in p]
    n.reverse()
    return n

def create_path(cs,path,label):
    n=[]
    prev=path[0]
    i=1
    while i < len(path[1:]):
        n.append(path[i])
        i+=1
        if path[i-1].startswith(">"):
            n.append(cs[int(prev)][label])
        else:
            n.append(cs[int(path[i])][label])
        prev=path[i]
        i+=1
    return "".join(n)
            
def j08_verbChains(sig,cs,bs,args,extra):
    name1,type1,name2,type2,label=args
    tree=getTree(cs,label)
    dpreds=dict(cs.preds)
    sig.addDef(name1,type1)
    sig.addDef(name2,type2)
    for w1 in range(1,len(cs)):
        w=cs[w1]
        if impossible.test_pred(w,dpreds,extra):
            head=cs[int(w[label+"head"])]
            while head[label+"dep_rel"]=="VC":                
                head=cs[int(head[label+"head"])]
            chld=[cs[int(c)][label+"dep_rel"]  for c in tree[head["id"]]]
            pos_chld=[cs[int(c)]["ppos"]  for c in tree[head["id"]]]
            both_chld=zip(chld,pos_chld)
            if "SBJ" in chld:
                bs.addPred(name1,(str(w1),))
            elif ("DEP","NN") in both_chld or ("DEP","PRP") in both_chld:
                bs.addPred(name1,(str(w1),))
                
            if head[label+"dep_rel"]=="OPRD":
                head=cs[int(head[label+"head"])]
                chld=[cs[int(c)][label+"dep_rel"]  for c in tree[head["id"]]]
                if "OBJ" in chld:
                    bs.addPred(name2,(str(w1),))


def j08_subCat(sig,cs,bs,args,extra):
    name1,type1,label=args
    tree=getTree(cs,label)
    dpreds=dict(cs.preds)
    sig.addDef(name1,type1)
    for w1 in range(1,len(cs)):
        w=cs[w1]
        if impossible.test_pred(w,dpreds,extra):
            head=cs[int(w[label+"head"])]
            if tree.has_key(w["id"]):
                chld=[cs[int(c)][label+"dep_rel"]  for c in tree[w["id"]]]
                p = normalize_entry("-".join(chld))
                bs.addPred(name1,(str(w1),p))
                sig.addType(type1[1],p)



def j08_relpath(sig,cs,bs,args,extra):
    name,type,label,info=args
    tree=getTree(cs,label)
    sig.addDef(name,type)
    dpreds=dict(cs.preds)

    paths_={}
    # paths 0 distance
    paths=[]
    paths.append([])
    for w1 in range(len(cs)):
        paths[-1].append([((w1,w1),[])])
    # Find the minimum paths bottom-up
    for size in range(1,len(cs)):
        # prev+1
        paths.append([])
        for w1 in range(len(cs)):
            final=[]            
            if tree.has_key(str(w1)):
                for c in tree[str(w1)]:
                    for (w2,ini),p in paths[size-1][int(c)]:
                        if w2 != 0:
                                if len(p)>0:
                                    final.append(((int(c),ini),[c,"<"]+p))
                                else:
                                    final.append(((int(c),ini),[c]))
            paths[size].append(final)
        for l in paths[size]:
            for pos,path in l:
                f,i=pos
                if paths_.has_key(pos):
                    if len(paths_[pos])<len(path):
                        paths_[pos]=path
                else:
                        paths_[pos]=path
                pos=(i,f)
                if paths_.has_key(pos):
                    if len(paths_[pos])<len(path):
                        paths_[pos]=reverse(path)
                else:
                        paths_[pos]=reverse(path)

    for w1 in range(1,len(cs)):
        if not impossible.test_pred(cs[w1],dpreds,extra):
           continue
        for w2 in range(1,len(cs)):
            if not impossible.test_arg(cs[w2],None):
                continue
            if w2 == w1:
                continue
            if paths_.has_key((w1,w2)):
                for typ,inf in info:
                    p = normalize_entry(create_path(cs,paths_[(w1,w2)],inf+"dep_rel"))
                    bs.addPred(name,(str(w1),str(w2),p))
                    sig.addType(typ,p)
            else:
                new_path=[i for i in range(1000)]
                for mid in range(len(cs)):
                    if mid==w1:
                        continue
                    if mid==w2:
                        continue
                    tmp=[]
                    if w1 > mid and w2 > mid and paths_.has_key((mid,w1)) and paths_.has_key((mid,w2)):
                                tmp=paths_[(mid,w1)]+["&"]+paths_[(mid,w2)][2:]
                    if w1 < mid and w2 > mid and paths_.has_key((w1,mid)) and paths_.has_key((mid,w2)):
                                tmp=paths_[(w1,mid)]+["&"]+paths_[(mid,w2)][2:]
                    if w1 > mid and w2 < mid and paths_.has_key((mid,w1)) and paths_.has_key((w2,mid)):
                                tmp=paths_[(mid,w1)]+["@"]+paths_[(w2,mid)][2:]
                    if w1 < mid and w2 < mid and paths_.has_key((w1,mid)) and paths_.has_key((w2,mid)):
                                tmp=paths_[(w1,mid)]+["@"]+paths_[(w2,mid)][2:]
                    if len(tmp)>1 and len(tmp) < len(new_path):
                       new_path=tmp
                if len(new_path)!=1000:
                    #print "*",w1,w2,new_path
                    for typ,inf in info:
                        p = normalize_entry(create_path(cs,new_path,inf+"dep_rel"))
                        bs.addPred(name,(str(w1),str(w2),p))
                        sig.addType(typ,p)
                else:
                    print "EEEEEEEEE"

#    for w1 in range(1,len(cs)):
#        for w2 in range(w1+1,len(cs)):

def feats(sig,cs,bs,args,extra):
    name,type,label=args
    sig.addDef(name,type)
    sig.addType(type[1],'"NONE"')
    sig.addType(type[2],'"NONE"')
    for w in cs:
        feats=w[label].split('|')
        for fv in feats:
            if not fv == "_" and len(fv)>0:
                fv=fv.strip()
                bits=fv.split('=')
                if len(bits) > 1:
                    f=normalize_entry(bits[0])
                    v=normalize_entry(bits[1])
                else:
                    f=normalize_entry("gen")
                    v=normalize_entry(bits[0])
                if v != '"*"':
                    bs.addPred(name,(w["id"],f,v))
                    sig.addType(type[1],f)
                    sig.addType(type[2],v)

                
def cpos(sig,cs,bs,args,extra):
    if len(args) ==2:
        name,type = args
        ppos="ppos"
    else:
        name,type,ppos = args
    sig.addDef(name,type)
    for w in cs:
            val=normalize_entry(w[ppos][0])
            # In case there is not ppos, then we use gpos
            bs.addPred(name,(w["id"],val))
            sig.addType(type[1],val)

def possiblePredicate(sig,cs,bs,args,extra_d):
    name,type,extra = args
    sig.addDef(name,type)
    dpreds=dict(cs.preds)
    for w in cs:
            if impossible.test_pred(w,dpreds,extra_d):
                bs.addPred(name,[w["id"]])

def possibleArgument(sig,cs,bs,args,extra):
    name,type = args
    sig.addDef(name,type)
    for w in cs:
            if impossible.test_arg(w,None):
                bs.addPred(name,[w["id"]])


def ne(sig,cs,bs,args,extra):
    name,type,ne_label = args
    sig.addDef(name,type)
    for w in cs:
        if w[ne_label] != "0":            
            val=normalize_entry(w[ne_label])
            bs.addPred(name,(w["id"],val))
            sig.addType(type[1],val)

def wnet(sig,cs,bs,args,extra):
    name,type,wnet_label = args
    sig.addDef(name,type)
    for w in cs:
        if w["id"]!="0":
            val=normalize_entry(w[wnet_label])
            bs.addPred(name,(w["id"],val))
            sig.addType(type[1],val)

def voice(sig,cs,bs,args,extra):
    '''Extracts voice'''
    if len(args) == 2:
        name,type = args
        pre="s_"
    else:
        name,type,pre = args


    st = 0 # No verb found
    sig.addDef(name,type)
    for w in cs:
        if st == 0:
            if w[pre+"ppos"].startswith('V'):
                if w[pre+"form"][0].islower():
                    if w[pre+"ppos"].startswith('VBD'):
                        st = 1 # Next VBN is passive (unless is be)
                    elif  w[pre+"ppos"].startswith("VBZ"):
                        st = 1 # Next VBN is passive (unless is be)
                    elif w[pre+"ppos"]=='VB':
                        if w[pre+"lemma"].startswith('be') or\
                                w[pre+"lemma"].startswith('have'):
                            st = 1 # Next VBN is passive (unless is be)
                    bs.addPred(name,(w["id"],'"act"'))            


        elif st == 1:
            if w[pre+"ppos"].startswith('VBN'):
                bs.addPred(name,(w["id"],'"pas"'))
                if not w[pre+'lemma'].startswith("be"):
                    st=0 # Ready for next
            elif w[pre+"ppos"].startswith('VB'):
                bs.addPred(name,(w["id"],'"act"'))            
            elif not w[pre+"ppos"] in ['RB','IN','VBN']:
                st = 0 # It wasn't wort to check
    sig.addType(type[1],'"act"')
    sig.addType(type[1],'"pas"')



def voice_catalan_spanish(sig,cs,bs,args,extra):
    '''Extracts voice for spanish'''
    name,type = args
    sig.addDef(name,type)
    for w in cs:
        if w['ppos'].startswith('v'):
            pfeat=w["pfeat"]
            try:
                pfeat.index("mood=pastparticiple")
                bs.addPred(name,(w["id"],'"pas"'))
            except ValueError:
                bs.addPred(name,(w["id"],'"act"'))
    sig.addType(type[1],'"act"')
    sig.addType(type[1],'"pas"')


def voice_czech(sig,cs,bs,args,extra):
    '''Extracts voice for czech'''
    name,type = args
    sig.addDef(name,type)
    for w in cs:
        if w['ppos'].startswith('V'):
            pfeat=w["pfeat"]
            try:
                pfeat.index("Voi=P")
                bs.addPred(name,(w["id"],'"pas"'))
            except ValueError:
                bs.addPred(name,(w["id"],'"act"'))
    sig.addType(type[1],'"act"')
    sig.addType(type[1],'"pas"')


def voice_german(sig,cs,bs,args,extra):
    '''Extracts voice for czech'''
    name,type = args
    sig.addDef(name,type)
    for w in cs:
        if w['ppos'].startswith('VAPP'):
                bs.addPred(name,(w["id"],'"pas"'))
        else:
                bs.addPred(name,(w["id"],'"act"'))
    sig.addType(type[1],'"act"')
    sig.addType(type[1],'"pas"')

def no_voice(sig,cs,bs,args,extra):
    '''Extracts voice for chinese'''
    name,type = args
    sig.addDef(name,type)
    sig.addType(type[1],'"act"')
    sig.addType(type[1],'"pas"')





def extendPaths(lengthNPaths, length1Paths, undirected):
    """ Extends the paths in lengthNPaths by using the edges in length1Path """
    longerPaths = {}
    for start in lengthNPaths.keys():
        for middle in lengthNPaths[start].keys():
            if length1Paths.has_key(middle):
                for end in length1Paths[middle].keys():
                    first = lengthNPaths[start][middle]
                    second = length1Paths[middle][end]
                    if not second[0] in first and not start == end:
                        path = lengthNPaths[start][middle]+length1Paths[middle][end]
                        if not longerPaths.has_key(start):
                            longerPaths[start] = {}
                        if not longerPaths.has_key(end) and undirected:
                            longerPaths[end] = {}                            
                        longerPaths[start][end] = path
                        if undirected:
                            longerPaths[end][start] = path
    return longerPaths
    

def depfeatures(sig,cs,bs,args,extra):
    """ creates all dependency paths between all tokens
        also creates frames 
    """
    name, type, head_label, dep_rel_label, frameName, frameType = args
    nameDirected = name + "_directed"
    nameUnlabeled = name + "_unlabeled"
    namePathFrame = name + "_frame"
    namePathFrameUnlabeled = name + "_frame_unlabeled"
    nameUnlabeledFrame = frameName + "_unlabeled"
    nameLength = name + "_length"
    namePalmerPruned = "palmer"
    namePathFrameDistance = "path_frame_distance"
    typePathFrameDistance = ["Int","Int","Int"]
    typeLength = ["Int","Int","Int"]
    typeUnlabeled = ["Int","Int","MPathUnlabeled"]
    typePathFrame = ["Int","Int","MPathFrame"]
    typePathFrameUnlabeled = ["Int","Int","MPathFrameUnlabeled"]
    typeUnlabeledFrame = ["Int", "MFrameUnlabeled"]
    typePalmerPruned = ["Int","Int"]
    
    sig.addDef(name,type)
    sig.addDef(nameDirected,type)
    sig.addDef(frameName,frameType)
    sig.addDef(nameLength, typeLength)
    sig.addDef(nameUnlabeled,typeUnlabeled)
    sig.addDef(nameUnlabeledFrame, typeUnlabeledFrame)
    sig.addDef(namePathFrame, typePathFrame)
    sig.addDef(namePathFrameUnlabeled, typePathFrameUnlabeled)
    sig.addDef(namePalmerPruned, typePalmerPruned)
    sig.addDef(namePathFrameDistance, typePathFrameDistance)
    paths = {}
    pathsDirect = {}
    #create length 1 symmetric incidence matrix
    for w in cs:
        if w["id"]!="0":
            val=w[dep_rel_label]
            head = int(w[head_label])            
            mod = int(w["id"])
            if not paths.has_key(head):
                paths[head] = {}
            if not paths.has_key(mod):
                paths[mod] = {}
            if not pathsDirect.has_key(head):
                pathsDirect[head] = {}
            paths[head][mod] = [(head,mod,val)]
            paths[mod][head] = [(head,mod,val)]
            pathsDirect[head][mod] = [(head,mod,val)]
    lengthOne = paths  
    lengthOneDirect = pathsDirect
    all = [] 
    #create new paths with length 2, then 3, then 4 etc. 
    #right now we assume no cycles -> only one path
    while len(paths) > 0 and len(all) < 4:
        all.append(paths)
        longerPaths = extendPaths(paths, lengthOne, True)
        paths = longerPaths
    #create directed paths
    allDirect = []   
    while len(pathsDirect) > 0 and len(allDirect) < 10:
        allDirect.append(pathsDirect)
        longerPathsDirect = extendPaths(pathsDirect, lengthOneDirect, False)
        pathsDirect = longerPathsDirect

    #combine paths of all sizes
    allSizes = combinePaths(all)
    allSizesDirect = combinePaths(allDirect)

    #write out the atom for each path
    writePathsAsAtoms(sig, bs, name, type[2], allSizes)
    writePathsAsAtoms(sig, bs, nameUnlabeled, typeUnlabeled[2], allSizes, False)
    writePathsAsAtoms(sig, bs, nameDirected, type[2] , allSizesDirect)
                
    #write path length
    writePathsLengths(sig, bs, nameLength, allSizes)            
                
    #now let's do the frames using the paths with length 1
    writeFrameAsAtoms(sig, bs, frameName, frameType,all)
    writeFrameAsAtoms(sig, bs, nameUnlabeledFrame, typeUnlabeledFrame,all, False)
    
    # the frame of the path between predicate and argument
    writePathFrameAsAtoms(sig, bs , namePathFrame, typePathFrame, lengthOneDirect , allSizes)
    writePathFrameAsAtoms(sig, bs , namePathFrameUnlabeled, typePathFrameUnlabeled, lengthOneDirect , allSizes, False)
            
    # write the path frame distance        
    writePathFrameAsAtoms(sig, bs , namePathFrameDistance, typePathFrameDistance, lengthOneDirect , allSizes, False, True)
            
    # write candidates after palmer prune
    root = 0;
    for end in allSizesDirect[root].keys():
        # we add the possibility that predicates can be their own arguments
        path = allSizesDirect[root][end][0] + [(end,end,'dummy')]
        for (head,mod,label) in path:
            if lengthOneDirect.has_key(head):
                for child in lengthOneDirect[head].keys():
                    bs.addPred(namePalmerPruned, (str(end), str(child)))
                    if lengthOneDirect.has_key(child):
                        for grandchild in lengthOneDirect[child].keys():
                            (h,m,l) = lengthOneDirect[head][child][0]
                            if l == "PMOD":
                                bs.addPred(namePalmerPruned, (str(end),str(grandchild)))
        
                  
            
def writePathFrameAsAtoms(sig, bs, name, type, graph, paths, labels = True, distance = False):
    for start in paths.keys():
        for end in paths[start].keys():
            path = paths[start][end][0]
            # iterate over path and print out children of nodes on path in order
            unsorted = set([(start,"!"),(end,"?")])
            for (head,mod,label) in path:
                for child in graph[head].keys():
                    edge = graph[head][child][0]
                    unsorted.add((edge[1],edge[2]))
            # sort list
            sorted = list(unsorted)
            sorted.sort()
            if distance:
                index = 0
                for (token,label) in sorted:
                    if label == "!":
                        indexStart = index
                    else: 
                        if label == "?":
                            indexEnd = index
                    index = index + 1
                result = str(indexEnd - indexStart);
            else:
                result = "\""
                for (token,label) in sorted:
                    if labels or label == "?" or label == "!":
                        result += label + " "
                    else:
                        result += "." 
                result += "\""
            bs.addPred(name, (str(start), str(end),result))
            if not distance:
                sig.addType(type[2], result)
                        
        
        
def pathAsString(start, path, labels = True):
    """ return a string representation for the given path starting at the specified start
        node """
    first = path[0]
    last = path[len(path)-1]
    if start in last:
        path.reverse()
    previous = start
    result = "\""
    for (head,mod,label) in path:
        if head == previous:
            result += "v"
        else:
            result += "^"
        if labels:
            result += label
        previous = mod
    result += "\""
    return result             
    

# Depreciated don't use with out checking the args of dependecy_links
def dependency(sig,cs,bs,args,extra):
    name,type = args
    sig.addDef(name,type)
    for w in cs:
        if w["id"]!="0":
            val=normalize_entry(w["dep_rel"])
            bs.addPred(name,(w["head"],w["id"], val))
            sig.addType(type[2],val)


def dependency_link(sig,cs,bs,args,extra):
    name,type,head_label,dep_rel_label,link_name= args
    sig.addDef(name,type)
    sig.addDef(link_name,["Int","Int"])
    for w in cs:
        if w["id"]!="0":
            val=normalize_entry(w[dep_rel_label])
            bs.addPred(name,(w[head_label],w["id"], val))
            bs.addPred(link_name,(w[head_label],w["id"]))
            sig.addType(type[2],val)

def role_argument_label(sig,cs,bs,args,extra):
    '''Extracts the roles featues, it adds a predicate isArgument, if a token
    is a role, and hasLabel(i,j) if a token i has a role in the token(j)'''
    name,type,mode_label = args[0]
    name_isArgument,type_isArgument = args[1]
    name_hasLabel,type_hasLabel = args[2]
    sig.addDef(name,type)
    sig.addDef(name_isArgument,type_isArgument)
    sig.addDef(name_hasLabel,type_hasLabel)
    ixs=[]
    isArg={}
    for i in range(len(cs.preds)):
        ix,val = cs.preds[i]
        ixs.append(ix)
        jxs=[]
        if len(cs.args) > 0:
            for arg in cs.args[i]:
                jx,rolev= arg
                jxs.append(jx)
                val = normalize_entry(rolev)
                bs.addPred(name,(str(ix),str(jx),val))
                sig.addType(type[2],val)
                isArg[jx]=1
                bs.addPred(name_hasLabel,(str(ix),str(jx)))
        else:
                bs.addPred(name,(str(ix),str(ix),'"NONE"'))
                bs.addPred(name_hasLabel,(str(ix),str(ix)))
                sig.addType(type[2],'"NONE"')
            
    for arg,k in isArg.iteritems():
        bs.addPred(name_isArgument,(str(arg),))



def role_none(sig,cs,bs,args,extra):
    '''Extracts the role predicates, and add a NONE label in case doesn't
    exists, it has three modes:
        ALL = generates all NONEs
        FRAMES = generates NONES for only frames
        NOT = it doesn't generates NONES' '''
    name,type,mode_label = args
    if mode_label=="NOT" :
        mode=0
    elif mode_label=="FRAMES":
        mode=1
    else:
        mode=2
    sig.addDef(name,type)
    ixs=[]
    for i in range(len(cs.preds)):
        ix,val = cs.preds[i]
        ixs.append(ix)
        jxs=[]
        for arg in cs.args[i]:
            jx,rolev= arg
            jxs.append(jx)
            val = normalize_entry(rolev)
            bs.addPred(name,(str(ix),str(jx),val))
            sig.addType(type[2],val)
        if mode>0:
            val = normalize_entry("NONE")
            for jx in [str(x) for x in range(len(cs))]:
                if not jx in jxs:
                    bs.addPred(name,(str(ix),str(jx),val))
            sig.addType(type[2],val)
    if mode>1:
        val = normalize_entry("NONE")
        for ix in [str(x) for x in range(len(cs))]:
            if not ix in ixs:
                for jx in [str(x) for x in range(len(cs))]:
                    bs.addPred(name,(str(ix),str(jx),val))
        sig.addType(type[2],val)



# Depreciated, now labeling roles which aren't present with NONE
def role(sig,cs,bs,args,extra):
    name,type = args
    sig.addDef(name,type)
    for i in range(len(cs.preds)):
        ix,val = cs.preds[i]
        for arg in cs.args[i]:
            jx,rolev= arg
            val = normalize_entry(rolev)
            bs.addPred(name,(str(ix),str(jx),val))
            sig.addType(type[2],val)



# Depreciated, now using lemma as predicate
def frame(sig,cs,bs,args,extra):
    name,type = args
    sig.addDef(name,type)
    for i in range(len(cs.preds)):
        ix,framev = cs.preds[i]
        val = normalize_entry(framev)
        bs.addPred(name,(str(ix),val))
        sig.addType(type[1],val)

def frame_lemma(sig,cs,bs,args,extra):
    '''Extracts frames using relying the lemma is the predicate'''
    name_pred,type_pred = args[0]
    name_label,type_label = args[1]
    sig.addDef(name_label,type_label)
    sig.addDef(name_pred,type_pred)
    for i in range(len(cs.preds)):
        ix,framev = cs.preds[i]
        label = framev.split(".")
        val = normalize_entry(label[-1])
        bs.addPred(name_pred,(str(ix),))
        bs.addPred(name_label,(str(ix),val))
        sig.addType(type_label[1],val)


def sense(sig,cs,bs,args,extra):
    '''Extracts frames using relying the lemma is the predicate'''
    name_label,type_label = args[0]
    sig.addDef(name_label,type_label)
    for i in range(len(cs.preds)):
        ix,framev = cs.preds[i]
        label = framev.split(".")
        if len(label)>1:
            val = normalize_entry(label[-1])
        else:
            val = "NONE"
        bs.addPred(name_label,(str(ix),val))
        sig.addType(type_label[1],val)



def frame_lemma2(sig,cs,bs,args,extra):
    '''Extracts frames using relying the lemma is the predicate'''
    name_pred,type_pred = args[0]
    name_label,type_label = args[1]
    preds=args[2]
    sig.addDef(name_label,type_label)
    sig.addDef(name_pred,type_pred)
    for ix in preds:
        bs.addPred(name_pred,(ix,))

