package thebeast.nodmem.type;

import thebeast.nod.type.Attribute;
import thebeast.nod.type.KeyAttributes;
import thebeast.nodmem.mem.MemColumnSelector;
import thebeast.nodmem.mem.MemVector;
import thebeast.nodmem.mem.MemPointer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class MemKeyAttributes implements KeyAttributes {

    private ArrayList<Attribute> attributes;
    private MemColumnSelector selector;

    public MemKeyAttributes(MemHeading heading, List<Attribute> attributes) {
        this.attributes = new ArrayList<Attribute>(attributes);
        ArrayList<Integer> intCols = new ArrayList<Integer>();
        ArrayList<Integer> doubleCols = new ArrayList<Integer>();
        ArrayList<Integer> chunkCols = new ArrayList<Integer>();
        for (Attribute attribute : attributes){
            MemPointer memPointer = heading.pointerForAttribute(attribute.name());
            switch(memPointer.type){
                case INT: intCols.add(memPointer.pointer); break;
                case CHUNK: doubleCols.add(memPointer.pointer); break;
                case DOUBLE: chunkCols.add(memPointer.pointer); break;         
            }
        }
        int[] intArray = new int[intCols.size()];
        for (int i = 0; i < intArray.length;++i) intArray[i] = intCols.get(i);
        int[] doubleArray = new int[doubleCols.size()];
        for (int i = 0; i < doubleArray.length;++i) doubleArray[i] = doubleCols.get(i);
        int[] chunkArray = new int[chunkCols.size()];
        for (int i = 0; i < chunkArray.length;++i) chunkArray[i] = chunkCols.get(i);
        selector = new MemColumnSelector(intArray,doubleArray, chunkArray);
    }

    public List<Attribute> attributes() {
        return attributes;
    }


    public MemColumnSelector getSelector() {
        return selector;
    }
}
