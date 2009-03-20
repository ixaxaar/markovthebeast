#!/usr/bin/python2.4
# --------------------------------------------------------
# Library to deal with corpora
# --------------------------------------------------------
# Ivan Vladimir Meza-Ruiz
# 2008/Edinburgh
# --------------------------------------------------------

import re
import gzip
import codecs
import tarfile

re_space=re.compile(r'[\s]+')
re_space2=re.compile(r'^\s+')
re_emptyline=re.compile(r'^\t\t')
re_quota=re.compile(r'"')
re_quota2=re.compile(r'^"')

# Coll09 corpus class
class Conll09Corpus:
    """It handles a corpus in the Conll09 format"""

    #ID FORM LEMMA PLEMMA POS PPOS FEAT PFEAT HEAD PHEAD DEPREL PDEPREL FILLPRED PRED APREDs 
    # Extra line for root elements
    e_line = "0\tROOT\tROOT\tROOT\tROOT\tROOT\t_\t_\t0\t0\tROOT\tROOT\t_"
    use_eline = True

    def __init__(self,filename=None,gold_deps=False,sense=True):
        """Initilize the handler
            filename  is the name of the file where the corpus is located
            gold_deps use gold deps for the mst predicates"""
        # It doesn't saves the corpus in memory anymore
        #self.sntcs=[ ]
        self.ix=None
        self.len=0
        self.filename=filename
        file = None
        self.sense=sense
        self.gold_deps=gold_deps
        if not filename is None:
            if not "@" in filename:
               file=open(filename,'r')
            else:
               bits=filename.split('@')
               tf=tarfile.open(bits[0],encoding="utf-8")
               name=None
               for ti in tf.getmembers():
                   if ti.name.find(bits[1]) > -1:
                       name=ti
                       break
               if not name is None:
                   file=tf.extractfile(name)
               else:
                   sys.exit(1)

            line=file.readline()
            while len(line)==1:
                line=file.readline()
            chnk = self.readChunk(file,line)
            i = 1
            while len(chnk)>0 :
                i+=1
                line=file.readline()
                while len(line)==1:
                    line=file.readline()
                chnk = self.readChunk(file,line)
        self.len = i
        file.close()

    def readChunk(self,file,first_line=""):
        lines = []
        line = first_line
        m = re_emptyline.match(line)
        while len(line) > 1 and not m:
            line=line.strip()
            lines.append(line)
            line = file.readline()
            m = re_emptyline.match(line)
        return lines

    def size(self):
        return self.len

    def __iter__(self):
        self.file = open(self.filename,'r')
        return self

    def next(self):
        line=self.file.readline()
        while len(line)==1:
             line=self.file.readline()
        chnk = self.readChunk(self.file,line)
        if len(chnk)>0 :
            return self.chnk2sntc(chnk)
        else:
            self.file.close()
            raise StopIteration 


    def chnk2sntc(self,chnk):
        """Translate a chnk into a sentence"""

        lines = chnk
        if Conll09Corpus.use_eline:
            size_line=len(re_space.split(lines[0]))
            lines=lines+[Conll09Corpus.e_line]
        return Conll09Sntc(lines,self.gold_deps,sense=self.sense)

#ID FORM LEMMA PLEMMA POS PPOS FEAT PFEAT HEAD PHEAD DEPREL PDEPREL FILLPRED PRED APREDs 
class Conll09Sntc:
    def __init__(self,lines,gold_deps=False,sense=True):
        self.sntc=[]
        self.preds=[]
        self.args=[]
        
        if  not lines is None:            
            # Main lines
            self.createFromLines(lines,gold_deps)
            preds=[]    
            # SRL labels            
            args=[[] for i in re_space.split(lines[1])[13:]]
            for i in range(len(lines)):
                line = re_space.split(lines[i])[13:]
                ix = re_space.split(lines[i])[0]
                if len(line)>0 and line[0] != '_':
                    preds.append((ix,line[0]))
                for j in range(1,len(line)):
                    if line[j] != '_':
                        args[j-1].append((ix,line[j]))
            self.preds=preds
            self.args=args


    def createFromLines(self,lines,olines=[],gold_deps=False):
        for line in lines:
            self.sntc.append(Conll09Wrd(line))

