#include <jni.h>

#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <iostream>

#include "thebeast_osi_CbcModel.h"
#include "OsiClpSolverInterface.hpp"
#include "OsiCbcSolverInterface.hpp"
#include "CbcModel.hpp"
#include "CoinBuild.hpp"

#define MY_INF 1E100

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    createCbcModel
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_thebeast_osi_CbcModel_createCbcModel
  (JNIEnv *, jclass, jlong osiPtr){
  OsiSolverInterface* solver = (OsiSolverInterface*) osiPtr;
  CbcModel* model = new CbcModel(*solver);
  return (jlong) model;
}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    branchAndBound
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_CbcModel_branchAndBound
  (JNIEnv *, jobject, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  model->branchAndBound();
}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    bestSolution
 * Signature: (J)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_thebeast_osi_CbcModel_bestSolution
  (JNIEnv* env, jobject, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  int colCount = model->getNumCols();
  const double* cResult = model->bestSolution();
  jdoubleArray jResult = env->NewDoubleArray(colCount);
  env->SetDoubleArrayRegion(jResult,0,colCount,const_cast<jdouble*>(cResult));
  return jResult;

}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    setLogLevel
 * Signature: (IJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_CbcModel_setLogLevel
  (JNIEnv *, jobject, jint level, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  model->setLogLevel((int)level);  
}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    delete
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_CbcModel_delete
  (JNIEnv *, jobject, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  delete model;  
}


/*
 * Class:     thebeast_osi_CbcModel
 * Method:    solver
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_thebeast_osi_CbcModel_solver
  (JNIEnv *, jclass, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  return (jlong) model->solver();  
}


/*
 * Class:     thebeast_osi_CbcModel
 * Method:    referenceSolver
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_thebeast_osi_CbcModel_referenceSolver
  (JNIEnv *, jclass, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  return (jlong) model->referenceSolver();  

}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    saveReferenceSolver
 * Signature: (J)J
 */
JNIEXPORT void JNICALL Java_thebeast_osi_CbcModel_saveReferenceSolver
  (JNIEnv *, jclass, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  model->saveReferenceSolver();  
}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    resetToReferenceSolver
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_CbcModel_resetToReferenceSolver
  (JNIEnv *, jclass, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  model->resetToReferenceSolver();    
}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    setMaximumSeconds
 * Signature: (DJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_CbcModel_setMaximumSeconds
  (JNIEnv *, jobject, jdouble seconds, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  model->setMaximumSeconds((double)seconds);  
}


