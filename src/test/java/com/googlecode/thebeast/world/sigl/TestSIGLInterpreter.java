package com.googlecode.thebeast.world.sigl;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import com.googlecode.thebeast.world.sql.SQLSignature;
import com.googlecode.thebeast.world.Signature;

/**
 * @author Sebastian Riedel
 */
public class TestSIGLInterpreter {

    @Test
    public void testCreateTypeMultipleConstants(){
        Signature signature = SQLSignature.createSignature();
        SIGLInterpreter interpreter = new SIGLInterpreter(signature);
        interpreter.interpret("type Type : A,\"B,C,D\"");
        assertEquals(signature.getType("Type").size(), 2);
    }
    @Test
    public void testCreateTypeOneConstant(){
        Signature signature = SQLSignature.createSignature();
        SIGLInterpreter interpreter = new SIGLInterpreter(signature);
        interpreter.interpret("type Type : A");
        assertEquals(signature.getType("Type").size(), 1);
    }

    @Test
    public void testCreateTypeComments(){
        Signature signature = SQLSignature.createSignature();
        SIGLInterpreter interpreter = new SIGLInterpreter(signature);
        interpreter.interpret("//single line comment\n" +
            "type Type : A /* multi\nline\ncomment*/");
        assertEquals(signature.getType("Type").size(), 1);
    }

    @Test
    public void testCreatePredicate(){
        Signature signature = SQLSignature.createSignature();
        SIGLInterpreter interpreter = new SIGLInterpreter(signature);
        interpreter.interpret("type TypeA : A,\"B,C,D\"");
        interpreter.interpret("type TypeB : B,\"Hallo Du\"");
        interpreter.interpret("predicate pred: TypeA x TypeB");
        assertEquals(signature.getPredicate("pred").getArgumentTypes().size(), 2);
    }

    @Test
    public void testInterpretMultiStatements(){
        Signature signature = SQLSignature.createSignature();
        SIGLInterpreter interpreter = new SIGLInterpreter(signature);
        interpreter.interpret("type TypeA : A; type TypeB: B;\n predicate pred: TypeA x TypeB;");
        assertEquals(signature.getPredicate("pred").getArgumentTypes().size(), 2);
    }


}