#        for w in self.sntc: 
#            w['pdep_rel']=self.sntc[int(w['phead'])]['ppos']

    def __getitem__(self,id):
        if id==0:
            id=len(self.sntc)
        return self.sntc[id-1]

    def __len__(self):
        return len(self.sntc)

    def __iter__(self):
        return self.sntc.__iter__();

    def __str__(self):
        dpreds=dict(self.preds)
        dargs=[dict(arg) for arg in self.args]
        lines = [w.__str__() for w in self.sntc[:-1]]

        for i in range(0,len(lines)):
            try:
                pred=dpreds[str(i+1)]
            except KeyError:
                pred="_"
            lines[i]+="\t"+pred

        for j in range(0,len(self.preds)):
            for i in range(0,len(lines)):
                try:
                    arg=dargs[j][str(i+1)]
                except KeyError:
                    arg="_"
                lines[i]+="\t"+arg
        return "\n".join(lines)

    def utt(self,s=0,label="form"):
        if s==0:
            return " ".join([ x[label] for x in self.sntc])
        else:
            return " ".join([ x[label] for x in self.sntc[:s]])

    def conll05(self):
        pass

    def conll06(self):
        pass

    def conll06_mst(self):
        pass

    def conll07(self):
        pass

    def conll07_mst(self):
        pass

    def conll08_mst(self):
        pass

    def conll08(self):
        return self.__str__()

    def replace(self,label,chnk):
        for i in range(len(self.sntc)):
            self.sntc[i].replace(label,chnk[i])

    def eliminate(self,label,cn):
        preds=[ (i,s) for (i,s) in self.preds 
                if not self.sntc[int(i)-1][label].startswith(cn)]
        args =[ self.args[i] for i in range(len(self.preds)) 
                if not self.sntc[int(self.preds[i][0])-1][label].startswith(cn)]
        self.preds=preds
        self.args=args
        
#ID FORM LEMMA PLEMMA POS PPOS FEAT PFEAT HEAD PHEAD DEPREL PDEPREL FILLPRED PRED APREDs 
class Conll09Wrd:
    map=["id",
            "form",
            "lemma",
            "plemma",
            "pos",
            "ppos",
            "feat",
            "pfeat",
            "head",
            "phead",
            "dep_rel",
            "pdep_rel",
            "fillpred"
            ]
    omap=dict(zip(map,range(len(map))))

    def __init__(self,line,oline=None,gold_deps=False):
        self.w=re_space.split(line)[:13]
            
    def __getitem__(self,x):
        return self.w[Conll09Wrd.omap[x]]

    def __setitem__(self,x,y):
        self.w[Conll09Wrd.omap[x]]=y

    def replace(self,label,val):
        self.w[Conll09Wrd.omap[label]]=val

    def add(self,eles):
        pass

    def __str__(self):
        line = [self.w[0],self.w[1],self.w[2],self.w[3],self.w[4],
                self.w[5],self.w[6],self.w[7],self.w[8],self.w[9],
                self.w[10],self.w[11],self.w[12]]
        return "\t".join(line)

    def conll05(self):
        pass

    def conll06(self):
        pass

    def conll06(self):
        pass

    def conll06_mst(self):
        pass

    def conll07(self):
        pass

    def conll07_mst(self):
        pass

    def conll08(self):
        return self.__str__()
    
    def conll08_mst(self):
        pass



