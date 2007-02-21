package thebeast.nod.test;

import thebeast.nod.type.*;
import thebeast.nod.value.CategoricalValue;
import thebeast.nod.value.IntValue;
import thebeast.nod.identifier.Name;
import thebeast.util.Pair;

import java.util.LinkedList;

/**
 * @author Sebastian Riedel
 */
public class TestTypeFactory extends NoDTest {

    public void testCreateCategoricalType(){
        LinkedList<String> reps = new LinkedList<String>();
        reps.add("the");
        reps.add("man");
        reps.add("likes");
        reps.add("boat");
        CategoricalType type = typeFactory.createCategoricalType(wordTypeID, reps);
        assertEquals(4,type.values().size());
        for (String rep: reps){
            assertEquals(rep, type.value(rep).representation());
        }
        int index = 0;
        for (CategoricalValue v : type.values()){
            assertEquals(reps.get(index++),v.representation());
        }
        assertEquals(wordType,type);
        assertEquals(type, typeFactory.categoricalType(wordTypeID));
    }

    public void testCreateHeading(){
        LinkedList<Pair<String, Type>> attributes = new LinkedList<Pair<String, Type>>();
        attributes.add(new Pair<String, Type>("position", positionType));
        attributes.add(new Pair<String, Type>("word",wordType));
        Heading heading = typeFactory.createHeading(attributes);
        assertEquals(positionType, heading.getType("position"));
        assertEquals(wordType, heading.getType("word"));
        assertEquals(2, heading.attributes().size());
        assertEquals(positionID,heading.attributes().get(0).name());
        assertEquals(wordID,heading.attributes().get(1).name());

    }

    public void testCreateTupleType(){
        TupleType type = typeFactory.createTupleType(tokenHeading);
        assertEquals(tokenHeading, type.heading());
        assertEquals(tokenTupleType,type);
    }

    public void testCreateRelationType(){
        RelationType type = typeFactory.createRelationType(tokenHeading);
        assertEquals(tokenHeading,type.heading());
        assertEquals(tokenTupleType,type.instanceType());
    }

    public void testCreateIntegerType(){
        IntType type = typeFactory.createIntType(positionTypeID, 0,5);
        assertEquals(0,type.from());
        assertEquals(5,type.to());
        for (int i = 0; i < 5; ++i){
            assertEquals(i,type.value(i).getInt());
        }
        int index = 0;
        for (IntValue v : type.values()){
            assertEquals(index++,v.getInt());    
        }
        assertEquals(5,type.size());
        assertEquals(positionType, type);
        assertEquals(type, typeFactory.intType(positionTypeID));
    }

    public void testCreateDoubleType(){
        DoubleType type = typeFactory.createDoubleType(positionTypeID, -10.0,10.0);
        assertEquals(-10.0,type.from());
        assertEquals(10.0, type.to());
        assertEquals(1.0, type.value(1.0).getDouble());
        assertEquals(-1.0, type.value(-1.0).getDouble());
    }


}
