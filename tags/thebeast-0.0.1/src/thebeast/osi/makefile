# Copyright (C) 2006 International Business Machines and others.
# All Rights Reserved.
# This file is distributed under the Common Public License.

# $Id: Makefile.in 726 2006-04-17 04:16:00Z andreasw $

##########################################################################
#    You can modify this example makefile to fit for your own program.   #
#    Usually, you only need to change the five CHANGEME entries below.   #
##########################################################################

# To compile other examples, either changed the following line, or
# add the argument DRIVER=problem_name to make
DRIVER = thebeast_osi_OsiSolverJNI

# CHANGEME: This should be the name of your executable
EXE = $(DRIVER)

LIB = libosi.jnilib

# CHANGEME: Here is the name of all object files corresponding to the source
#           code that you wrote in order to define the problem statement
OBJS =  $(DRIVER).o

# CHANGEME: Additional libraries
ADDLIBS =

# CHANGEME: Additional flags for compilation (e.g., include flags)
ADDINCFLAGS =

# CHANGEME: Directory to the sources for the (example) problem definition
# files
SRCDIR = .


##########################################################################
#  Usually, you don't have to change anything below.  Note that if you   #
#  change certain compiler options, you might have to recompile the      #
#  package.                                                              #
##########################################################################

# C++ Compiler command
CXX = g++

# C++ Compiler options
CXXFLAGS = -O3 -fomit-frame-pointer -pipe -DNDEBUG -Wimplicit -Wparentheses -Wreturn-type -Wcast-qual -Wall -Wpointer-arith -Wwrite-strings -Wconversion

# additional C++ Compiler options for linking
CXXLINKFLAGS = 

# Directory with COIN header files
COININCDIR = /Users/sebastian/projects/coin-Cbc/include

# Directory with COIN libraries
COINLIBDIR = /Users/sebastian/projects/coin-Cbc/lib

# Libraries necessary to link with Clp
LIBS = -L$(COINLIBDIR) -lCbc -lCgl -lOsiClp -lOsiCbc -lOsi -lClp -lCoinUtils \
	-lm \
	`cat $(COINLIBDIR)/cgl_addlibs.txt` \
	`cat $(COINLIBDIR)/osi_addlibs.txt` \
	`cat $(COINLIBDIR)/clp_addlibs.txt` \
	`cat $(COINLIBDIR)/coinutils_addlibs.txt`

# Necessary Include dirs (we use the CYGPATH_W variables to allow
# compilation with Windows compilers)
INCL =  -I`$(CYGPATH_W) $(COININCDIR)` $(ADDINCFLAGS) -I/System/Library/Frameworks/JavaVM.framework/Headers

# The following is necessary under cygwin, if native compilers are used
CYGPATH_W = echo

# Here we list all possible generated objects or executables to delete them
CLEANFILES = \
	thebeast_osi_OsiSolverJNI.o thebeast_osi_OsiSolverJNI

all: lib

.SUFFIXES: .cpp .c .o .obj

$(EXE): $(OBJS)
	bla=;\
	for file in $(OBJS); do bla="$$bla `$(CYGPATH_W) $$file`"; done; \
	$(CXX) $(CXXLINKFLAGS) $(CXXFLAGS) -o $@ $$bla $(ADDLIBS) $(LIBS)

lib: $(OBJS)
	$(CXX) -dynamiclib $(CXXLINKFLAGS) $(CXXFLAGS) -o $(LIB) $(ADDLIBS) $(LIBS) $(OBJS) -framework JavaVM

clean:
	rm -rf $(CLEANFILES)

.cpp.o:
	$(CXX) $(CXXFLAGS) $(INCL) -c -o $@ `test -f '$<' || echo '$(SRCDIR)/'`$<


.cpp.obj:
	$(CXX) $(CXXFLAGS) $(INCL) -c -o $@ `if test -f '$<'; then $(CYGPATH_W) '$<'; else $(CYGPATH_W) '$(SRCDIR)/$<'; fi`

.c.o:
	$(CC) $(CFLAGS) $(INCL) -c -o $@ `test -f '$<' || echo '$(SRCDIR)/'`$<


.c.obj:
	$(CC) $(CFLAGS) $(INCL) -c -o $@ `if test -f '$<'; then $(CYGPATH_W) '$<'; else $(CYGPATH_W) '$(SRCDIR)/$<'; fi`
