#!/usr/bin/python2.4
# --------------------------------------------------------
# Library to deal with corpora
# --------------------------------------------------------
# Ivan Vladimir Meza-Ruiz
# 2008/Edinburgh
# --------------------------------------------------------

import re

re_space=re.compile(r'\s+')


# Coll08 corpus class
class Conll08Corpus:
    """It handles a corpus in the Conll08 format"""

    # Extra line for root elements
    e_line = "0\tROOT\tROOT\tROOT\tROOT\tROOT\tROOT\t0\t0\t_\t_"
    e_open_line = "0\t0\tROOT\tROOT\tROOT"
    use_eline = True

    def __init__(self,filename=None,ofilename=None,efilename=None,hyphens=True):
        """Initilize the handler
            filename is the name of the file where the corpus is located
            ofilename is the name of the file where the extra information is\
                    available"""
        # It doesn't saves the corpus in memory anymore
        #self.sntcs=[ ]
        self.ix=None
        self.len=0
        self.filename=filename
        self.ofilename=ofilename
        file = None
        ofile = None
        self.hyphens=hyphens
        if not filename is None:
            file = open(filename,'r')
            if not ofilename is None:
                ofile = open(ofilename,'r')
            chnk = self.readChunk(file,ofile)
            i = 0
            while len(chnk[0])>0 :
                i+=1
                chnk = self.readChunk(file,ofile,efilename)
        self.len = i
        file.close()
        if not ofile is None:
            ofile.close()

    def readChunk(self,file,ofile=None,efilename=None):
        lines = []
        olines = []
        line = file.readline()
        if not ofile is None:
            oline = ofile.readline()
            oline= oline.strip()
        while len(line) > 1 :
            line=line.strip()
            lines.append(line)
            if not ofile is None:
                olines.append(oline)
            line = file.readline()
            if not ofile is None:
                oline = ofile.readline()
                oline= oline.strip()
        return (lines,olines)

    def size(self):
        return self.len

    def __iter__(self):
        self.file = open(self.filename,'r')
        self.ofile = None
        if not self.ofilename is None:
            self.ofile= open(self.ofilename,'r')
        return self

    def next(self):
        chnk = self.readChunk(self.file,self.ofile)
        if len(chnk[0])>0 :
            return self.chnk2sntc(chnk)
        else:
            self.file.close()
            if not self.ofile is None:
                self.ofile.close()            
            raise StopIteration 


    def chnk2sntc(self,chnk):
        """Translate a chnk into a sentence"""

        lines,olines = chnk
        if Conll08Corpus.use_eline:
            size_line=len(re_space.split(lines[0]))
            extra="\t".join(["_" for i in range(size_line-11)])
            if len(extra)>0:
                lines.append(Conll08Corpus.e_line+"\t"+extra);
            else:
                lines.append(Conll08Corpus.e_line);
            if not self.ofile is None:
                olines.append(Conll08Corpus.e_open_line)
        return Conll08Sntc(lines,olines,self.hyphens)






