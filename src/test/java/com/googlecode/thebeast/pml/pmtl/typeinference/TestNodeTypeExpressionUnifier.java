package com.googlecode.thebeast.pml.pmtl.typeinference;

import com.googlecode.thebeast.pml.pmtl.lexer.Lexer;
import com.googlecode.thebeast.pml.pmtl.lexer.LexerException;
import com.googlecode.thebeast.pml.pmtl.node.*;
import com.googlecode.thebeast.pml.pmtl.parser.Parser;
import com.googlecode.thebeast.pml.pmtl.parser.ParserException;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.List;

/**
 * @author Sebastian Riedel
 */
public class TestNodeTypeExpressionUnifier {

    @Test
    public void testUnification() throws ParserException, LexerException, IOException {
        Signature signature = SQLSignature.createSignature();
        signature.interpret("type TypeA: A,B,C; predicate pred: TypeA x TypeA;");

        Parser parser = new Parser(new Lexer(new PushbackReader(
            new StringReader("(pred(y,x) ^ pred(x,A)) => pred(x,y)"))));
        Start start = parser.parse();
        AFormulaPmtl aPmtl = (AFormulaPmtl) start.getPPmtl();
        AAtomComposable aAtomComposable =  (AAtomComposable)((AImpliesComposable)aPmtl.getComposable()).getArg2();
        AAtom aAtom = (AAtom) aAtomComposable.getAtom();

        List<NodeTypeEquation> equations = TypeEquationExtractor.extractEquations(signature, aPmtl);
        NodeTypeSubstitution result = NodeTypeExpressionUnifier.unify(equations);
        assertEquals(result.getType(aAtom.getArgs().get(0)), signature.getType("TypeA"));

    }
}