# Coll08 corpus class
class Conll08Corpus:
    """It handles a corpus in the Conll08 format"""

    # Extra line for root elements
    e_line = "0\tROOT\tROOT\tROOT\tROOT\tROOT\tROOT\t0\t0\t_\t_"
    e_open_line = "0\t0\tROOT\tROOT\tROOT"
    use_eline = True

    def __init__(self,filename=None,ofilename=None,efilename=None,hyphens=True,gold_deps=False):
        """Initilize the handler
            filename  is the name of the file where the corpus is located
            ofilename is the name of the file where the extra information is\
                    available
            efilename mst extra file (conll06 format)
            hyphens   eliminate hyphens
            gold_deps use gold deps for the mst predicates"""
        # It doesn't saves the corpus in memory anymore
        #self.sntcs=[ ]
        self.ix=None
        self.len=0
        self.filename=filename
        self.ofilename=ofilename
        file = None
        ofile = None
        self.hyphens=hyphens
        self.gold_deps=gold_deps
        if not filename is None:
            if not "@" in filename:
               file=open(filename,'r')
            else:
               bits=filename.split('@')
               tf=tarfile.open(bits[0])
               name=None
               for ti in tf.getmembers():
                   if ti.name.find(bits[1]) > -1:
                       name=ti
                       break
               if not name is None:
                   file=tf.extractfile(name)
               else:
                   sys.exit(1)


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
        return Conll08Sntc(lines,olines,self.hyphens,self.gold_deps)






class Conll08Sntc:
    def __init__(self,lines=None,olines=[],hyphens=True,gold_deps=False):
        self.sntc=[]
        self.preds=[]
        self.args=[]
        
        
        if  not lines is None:            
            # Main lines
            self.createFromLines(lines,olines,gold_deps)
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
                            if not w['lemma'].startswith('_'):
                                self.sntc[i-size]['lemma']+=w['lemma']
                            size+=1
                        elif inside:
                            inside=False
                            break
                    if flag_change:
                        # Looking for the head
                        head=int(self.sntc[skip-1]['head'])
                        for jj in range(skip-1,skip+size):
                            h_tmp=int(self.sntc[jj]['head'])
                            if h_tmp > skip+size+1 or h_tmp < skip:
                                head=h_tmp                                
                                break
                        self.sntc[skip-1]['head']=self.sntc[head-1]['head']
                        self.sntc[skip-1]['dep_rel']=self.sntc[head-1]['dep_rel']
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

     



    def createFromLines(self,lines,olines=[],gold_deps=False):
        if len(olines) > 0:
            for i in range(len(lines)):
                self.sntc.append(Conll08Wrd(lines[i],olines[i],gold_deps))
        else:
            for line in lines:
                self.sntc.append(Conll08Wrd(line))

    def __getitem__(self,id):
        if id==0:
            id=len(self.sntc)
        return self.sntc[id-1]

        return self.sntc.__iter__();

    def __len__(self):
        return len(self.sntc)

    def __iter__(self):
        return self.sntc.__iter__();

    def __str__(self):
        dpreds=dict(self.preds)
        dargs=[dict(arg) for arg in self.args]
        lines = [w.__str__() for w in self.sntc[:-1]]

        for i in range(0,len(lines)):
            try:
                pred=dpreds[str(i+1)]
            except KeyError:
                pred="_"
            lines[i]+="\t"+pred

        for j in range(0,len(self.preds)):
            for i in range(0,len(lines)):
                try:
                    arg=dargs[j][str(i+1)]
                except KeyError:
                    arg="_"
                lines[i]+="\t"+arg
        return "\n".join(lines)

    def utt(self,s=0,label="form"):
        if s==0:
            return " ".join([ x[label] for x in self.sntc])
        else:
            return " ".join([ x[label] for x in self.sntc[:s]])

    def conll05(self):
        dpreds=dict(self.preds)
        dargs=[dict(arg) for arg in self.args]
        lines = [[w.conll05()] for w in self.sntc]
        deps=[(w["id"],w["head"]) for w in self.sntc]
        tree={}

        for d,h in deps:
            try:
                tree[h].append(d)
            except KeyError:
                tree[h]=[d]

        pred_ix=0
        spans=[]
        for args in dargs:
            spans_s={}
            spans_e={}
            pred=int(self.preds[pred_ix][0])
            partS=[]
            for head,arg in args.iteritems():
                arg_span=self.get_span(tree,head,[head])
                print "*",arg_span,arg
                if pred in arg_span:
                    c_spans=[[int(head)]]
                    arg_span=[]
                    for c in tree[head]:
                        c_spans.append(self.get_span(tree,c,[c]))
                    for c_span in c_spans:
                        if not pred in c_span:
                            arg_span+=c_span
                    arg_span.sort()
                prev=arg_span[0]
                partS=[[prev]]
                for arg_span_ix in range(1,len(arg_span)):
                    cur=arg_span[arg_span_ix]
                    if (prev+1) != cur:
                        partS[-1].append(prev)
                        partS.append([cur])
                    prev=cur
                partS[-1].append(arg_span[-1])
                print partS,arg
                for part in partS:
                    if arg=="AM-MOD":
                        spans_s[int(head)]=(arg,int(head))
                        try:
                            spans_e[int(head)]+=1
                        except KeyError:
                            spans_e[int(head)]=1
                    else:
                        spans_s[part[0]]=(arg,part[1])
                        try:
                            spans_e[part[1]]+=1
                        except KeyError:
                            spans_e[part[1]]=1

                print arg_span,arg,head
            pred_ix+=1           
            spans.append((spans_s,spans_e))
        for i in range(0,len(lines)):
            # Prints the predicate
            try:
                pred=dpreds[str(i+1)]
                bits=pred.split(".")
                lines[i].append("\t")
                lines[i].append(bits[1])
                lines[i].append("\t")
                lines[i].append(bits[0])
                continue
            except KeyError:
                lines[i].append("\t-\t-")
        pred_ix=0
        print spans
        for spans_s,spans_e in spans:
            state=0
            prev_span=-1
            span_e=-1
            for i in range(0,len(lines)):
