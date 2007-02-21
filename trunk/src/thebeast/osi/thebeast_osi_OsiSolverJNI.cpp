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

#define MY_INF 1E100

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
  double dEmpty = 0;
	int iEmpty = 0;
  //char cEmpty = '?';
	solver->loadProblem(0, 0, &iEmpty, &iEmpty, &dEmpty, &dEmpty, &dEmpty, &dEmpty, &dEmpty, &dEmpty);  
  return (jint)solver;
}


/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    setHintParam
 * Signature: (IZII)Z
 */
JNIEXPORT jboolean JNICALL Java_thebeast_osi_OsiSolverJNI_setHintParam
  (JNIEnv *, jclass, jint key, jboolean yesNo, jint strength, jint ptr){
  OsiSolverInterface* solver = (OsiSolverInterface*) ptr;
//  return solver->setHintParam(OsiDoReducePrint,yesNo,OsiHintTry);  
  return solver->setHintParam(OsiHintParam(key),yesNo,OsiHintStrength(strength));
}

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    setCbcLogLevel
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_setCbcLogLevel
  (JNIEnv *, jclass, jint level, jint ptr){

  OsiCbcSolverInterface* solver = (OsiCbcSolverInterface*) ptr;
  //printf("%d",solver);
  solver->messageHandler()->setLogLevel(level);
  //solver->getModelPtr()->messageHandler()->setLogLevel(level);
}



/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    initialSolve
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_initialSolve
  (JNIEnv *, jobject, jint ptr){
  OsiSolverInterface* solver = (OsiSolverInterface*) ptr;
  solver->initialSolve();
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
 * Method:    getNumCols
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_thebeast_osi_OsiSolverJNI_getNumCols
  (JNIEnv *, jobject, jint ptr){
  OsiSolverInterface* solver = (OsiSolverInterface*) ptr;
  return (jint) solver->getNumCols();   
}

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    getNumRows
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_thebeast_osi_OsiSolverJNI_getNumRows
  (JNIEnv *, jobject, jint ptr){
  OsiSolverInterface* solver = (OsiSolverInterface*) ptr;
  return (jint) solver->getNumRows();   
}


/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    addCol
 * Signature: (I[I[DDDDI)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_addCol
  (JNIEnv * env, jobject, jint numberElements,
  jintArray rows, jdoubleArray elements,
  jdouble collb, jdouble colub,
  jdouble obj, jint ptr){

  OsiSolverInterface* solver = (OsiSolverInterface*) ptr;

  jint* cRows = env->GetIntArrayElements(rows, 0);
  jdouble* cElements = env->GetDoubleArrayElements(elements,0);

  collb = collb == MY_INF ? DBL_MAX : collb == -MY_INF ? -DBL_MAX : collb;
  colub = colub == MY_INF ? DBL_MAX : colub == -MY_INF ? -DBL_MAX : colub;

  solver->addCol(numberElements,(int*)cRows,(double*)cElements,collb,colub,obj);

  env->ReleaseIntArrayElements(rows, cRows, 0);
  env->ReleaseDoubleArrayElements(elements, cElements, 0);
}


/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    addRow
 * Signature: (I[I[DDDI)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_addRow
  (JNIEnv * env, jobject, jint numberElements, jintArray cols, jdoubleArray elements, jdouble rowlb, jdouble rowub, jint ptr){

  OsiSolverInterface* solver = (OsiSolverInterface*) ptr;

  jint* cCols = env->GetIntArrayElements(cols, 0);
  jdouble* cElements = env->GetDoubleArrayElements(elements,0);

  rowlb = rowlb == MY_INF ? DBL_MAX : rowlb == -MY_INF ? -DBL_MAX : rowlb;
  rowub = rowub == MY_INF ? DBL_MAX : rowub == -MY_INF ? -DBL_MAX : rowub;


  solver->addRow(numberElements,(int*)cCols,(double*)cElements,rowlb,rowub);

  env->ReleaseIntArrayElements(cols, cCols, 0);
  env->ReleaseDoubleArrayElements(elements, cElements, 0);

}

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    setColLower
 * Signature: (IDI)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_setColLower
  (JNIEnv *, jobject, jint index, jdouble collb, jint ptr){

  OsiSolverInterface* solver = (OsiSolverInterface*) ptr;

  collb = collb == MY_INF ? DBL_MAX : collb == -MY_INF ? -DBL_MAX : collb;

  solver->setColLower(index,collb);
}

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    setColUpper
 * Signature: (IDI)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_setColUpper
  (JNIEnv *, jobject, jint index, jdouble colub, jint ptr){

  OsiSolverInterface* solver = (OsiSolverInterface*) ptr;

  colub = colub == MY_INF ? DBL_MAX : colub == -MY_INF ? -DBL_MAX : colub;

  solver->setColUpper(index,colub);
}



/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    setObjSense
 * Signature: (DI)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_setObjSense
  (JNIEnv *, jobject, jdouble s, jint ptr){

    OsiSolverInterface* solver = (OsiSolverInterface*) ptr;
    solver->setObjSense(s);
}

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    setInteger
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_setInteger
  (JNIEnv *, jobject, jint index, jint ptr){
    OsiSolverInterface* solver = (OsiSolverInterface*) ptr;
    solver->setInteger(index);
  

}



/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    getObjValue
 * Signature: (I)D
 */
JNIEXPORT jdouble JNICALL Java_thebeast_osi_OsiSolverJNI_getObjValue
  (JNIEnv *, jobject, jint ptr){
    OsiSolverInterface* solver = (OsiSolverInterface*) ptr;
    return solver->getObjValue();  

}

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    reset
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_reset
  (JNIEnv *, jobject, jint ptr){
    OsiSolverInterface* solver = (OsiSolverInterface*) ptr;
    solver->reset();  
}

/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    delete
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_OsiSolverJNI_delete
  (JNIEnv *, jobject, jint ptr){
    OsiSolverInterface* solver = (OsiSolverInterface*) ptr;
    delete solver;
}



/*
 * Class:     thebeast_osi_OsiSolverJNI
 * Method:    getColSolution
 * Signature: (I)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_thebeast_osi_OsiSolverJNI_getColSolution
  (JNIEnv * env, jobject, jint ptr){
    OsiSolverInterface* solver = (OsiSolverInterface*) ptr;
    int colCount = solver->getNumCols();
    const double* cResult = solver->getColSolution();
    jdoubleArray jResult = env->NewDoubleArray(colCount);
    env->SetDoubleArrayRegion(jResult,0,colCount,const_cast<jdouble*>(cResult));
    return jResult;
    
}

