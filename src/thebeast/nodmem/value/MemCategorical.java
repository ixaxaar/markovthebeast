package thebeast.nodmem.value;

import thebeast.nod.value.ValueVisitor;
import thebeast.nod.value.CategoricalValue;
import thebeast.nod.type.CategoricalType;
import thebeast.nodmem.mem.MemChunk;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.type.MemCategoricalType;

/**
 * @author Sebastian Riedel
 */
public class MemCategorical extends AbstractMemValue<CategoricalType> implements CategoricalValue {

    private MemChunk chunk;
    private int pointer;

    public MemCategorical(MemChunk chunk, int pointer, CategoricalType type) {
        super(type);
        this.chunk = chunk;
        this.pointer = pointer;
    }

    public void acceptValueVisitor(ValueVisitor visitor) {
        visitor.visitCategorical(this);
    }

    public String representation() {
        return ((MemCategoricalType)type).representation(chunk.intData[pointer]);
    }

    public int index() {
        return chunk.intData[pointer];
    }

     public int hashCode() {
        return chunk.intData[pointer];
    }

    public boolean equals(Object object) {
        if (object instanceof MemCategorical) {
            MemCategorical memCategorical = (MemCategorical) object;
            return memCategorical.chunk.intData[memCategorical.pointer] == chunk.intData[pointer];
        }
        return false;
    }

    void setPointer(MemVector pointer) {
        this.pointer = pointer.xInt;
    }


    public void copyFrom(AbstractMemValue v) {
        MemCategorical other = (MemCategorical) v;
        chunk.intData[pointer] = other.chunk.intData[other.pointer]; 
    }
}
