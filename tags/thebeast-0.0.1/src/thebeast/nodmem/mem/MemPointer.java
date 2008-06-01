package thebeast.nodmem.mem;

/**
 * @author Sebastian Riedel
 */
public final class MemPointer {

  public MemChunk.DataType type;
  public int pointer;


  public MemPointer(MemChunk.DataType type, int pointer) {
    this.type = type;
    this.pointer = pointer;
  }

  public MemPointer(MemPointer src, MemVector add){
    this.type = src.type;
    this.pointer = src.pointer;
    add(add);
  }


  public void add(MemVector op) {
    switch (type) {
      case INT:
        pointer += op.xInt;
        break;
      case DOUBLE:
        pointer += op.xDouble;
        break;
      case CHUNK:
        pointer += op.xChunk;
        break;
    }
  }

}
