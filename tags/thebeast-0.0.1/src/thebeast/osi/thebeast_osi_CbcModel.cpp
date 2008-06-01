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

#include "CglGomory.hpp"
#include "CglProbing.hpp"
#include "CglKnapsackCover.hpp"
#include "CglRedSplit.hpp"
#include "CglClique.hpp"
#include "CglFlowCover.hpp"
#include "CglMixedIntegerRounding2.hpp"


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

/*
  private native void addCglProbing(boolean useObjective, int maxPass, int maxPassRoot,
                            int maxProbe, int maxProbeRoot, int maxLook, int maxLookRoot,
                            int maxElements, int rowCuts, long pointer);

 * Class:     thebeast_osi_CbcModel
 * Method:    addCglProbing
 * Signature: (ZIIIIIIIIJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_CbcModel_addCglProbing
  (JNIEnv *, jobject, jboolean useObjective, jint maxPass, jint maxPassRoot,
   jint maxProbe, jint maxProbeRoot, jint maxLook, jint maxLookRoot, jint maxElements, jint rowCuts, jlong ptr){

   CbcModel* model = (CbcModel*) ptr;
   CglProbing* generator1 = new CglProbing();
   generator1->setUsingObjective(useObjective);
   generator1->setMaxPass(maxPass);
   generator1->setMaxPassRoot(maxPassRoot);
   // Number of unsatisfied variables to look at
   generator1->setMaxProbe(maxProbe);
   generator1->setMaxProbeRoot(maxProbeRoot);
   // How far to follow the consequences
   generator1->setMaxLook(maxLook);
   generator1->setMaxLookRoot(maxLookRoot);
   // Only look at rows with fewer than this number of elements
   generator1->setMaxElements(maxElements);
   generator1->setRowCuts(rowCuts);
   model->addCutGenerator(generator1,-1,"Probing");


}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    addCglGomory
 * Signature: (IJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_CbcModel_addCglGomory
  (JNIEnv *, jobject, jint limit, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  CglGomory* generator2 = new CglGomory();
  generator2->setLimit(limit);
  model->addCutGenerator(generator2,-1,"Gomory");

}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    addCglKnapsackCover
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_CbcModel_addCglKnapsackCover
  (JNIEnv *, jobject, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  CglKnapsackCover* generator3 = new CglKnapsackCover();
  model->addCutGenerator(generator3,-1,"KnapsackCover");  
}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    addCglRedsplit
 * Signature: (IJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_CbcModel_addCglRedsplit
  (JNIEnv *, jobject, jint limit, jlong ptr){
    CbcModel* model = (CbcModel*) ptr;
    CglRedSplit* generator2 = new CglRedSplit();
    generator2->setLimit(limit);
    model->addCutGenerator(generator2,-1,"Redsplit");
}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    addCglClique
 * Signature: (ZZJ)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_CbcModel_addCglClique
  (JNIEnv *, jobject, jboolean starCliqueReport, jboolean rowCliqueReport, jlong ptr){
    CbcModel* model = (CbcModel*) ptr;
    CglClique* generator2 = new CglClique();
    generator2->setStarCliqueReport(starCliqueReport);
    generator2->setRowCliqueReport(rowCliqueReport);
    model->addCutGenerator(generator2,-1,"Clique");

}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    addCglMixedIntegerRounding2
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_CbcModel_addCglMixedIntegerRounding2
  (JNIEnv *, jobject, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  CglMixedIntegerRounding2* generator3 = new CglMixedIntegerRounding2();
  model->addCutGenerator(generator3,-1,"MixedIntegerRounding2");

}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    addCglFlowCover
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_thebeast_osi_CbcModel_addCglFlowCover
  (JNIEnv *, jobject, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  CglFlowCover* generator2 = new CglFlowCover();
  model->addCutGenerator(generator2,-1,"FlowCover");    
}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    setAllowableGap
 * Signature: (DJ)Z
 */
JNIEXPORT jboolean JNICALL Java_thebeast_osi_CbcModel_setAllowableGap
  (JNIEnv *, jobject, jdouble value, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  return model->setAllowableGap(value);  
}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    setAllowableFractionGap
 * Signature: (DJ)Z
 */
JNIEXPORT jboolean JNICALL Java_thebeast_osi_CbcModel_setAllowableFractionGap
  (JNIEnv *, jobject, jdouble value, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  return model->setAllowableFractionGap(value);
}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    setAllowablePercentageGap
 * Signature: (DJ)Z
 */
JNIEXPORT jboolean JNICALL Java_thebeast_osi_CbcModel_setAllowablePercentageGap
  (JNIEnv *, jobject, jdouble value, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  return model->setAllowableFractionGap(value);  
}

/*
 * Class:     thebeast_osi_CbcModel
 * Method:    setMaximumNodes
 * Signature: (IJ)Z
 */
JNIEXPORT jboolean JNICALL Java_thebeast_osi_CbcModel_setMaximumNodes
  (JNIEnv *, jclass, jint value, jlong ptr){
  CbcModel* model = (CbcModel*) ptr;
  return model->setMaximumNodes(value);  

}
