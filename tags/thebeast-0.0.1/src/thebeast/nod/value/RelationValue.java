package thebeast.nod.value;

import thebeast.nod.type.RelationType;

import java.util.Iterator;
import java.io.OutputStream;
import java.io.IOException;

/**
 * @author Sebastian Riedel
 */
public interface RelationValue extends SetValue<RelationType>, Iterable<TupleValue> {
  int size();

  Iterator<TupleValue> tuples();

  void writeTo(OutputStream os, boolean header) ;

}
