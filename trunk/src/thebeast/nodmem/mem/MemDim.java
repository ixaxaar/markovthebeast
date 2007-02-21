package thebeast.nodmem.mem;

/**
 * @author Sebastian Riedel
 */
public class MemDim extends MemVector {

    public MemDim() {
    }

    public MemDim(MemVector pointer) {
        super(pointer);
    }

    public MemDim(int xInt, int xDouble, int xChunk) {
        super(xInt, xDouble, xChunk);
    }
}
