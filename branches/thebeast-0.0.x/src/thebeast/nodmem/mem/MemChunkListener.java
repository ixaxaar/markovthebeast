package thebeast.nodmem.mem;

/**
 * @author Sebastian Riedel
 */
public interface MemChunkListener {
    void rowsAdded(MemChunk memChunk, int howMany);

    void cleared(MemChunk memChunk);

    void modified(MemChunk memChunk);
}
