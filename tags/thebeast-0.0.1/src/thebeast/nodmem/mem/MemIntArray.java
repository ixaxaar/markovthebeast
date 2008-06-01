package thebeast.nodmem.mem;

/**
 * @author Sebastian Riedel
 */
public final class MemIntArray {
  public int[] data;

  public MemIntArray(int size) {
    data = new int[size];
  }

  public void increaseCapacity(int howMuch){
    int[] old = data;
    data = new int[old.length + howMuch];
    System.arraycopy(old,0,data,0,old.length);
  }

}
