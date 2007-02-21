package thebeast.nodmem.mem;

/**
 * @author Sebastian Riedel
 */
public class MemVector {

  public int xInt;
  public int xDouble;
  public int xChunk;

  public static MemVector ZERO = new MemVector(0,0,0);


  public MemVector() {
  }

  public MemVector(int row, MemDim dim) {
    this.xInt = row * dim.xInt;
    this.xDouble = row * dim.xDouble;
    this.xChunk = row * dim.xChunk;

  }

  public MemVector(MemVector pointer) {
    this.xInt = pointer.xInt;
    this.xDouble = pointer.xDouble;
    this.xChunk = pointer.xChunk;
  }

  public MemVector(int currentInt, int currentDouble, int currentChunk) {
    this.xInt = currentInt;
    this.xDouble = currentDouble;
    this.xChunk = currentChunk;
  }

  public MemVector(MemPointer pointer) {
    switch (pointer.type) {
      case INT:
        xInt = pointer.pointer;
        break;
      case DOUBLE:
        xDouble = pointer.pointer;
        break;
      case CHUNK:
        xChunk = pointer.pointer;
        break;
    }
  }

  public void add(MemVector op) {
    xInt += op.xInt;
    xDouble += op.xDouble;
    xChunk += op.xChunk;
  }

  public void add(int rows, MemVector op) {
    xInt += rows * op.xInt;
    xDouble += rows * op.xDouble;
    xChunk += rows * op.xChunk;
  }

  public void add(int ints, int doubles, int chunks) {
    xInt += ints;
    xDouble += doubles;
    xChunk += chunks;
  }

  public void add(int rows, int ints, int doubles, int chunks) {
    xInt += rows * ints;
    xDouble += rows * doubles;
    xChunk += rows * chunks;
  }

  public void add(MemPointer pointer) {
    switch (pointer.type) {
      case INT:
        xInt += pointer.pointer;
        break;
      case DOUBLE:
        xDouble += pointer.pointer;
        break;
      case CHUNK:
        xChunk += pointer.pointer;
        break;
    }
  }

  public void shallowCopy(MemVector pointer) {
    this.xInt = pointer.xInt;
    this.xDouble = pointer.xDouble;
    this.xChunk = pointer.xChunk;
  }
}
