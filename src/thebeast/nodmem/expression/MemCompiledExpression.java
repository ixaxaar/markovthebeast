package thebeast.nodmem.expression;

import thebeast.nodmem.mem.*;
import thebeast.nodmem.type.MemTupleType;
import thebeast.nod.expression.*;
import thebeast.nod.type.Type;

/**
 * @author Sebastian Riedel
 */
public class MemCompiledExpression{

  MemFunction function;

  public MemCompiledExpression(MemFunction function) {
    this.function = function;
  }

  void evaluate(MemChunk dst, MemVector dstPointer){
    MemEvaluator.evaluate(function,null,null,dst, dstPointer);    
  }
}
  