#                print self.preds[pred_ix],str(i+1)
                if self.preds[pred_ix][0] == str(i+1) :
                    lines[i].append("\t(V*)")
                    continue
                # Prints the args
                try:
                    spans_s[i+1]
                    if state>0:
                        if lines[i-1][-1][1]=='(':
                            lines[i-1][-1]+=")"
                        else:
                            lines[i-1][-1]="\t*)"
                    span_e=spans_s[i+1][1]
                    if span_e==i+1:
                        lines[i].append("\t("+spans_s[i+1][0]+"*)")
                        span_e=-1
                        state=0
                    else:
                        state=1
                        lines[i].append("\t("+spans_s[i+1][0]+"*")
                    prev_span=i+1
                except KeyError:
                    if span_e==(i+1):
                        lines[i].append("\t*)")
                        state=0
                    else:
                        lines[i].append("\t*")
            pred_ix+=1
        return "\n".join(["".join(line) for line in lines])


    def get_span(self,tree,id,terminals=[]):
        s=dict([(int(x),1) for x in self.get_span_(tree,id,terminals)])
        s=s.keys()
        s.sort()
        return s

    def get_span_(self,tree,id,terminals=[]):
        
        try:
            tree[id]
        except KeyError:
            terminals.append(id)
            return terminals
        for d in tree[id]:
            terminals.append(d)
            if d!=id:
                terminals=self.get_span(tree,d,terminals)

        return terminals

    def conll06(self):
        return "\n".join([w.conll06() for w in self.sntc])

    def conll06_mst(self):
        return "\n".join([w.conll06_mst() for w in self.sntc])


    def conll07(self):
        return self.conll06()

    def conll07_mst(self):
        return self.conll06_mst()

    def conll08_mst(self):
        dpreds=dict(self.preds)
        dargs=[dict(arg) for arg in self.args]
        lines = [w.conll08_mst() for w in self.sntc[:-1]]

        for i in range(0,len(lines)):
            try:
                pred=dpreds[str(i+1)]
            except KeyError:
                pred="_"
            lines[i]+="\t"+pred

        for j in range(0,len(self.preds)):
            for i in range(0,len(lines)):
                try:
                    arg=dargs[j][str(i+1)]
                except KeyError:
                    arg="_"
                lines[i]+="\t"+arg
        return "\n".join(lines)


    def conll08(self):
        return self.__str__()


    def addChnk(self,chnk):
        for i in range(len(self.sntc)):
            self.sntc[i].add(chnk[i])

    def replace(self,label,chnk):
        for i in range(len(self.sntc)):
            self.sntc[i].replace(label,chnk[i])

    def eliminate(self,label,cn):
        preds=[ (i,s) for (i,s) in self.preds if not self.sntc[int(i)-1][label].startswith(cn)]
        args =[ self.args[i] for i in range(len(self.preds)) if not self.sntc[int(self.preds[i][0])-1][label].startswith(cn)]
        
        self.preds=preds
        self.args=args
        
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
            "malt_dep_rel",
            ]
    omap=dict(zip(map,range(len(map))))

    def __init__(self,line,oline=None,gold_deps=False):
        self.w=re_space.split(line)[:10]
        if len(self.w) == 8:
            self.w.append('_')
            self.w.append('_')
        if not oline is None:
            if not gold_deps:
                self.w+=re_space.split(oline)
            else:
                ol=re_space.split(oline)
                self.w+=ol[:3]+self.w[8:10]
        else:
            self.w.append('_')
            self.w.append('_')
            self.w.append('_')
            self.w.append('_')
            self.w.append('_')
            
    def __getitem__(self,x):
        return self.w[Conll08Wrd.omap[x]]


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

    def __str__(self):
        line = [self.w[0],self.w[1],self.w[2],self.w[3],self.w[4],
                self.w[5],self.w[6],self.w[7],self.w[8],self.w[9]]
        return "\t".join(line)

    def conll05(self):
        line = [self.w[1],"_",self.w[4],"_","_","_"]
        return "\t".join(line)


    def conll06(self):
        line = [self.w[0],self.w[1],self.w[2],self.w[4][0],self.w[4],
                "_",self.w[8],self.w[9],self.w[8],self.w[9]]
        return "\t".join(line)

    def conll06_mst(self):
        line = [self.w[0],self.w[1],self.w[2],self.w[4][0],self.w[4],
                "_",self.w[15],self.w[16],self.w[15],self.w[16]]
        return "\t".join(line)

    def conll07(self):
        return self.conll06()

    def conll07_mst(self):
        return self.conll06_mst()

    def conll08(self):
        return self.__str__()

    def conll08_mst(self):
        line = [self.w[0],self.w[1],self.w[2],self.w[3],self.w[4],
                self.w[5],self.w[6],self.w[7],self.w[15],self.w[16]]
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
            if not "@" in filename:
               file=open(filename,'r')
            else:
               bits=filename.split('@')
               tf=tarfile.open(bits[0])
               name=None
               for ti in tf.getmembers():
                   if ti.name.find(bits[1]) > -1:
                       name=ti
                       break
               if not name is None:
                   file=tf.extractfile(name)
               else:
                   sys.exit(1)

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
        if not "@" in self.filename:
               self.file=open(self.filename,'r')
        else:
               bits=self.filename.split('@')
               tf=tarfile.open(bits[0])
               name=None
               for ti in tf.getmembers():
                   if ti.name.find(bits[1]) > -1:
                       name=ti
                       break
               if not name is None:
                   self.file=tf.extractfile(name)
               else:
                   sys.exit(1)
        return self

    def next(self):
        chnk = self.readChunk(self.file)
        if len(chnk)>0:
            return TheBeastIntc(None,None,chnk)
        else:
            self.file.close()
            raise StopIteration


def getAtoms(line):
    atoms=[]
    
    bits=re_space.split(line)
    state=0

    for bit in bits:
        if len(bit) > 0:
            if state==0:
                if bit[0]=='"':
                    state=1
                atoms.append(bit)
            else:
                if bit[-1]=='"':
                    state=0
                atoms[-1]+=" "+bit

    return tuple(atoms)
        
            
        
        



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
                vals=getAtoms(line)
                self.addPred(pred,vals)

    def replace(self,pred,vals):
        self.intc[pred]=vals


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
    if '"' in val:
        val=val.replace('"',"_DQ_")
    if TheBeastCorpus.use_quotes:
        return '"%s"'%val
    return val
     




