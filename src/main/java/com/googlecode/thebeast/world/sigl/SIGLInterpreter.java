package com.googlecode.thebeast.world.sigl;

import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.Type;
import com.googlecode.thebeast.world.sigl.analysis.DepthFirstAdapter;
import com.googlecode.thebeast.world.sigl.lexer.Lexer;
import com.googlecode.thebeast.world.sigl.lexer.LexerException;
import com.googlecode.thebeast.world.sigl.node.*;
import com.googlecode.thebeast.world.sigl.parser.Parser;
import com.googlecode.thebeast.world.sigl.parser.ParserException;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * A SIGLInterpreter interprets PML Signature Language (SIGL) statements and programs in order to create types and
 * predicates of a signature.
 *
 * <p>The SIGL format can be described through the following grammar.
 * <pre>
 * start = sigl_statement | (sigl_statement ';')+
 * sigl_statement = type_statement | pred_statement
 * type_statement = 'type' id ':' constant (',' constant)*
 * pred_statement = 'predicate' id ':' type ('x' type)*
 * </pre>
 *
 * For example
 * <pre>
 * type Type: A,B,C;
 * predicate pred: Type x Type;
 * </pre>
 *
 * @author Sebastian Riedel
 */
public class SIGLInterpreter extends DepthFirstAdapter {

    /**
     * A list of constant strings that is constructed within the type declaration subtree.
     */
    private ArrayList<String> constants = new ArrayList<String>();
    /**
     * A list of types built within the predicate declaration substree.
     */
    private ArrayList<Type> types = new ArrayList<Type>();

    /**
     * The signature to modify based on the interpreted statements.
     */
    private Signature signature;

    /**
     * Creates a new SIGLInterpreter that modifies the given signature whenever it interprets SIGL statements.
     *
     * @param signature the signature this SIGLInterpreter should modify.
     */
    public SIGLInterpreter(final Signature signature) {
        this.signature = signature;
    }

    /**
     * Interprets the given string as a
     *
     * @param input the input SIGL string to interpret.
     */
    public void interpret(String input) {
        Parser parser = new Parser(new Lexer(new PushbackReader(new StringReader(input))));
        try {
            Start start = parser.parse();
            start.apply(this);
        } catch (ParserException e) {
            e.printStackTrace();
        } catch (LexerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void inAIdTypeConstant(AIdTypeConstant node) {
        constants.add(node.getIdentifier().getText());
    }

    @Override
    public void inAStringTypeConstant(AStringTypeConstant node) {
        constants.add(node.getStringLiteral().getText());
    }


    @Override
    public void inATypeDeclation(ATypeDeclation node) {
        constants.clear();
    }

    @Override
    public void outATypeDeclation(ATypeDeclation node) {
        signature.createType(node.getIdentifier().getText(), false, constants.toArray(new String[constants.size()]));
    }

    @Override
    public void outAArgType(AArgType node) {
        types.add(signature.getType(node.getIdentifier().getText()));
    }

    @Override
    public void inAPredDeclaration(APredDeclaration node) {
        types.clear();
    }

    @Override
    public void outAPredDeclaration(APredDeclaration node) {
        signature.createPredicate(node.getIdentifier().getText(), types);
    }

}
