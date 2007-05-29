package thebeast.nodmem.type;

import thebeast.nod.type.*;
import thebeast.nod.value.Value;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.value.AbstractMemValue;
import thebeast.nodmem.value.MemRelation;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Sebastian Riedel
 */
public class MemRelationType extends AbstractMemType implements RelationType {

  private MemHeading heading;
  private LinkedList<KeyAttributes> candidateKeys;

  public MemRelationType(MemHeading heading) {
    this.heading = heading;
    setDim(heading.getDim());
    candidateKeys = new LinkedList<KeyAttributes>();
    candidateKeys.add(new MemKeyAttributes(heading, heading.attributes()));
  }

  public MemRelationType(MemHeading heading, List<KeyAttributes> candidates) {
    this.heading = heading;
    setDim(heading.getDim());
    candidateKeys = new LinkedList<KeyAttributes>(candidates);
  }


  public void acceptTypeVisitor(TypeVisitor visitor) {
    visitor.visitRelationType(this);
  }

  public MemHeading heading() {
    return heading;
  }

  public List<KeyAttributes> candidateKeys() {
    return candidateKeys;
  }

  public Type instanceType() {
    return new MemTupleType(heading);
  }

  public Value emptyValue() {
    return null;
  }

  public AbstractMemValue valueFromChunk(MemChunk chunk, MemVector pointer) {
    MemChunk memChunk = chunk.chunkData[pointer.xChunk];
    assert memChunk != null;
    return new MemRelation(memChunk, new MemVector(), this);
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("RELATION {");
    buffer.append(heading);
    buffer.append("}");
    return buffer.toString();
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MemRelationType that = (MemRelationType) o;

    return heading.equals(that.heading);

  }

  public int hashCode() {
    return heading.hashCode();
  }

  public void loadFromRows(InputStream is, MemChunk dst, MemVector ptr) throws IOException {
    //actually we need the inside chunk
    dst = dst.chunkData[ptr.xChunk];
    dst.size = 0;
//    BufferedReader r = new BufferedReader(new InputStreamReader(is));
//    r.mark(3);
//    int first = r.read();
//    if (first == '#') {
//      String line = r.readLine().trim();
//      int length = Integer.valueOf(line);
//      if (dst.capacity < length) dst.increaseCapacity(length - dst.capacity);
//    } else {
//      r.reset();
//    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    int lineNr = 0;
    MemVector current = new MemVector(ptr);    
    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
      StringTokenizer tokenizer = new StringTokenizer(line,"[\t ]",false);
      //String[] split = line.split(" \t");
      if (lineNr++ >= dst.capacity) dst.increaseCapacity(40);
      int index = 0;
      for (Attribute attribute : heading.attributes()) {
        AbstractMemType type = (AbstractMemType) attribute.type();
        MemVector local = new MemVector(current);
        local.add(heading.pointerForIndex(index++));
        type.load(tokenizer.nextToken(), dst, local);
      }
      current.add(getDim());
      ++dst.size;

    }
    dst.unify();

//    StreamTokenizer tokenizer = new StreamTokenizer(r);
//
//    //tokenizer.resetSyntax();
//    tokenizer.parseNumbers();
//    tokenizer.wordChars('"', '"');
//    tokenizer.wordChars('\'', '\'');
//    //tokenizer.quoteChar('"');
//    //tokenizer.quoteChar('\'');
//    tokenizer.whitespaceChars(' ', ' ');
//    tokenizer.whitespaceChars('\t', '\t');
//    tokenizer.whitespaceChars('\n', '\n');
//    //tokenizer.eolIsSignificant(false);
//    MemVector current = new MemVector(ptr);
//    int line = 0;
//    while (tokenizer.nextToken() != StreamTokenizer.TT_EOF)
//
//    {
//      if (line++ >= dst.capacity) dst.increaseCapacity(40);
//      tokenizer.pushBack();
//      int index = 0;
//      System.out.print(line + " ");
//      for (Attribute attribute : heading.attributes()) {
//        AbstractMemType type = (AbstractMemType) attribute.type();
//        MemVector local = new MemVector(current);
//        local.add(heading.pointerForIndex(index++));
//        type.load(tokenizer, dst, local);
//      }
//      current.add(getDim());
//      ++dst.size;
//    }
//    //System.out.println(dst.size);
//    dst.unify();
    //System.out.println(dst.size);
  }


}