class Conll08Sntc:
    def __init__(self,lines=None,olines=[],hyphens=True):
        self.sntc=[]
        self.preds=[]
        self.args=[]
        
        if  not lines is None:
            # Main lines
            self.createFromLines(lines,olines)
            preds=[]    
            # SRL labels            
            args=[[] for i in re_space.split(lines[0])[11:]]
            for i in range(len(lines)):
                line = re_space.split(lines[i])[10:]
                ix = re_space.split(lines[i])[0]
                if len(line)>0 and line[0] != '_':
                    preds.append((ix,line[0]))
                for j in range(1,len(line)):
                    if line[j] != '_':
                        args[j-1].append((ix,line[j]))
            self.preds=preds
            self.args=args

            # Compressing in case no hyphens
            if not hyphens:
                sntc=[]
                flag_change=True
                while flag_change:
                    flag_change=False
                    skip=0
                    size=0
                    inside=False
                    for i in range(len(self.sntc)-1):
                        w=self.sntc[i]
                        if not inside and w['form']=="_" and w["gpos"]=="_":
                            skip=i
                            size=1
                            flag_change=True
                            inside=True
                        if inside and  w['form']=="_" and w["gpos"]=="_":
                            self.sntc[i-size]['lemma']+=w['lemma']
                            size+=1
                        elif inside:
                            inside=False
                            break
                    if flag_change:
                        del self.sntc[skip:(skip+size-1)]
                        for w in self.sntc:
                            if int(w['id'])>skip:
                                w['id']=str(int(w['id'])-size+1)
                            if w['head']!='_' and int(w['head']) > skip:
                                w['head']=str(int(w['head'])-size+1)
                        for ix in range(len(self.preds)):
                            if  int(self.preds[ix][0])>skip:
                                self.preds[ix]=(str(int(self.preds[ix][0])-size+1),self.preds[ix][1])
                        for ij in range(len(self.args)):
                            for ix in range(len(self.args[ij])):
                                if  int(self.args[ij][ix][0])>skip:
                                    self.args[ij][ix]=(str(int(self.args[ij][ix][0])-size+1),self.args[ij][ix][1])


       








    def createFromLines(self,lines,olines=[]):
        if len(olines) > 0:

            for i in range(len(lines)):
                self.sntc.append(Conll08Wrd(lines[i],olines[i]))
        else:
            for line in lines:
                self.sntc.append(Conll08Wrd(line))



    def __len__(self):
        return len(self.sntc)

    def __iter__(self):
        return self.sntc.__iter__();

    def __str__(self):
         return "\n".join([w.__str__() for w in self.sntc])

    def utt(self,s=0,label="form"):
        if s==0:
            return " ".join([ x[label] for x in self.sntc])
        else:
            return " ".join([ x[label] for x in self.sntc[:s]])

    def conll06(self):
        return "\n".join([w.conll06() for w in self.sntc])

    def conll05_mst(self):
        dpreds=dict(self.preds)
        dargs=[dict(arg) for arg in self.args]
        lines = [w.conll05_mst() for w in self.sntc]

        for i in range(1,len(lines)):
            try:
                pred=dpreds[str(i)]
            except KeyError:
                pred="_"
            lines[i]+="\t"+pred

        for j in range(1,len(self.preds)):
            for i in range(1,len(lines)):
                try:
                    arg=dargs[j][str(i)]
                except KeyError:
                    arg="_"
                lines[i]+="\t"+arg
        return "\n".join(lines)


    def conll05(self):
        dpreds=dict(self.preds)
        dargs=[dict(arg) for arg in self.args]
        lines = [w.conll05() for w in self.sntc]

        for i in range(1,len(lines)):
            try:
                pred=dpreds[str(i)]
            except KeyError:
                pred="_"
            lines[i]+="\t"+pred

        for j in range(1,len(self.preds)):
            for i in range(1,len(lines)):
                try:
                    arg=dargs[j][str(i)]
                except KeyError:
                    arg="_"
                lines[i]+="\t"+arg
        return "\n".join(lines)


    def addChnk(self,chnk):
        for i in range(len(self.sntc)):
            self.sntc[i].add(chnk[i])

    def replace(self,label,chnk):
        for i in range(len(self.sntc)):
            self.sntc[i].replace(label,chnk[i])



        
class Conll08Wrd:
    map=["id",
            "form",
            "lemma",
            "gpos",
            "ppos",
            "s_form",
            "s_lemma",
            "s_ppos",
            "head",
            "dep_rel",
            "ne_03",
            "ne_bbn",
            "wnet",
            "malt_head",
            "malt_dep_rel"
            ]
    omap=dict(zip(map,range(len(map))))

    def __init__(self,line,oline=None):
        self.w=re_space.split(line)[:10]
        if len(self.w) == 8:
            self.w.append('_')
            self.w.append('_')
        if not oline is None:            
            self.w+=re_space.split(oline)


    def __getitem__(self,x):
        return self.w[Conll08Wrd.omap[x]]

    def __str__(self):
        return "\t".join(self.w)

    def __setitem__(self,x,y):
        self.w[Conll08Wrd.omap[x]]=y


    def replace(self,label,val):
        self.w[Conll08Wrd.omap[label]]=val
 

    def add(self,eles):
        if len(self.w) == 10:
            for i in range(5):
                self.w.append('"_"')
        for ele in eles:
            self.w.append(ele)


    def conll06(self):
        line = [self.w[0],self.w[1],self.w[2],self.w[4][0],self.w[4],'_',
                self.w[8],self.w[9],self.w[8],self.w[9]]
        return "\t".join(line)

    def conll05(self):
        line = [self.w[0],self.w[1],self.w[2],self.w[3],self.w[4],'_',
                self.w[8],self.w[9],self.w[8],self.w[9]]
        return "\t".join(line)

    def conll05_mst(self):
        line = [self.w[0],self.w[1],self.w[2],self.w[3],self.w[4],'_',
                self.w[8],self.w[9],self.w[15],self.w[16]]
        return "\t".join(line)


