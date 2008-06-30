#!/usr/bin/python2.4
# --------------------------------------------------------
# Library of funtions to extract predicates from sntc of conll
# --------------------------------------------------------
# --------------------------------------------------------
# Ivan Vladimir Meza-Ruiz
# 2008/Edinburgh
# --------------------------------------------------------

from corpus import *


impossiblePredicatePOS = set(["DT","IN", "JJ", "MD", "RB", "ROOT","CC", "RBS", "CD", ".",",","''","``",":",";","$"])
impossibleArgumentPOS = set(["DT", "ROOT",".",",","''","``",":",";", "$"])

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

def single_preds(sig,cs,bs,args):
    for name,type,attr in args:
           sig.addDef(name,type)

    for w in cs:
        for name,type,attr in args:
            val=normalize_entry(w[attr])
            bs.addPred(name,(w["id"],val))
            sig.addType(type[1],val)

def cpos(sig,cs,bs,args):
    name,type = args
    sig.addDef(name,type)
    for w in cs:
            val=normalize_entry(w["ppos"][0])
            bs.addPred(name,(w["id"],val))
            sig.addType(type[1],val)

def possiblePredicate(sig,cs,bs,args):
    name,type = args
    sig.addDef(name,type)
    for w in cs:
            pos = w["ppos"]
            if not pos in impossiblePredicatePOS:
                bs.addPred(name,[w["id"]])

def possibleArgument(sig,cs,bs,args):
    name,type = args
    sig.addDef(name,type)
    for w in cs:
            pos = w["ppos"]
            if not pos in impossibleArgumentPOS:
                bs.addPred(name,[w["id"]])


def ne(sig,cs,bs,args):
    name,type,ne_label = args
    sig.addDef(name,type)
    for w in cs:
        if w[ne_label] != "0":            
            val=normalize_entry(w[ne_label])
            bs.addPred(name,(w["id"],val))
            sig.addType(type[1],val)

def wnet(sig,cs,bs,args):
    name,type,wnet_label = args
    sig.addDef(name,type)
    for w in cs:
        if w["id"]!="0":
            val=normalize_entry(w[wnet_label])
            bs.addPred(name,(w["id"],val))
            sig.addType(type[1],val)

def voice(sig,cs,bs,args):
    '''Extracts voice'''
    name,type = args
    st = 0 # No verb found
    sig.addDef(name,type)
    for w in cs:
        if st == 0:
            if w["s_ppos"].startswith('V'):
                if w["s_form"][0].islower():
                    if w["s_ppos"].startswith('VBD'):
                        st = 1 # Next VBN is passive (unless is be)
                    elif  w["s_ppos"].startswith("VBZ"):
                        st = 1 # Next VBN is passive (unless is be)
                    elif w["s_ppos"]=='VB':
                        if w["s_lemma"].startswith('be') or\
                                w["s_lemma"].startswith('have'):
                            st = 1 # Next VBN is passive (unless is be)
                    bs.addPred(name,(w["id"],'"act"'))            


        elif st == 1:
            if w["s_ppos"].startswith('VBN'):
                bs.addPred(name,(w["id"],'"pas"'))
                if not w['s_lemma'].startswith("be"):
                    st=0 # Ready for next
            elif w["s_ppos"].startswith('VB'):
                bs.addPred(name,(w["id"],'"act"'))            
            elif not w["s_ppos"] in ['RB','IN','VBN']:
                st = 0 # It wasn't wort to check
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
    

def depfeatures(sig,cs,bs,args):
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
def dependency(sig,cs,bs,args):
    name,type = args
    sig.addDef(name,type)
    for w in cs:
        if w["id"]!="0":
            val=normalize_entry(w["dep_rel"])
            bs.addPred(name,(w["head"],w["id"], val))
            sig.addType(type[2],val)


def dependency_link(sig,cs,bs,args):
    name,type,head_label,dep_rel_label,link_name= args
    sig.addDef(name,type)
    sig.addDef(link_name,["Int","Int"])
    for w in cs:
        if w["id"]!="0":
            val=normalize_entry(w[dep_rel_label])
            bs.addPred(name,(w[head_label],w["id"], val))
            bs.addPred(link_name,(w[head_label],w["id"]))
            sig.addType(type[2],val)

def role_argument_label(sig,cs,bs,args):
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
        for arg in cs.args[i]:
            jx,rolev= arg
            jxs.append(jx)
            val = normalize_entry(rolev)
            bs.addPred(name,(str(ix),str(jx),val))
            sig.addType(type[2],val)
            isArg[jx]=1
            bs.addPred(name_hasLabel,(str(ix),str(jx)))
    for arg,k in isArg.iteritems():
        bs.addPred(name_isArgument,(str(arg),))



def role_none(sig,cs,bs,args):
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
def role(sig,cs,bs,args):
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
def frame(sig,cs,bs,args):
    name,type = args
    sig.addDef(name,type)
    for i in range(len(cs.preds)):
        ix,framev = cs.preds[i]
        val = normalize_entry(framev)
        bs.addPred(name,(str(ix),val))
        sig.addType(type[1],val)

def frame_lemma(sig,cs,bs,args):
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

