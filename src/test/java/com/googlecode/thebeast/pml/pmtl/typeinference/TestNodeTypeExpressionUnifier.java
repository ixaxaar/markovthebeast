package com.googlecode.thebeast.pml.pmtl.typeinference;

import com.googlecode.thebeast.pml.pmtl.lexer.Lexer;
import com.googlecode.thebeast.pml.pmtl.lexer.LexerException;
import com.googlecode.thebeast.pml.pmtl.node.AAtom;
import com.googlecode.thebeast.pml.pmtl.node.AClause;
import com.googlecode.thebeast.pml.pmtl.node.APmtl;
import com.googlecode.thebeast.pml.pmtl.node.Start;
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
        signature.interpret("type TypeA: a,b,c; predicate pred: TypeA x TypeA;");

        Parser parser = new Parser(new Lexer(new PushbackReader(
            new StringReader("pred(X,Y) :- pred(Y,X), pred(X,a)"))));
        Start start = parser.parse();
        APmtl aPmtl = (APmtl) start.getPPmtl();
        AClause aClause = (AClause) aPmtl.getClause();
        AAtom aAtom = (AAtom) aClause.getHead();

        List<NodeTypeEquation> equations = TypeEquationExtractor.extractEquations(signature, aClause);
        NodeTypeSubstitution result = NodeTypeExpressionUnifier.unify(equations);
        assertEquals(result.getType(aAtom.getArgs().get(0)), signature.getType("TypeA"));

    }
}