def chg_map(eles):
        Conll08Wrd.map+=eles
        Conll08Wrd.omap=dict(zip(Conll08Wrd.map,range(len(Conll08Wrd.map))))


# TheBeastCorpus 
class TheBeastSignature:

    def __init__(self,strict=False):
        self.types={}
        self.defs={}
        self.strict=strict


    def setStrict(b):
        self.strict=b

    def addDef(self,name,deff):
        self.defs[name]=deff

    def addType(self,pred,val):

        try:
            self.types[pred]
            try:
               self.types[pred][val]+=1
            except KeyError:
               self.types[pred][val]=1
        except KeyError :
            self.types[pred]={}
            try:
               self.types[pred][val]+=1
            except KeyError:
               self.types[pred][val]=1

    def print2file(self,file):
        for t,vals in self.types.iteritems():
            vals=vals.keys()
            beg_line= "type " + t +":"
            if not self.strict:
                beg_line+=" ..."
            last=0
            print >> file, beg_line,",".join(vals),';'
#            for ii in range(len(vals)/100):
#                print >> file, ",".join(vals[(100*ii):(100*(ii+1)-1)]),","
#                last=ii
#            print >> file, ",".join(vals[(100*(last)):]),";"
        for n,d in self.defs.iteritems():
            print >> file, "predicate ", n, ":"," x ".join(d), ";"
                
            

class TheBeastCorpus:
    use_quotes=True
    def __init__(self,filename=None,sig=None):
        self.ix=None
        self.sig=sig
        self.instcs=[]
        self.len = 0
        self.filename=filename
        if not filename is None:
            file = open(filename,"r")
            chnk = self.readChunk(file)
            i=0
            while len(chnk)>0:
                i+=1
                chnk = self.readChunk(file)
            self.len = i
            file.close()
            

    def readChunk(self,file):
        lines=[]
        line = file.readline()
        while len(line)>0:
            line=line.strip()
            if not line.startswith(">>"):
                lines.append(line)
            else:
                if len(lines) > 0:
                    return lines
            line = file.readline()
        return lines

    def __iter__(self):
        self.file=open(self.filename,"r")
        return self

    def next(self):
        chnk = self.readChunk(self.file)
        if len(chnk)>0:
            return TheBeastIntc(None,None,chnk)
        else:
            self.file.close()
            raise StopIteration


class TheBeastIntc:
    def __init__(self,sig=None,par=None,lines=None):
        self.intc={}
        self.sig=sig
        self.corpus=par
        if  not lines is None:
            self.createFromLines(lines)

    def __getitem__(self,ix):
        return self.intc[ix]


    def createFromLines(self,lines):
        pred=""
        for line in lines:
            if line.startswith(">"):
                pred=line[1:]
                self.intc[pred]=[]
            else:
                vals=re_space.split(line)
                self.addPred(pred,vals)


    def addPred(self,pred,val):
        try:
            self.intc[pred].append(val)
        except KeyError :
            self.intc[pred]=[]
            self.intc[pred].append(val)

    def __str__(self):
        lines=[]
        for pred_name,preds in self.intc.iteritems():
            lines.append(">"+pred_name)
            for args in preds:
                lines.append("  ".join(args));
        return "\n".join(lines)


def normalize_entry(val):
    if TheBeastCorpus.use_quotes:
        return '"%s"'%val
    return val
     




