#include <jni.h>

#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

#include "thebeast_osi_OsiSolverJNI.h"
#include "OsiClpSolverInterface.hpp"
#include "OsiCbcSolverInterface.hpp"

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    createImplementation
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_thebeast_osi_OsiSolverJNI_createImplementation
  (JNIEnv *, jclass, jint implementation){
  OsiSolverInterface* solver = 0;
  switch(implementation){
    case 0: solver = new OsiCbcSolverInterface; break;
    case 1: solver = new OsiClpSolverInterface; break;
  }
  printf("pointer %d\n",solver);  
  return (jint)solver;
}




/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    initialSolve
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_initialSolve
  (JNIEnv *, jobject, jint ptr){
  OsiSolverInterface* solver = (OsiSolverInterface*) ptr;
  printf("pointer %d\n", solver);
  printf("cols %d\n", solver->getNumCols());
  //pointer->initialSolve();
  //((OsiSolverInterface*)ptr)->initialSolve();
}

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    resolve
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_resolve
  (JNIEnv *, jobject, jint ptr){
  OsiSolverInterface* solver = (OsiSolverInterface*) ptr;
  solver->resolve();
}

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    branchAndBound
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_branchAndBound
  (JNIEnv *, jobject, jint ptr){
  OsiSolverInterface* solver = (OsiSolverInterface*) ptr;
  solver->branchAndBound();
}

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    addCol
 * Signature: (I[I[DDDD)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_addCol
  (JNIEnv *, jobject, jint numberElements, jintArray, jdoubleArray, jdouble, jdouble, jdouble){

}
