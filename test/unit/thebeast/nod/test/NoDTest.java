package thebeast.nod.test;

import junit.framework.TestCase;
import thebeast.nod.NoDServer;
import thebeast.nod.expression.*;
import thebeast.nod.identifier.Name;
import thebeast.nod.statement.Insert;
import thebeast.nod.statement.Interpreter;
import thebeast.nod.statement.StatementFactory;
import thebeast.nod.type.*;
import thebeast.nod.util.ExpressionBuilder;
import thebeast.nod.util.StatementBuilder;
import thebeast.nod.variable.VariableFactory;
import thebeast.nodmem.MemNoDServer;

import java.util.LinkedList;

/**
 * @author Sebastian Riedel
 */
public abstract class NoDTest extends TestCase {

  protected TypeFactory typeFactory;
  protected StatementFactory statementFactory;
  protected NoDServer server;
  protected VariableFactory variableFactory;

  protected Name tokenRelVarID;
  protected Name positionID;
  protected Name wordID;
  protected CategoricalType wordType;
  protected IntType positionType;
  protected Heading tokenHeading;
  protected TupleType tokenTupleType;
  protected RelationType tokenRelationType;
  protected ExpressionFactory expressionFactory;
  protected VariableReference<RelationType> tokenReference;
  protected CategoricalConstant theConstant;
  protected IntConstant zeroConstant;
  protected TupleSelector token0Expr;
  protected Name positionTypeID;
  protected Name wordTypeID;
  protected ExpressionBuilder exprBuilder;
  protected TupleSelector token1Expr;
  protected TupleSelector token2Expr;
  protected TupleSelector token3Expr;
  protected TupleSelector token4Expr;
  protected RelationSelector tokenRelationSelector1;
  protected Insert insertIntoTokens2;
  protected Insert insertIntoTokens1;
  protected RelationSelector tokenRelationSelector2;
  protected Name wordAtbeginID;
  protected DoubleType doubleType;
  protected Name labelID;
  protected Name weightID;
  protected Heading featureHeading;
  protected Name labelTypeID;
  protected CategoricalType labelType;
  protected RelationType featureRelationType;
  protected Attribute wordAtBeginAttribute;
  protected Attribute labelAttribute;
  protected Attribute weightAttribute;
  protected Interpreter interpreter;
  protected Name featureRelVarID;
  protected StatementBuilder stmtBuilder;


  public static NoDServer createServer() {
    return new MemNoDServer();
  }


  protected void setUp() {
    server = NoDTest.createServer();
    typeFactory = server.typeFactory();
    statementFactory = server.statementFactory();
    expressionFactory = server.expressionFactory();
    exprBuilder = new ExpressionBuilder(server);
    stmtBuilder = new StatementBuilder(server);

    createIDs();
    createScalarTypes();
    createAttributes();
    createHeadings();
    createTupleTypes();
    createRelationTypes();
    createVariableReferences();
    createConstants();
    createTupleSelectors();
    createRelationSelectors();
    createStatements();

    interpreter = server.interpreter();

    exprBuilder.clear();

  }

  private void createStatements() {
    //insertIntoTokens1 = statementFactory.createInsert(tokenReference, tokenRelationSelector1);
    //insertIntoTokens2 = statementFactory.createInsert(tokenReference, tokenRelationSelector2);
  }

  private void createIDs() {
    //relation variable
    tokenRelVarID = server.identifierFactory().createName("Token");
    featureRelVarID = server.identifierFactory().createName("Feature");

    positionID = server.identifierFactory().createName("position");
    wordID = server.identifierFactory().createName("word");

    positionTypeID = server.identifierFactory().createName("Position");
    wordTypeID = server.identifierFactory().createName("Word");
    labelTypeID = server.identifierFactory().createName("Label");

    wordAtbeginID = server.identifierFactory().createName("word@begin");
    labelID = server.identifierFactory().createName("label");
    weightID = server.identifierFactory().createName("weight");

  }

  private void createTupleSelectors() {
    exprBuilder.clear();
    exprBuilder.id("position").integer(positionType, 0).id("word").categorical(wordType, "the").tuple();
    token0Expr = (TupleSelector) exprBuilder.getExpression();
    exprBuilder.id("position").integer(positionType, 1).id("word").categorical(wordType, "man").tuple();
    token1Expr = (TupleSelector) exprBuilder.getExpression();
    exprBuilder.id("position").integer(positionType, 2).id("word").categorical(wordType, "likes").tuple();
    token2Expr = (TupleSelector) exprBuilder.getExpression();
    exprBuilder.id("position").integer(positionType, 3).id("word").categorical(wordType, "the").tuple();
    token3Expr = (TupleSelector) exprBuilder.getExpression();
    exprBuilder.id("position").integer(positionType, 4).id("word").categorical(wordType, "boat").tuple();
    token4Expr = (TupleSelector) exprBuilder.getExpression();
  }

  private void createRelationSelectors() {
    exprBuilder.clear();
    exprBuilder.expr(token0Expr).expr(token1Expr).expr(token2Expr).expr(token3Expr).expr(token4Expr);
    exprBuilder.relation(5);
    tokenRelationSelector1 = (RelationSelector) exprBuilder.getExpression();

    exprBuilder.clear();
    exprBuilder.id("position").integer(positionType, 0).id("word").categorical(wordType, "the").tuple(2);
    exprBuilder.id("position").integer(positionType, 1).id("word").categorical(wordType, "boat").tuple(2);
    exprBuilder.id("position").integer(positionType, 2).id("word").categorical(wordType, "likes").tuple(2);
    exprBuilder.id("position").integer(positionType, 3).id("word").categorical(wordType, "the").tuple(2);
    exprBuilder.id("position").integer(positionType, 4).id("word").categorical(wordType, "man").tuple(2);
    exprBuilder.relation(5);
    tokenRelationSelector2 = (RelationSelector) exprBuilder.getExpression();

  }

  private void createConstants() {
    theConstant = expressionFactory.createCategoricalConstant(wordType, "the");
    zeroConstant = expressionFactory.createIntConstant(positionType, 0);

  }

  private void createVariableReferences() {
    tokenReference = expressionFactory.createRelationVariableReference(tokenRelationType, tokenRelVarID);
  }

  private void createRelationTypes() {
    tokenRelationType = typeFactory.createRelationType(tokenHeading);
    featureRelationType = typeFactory.createRelationType(featureHeading);
  }

  private void createTupleTypes() {
    tokenTupleType = typeFactory.createTupleType(tokenHeading);
  }

  private void createHeadings() {
    LinkedList<Attribute> attributes = new LinkedList<Attribute>();
    attributes.add(typeFactory.createAttribute("position",positionType));
    attributes.add(typeFactory.createAttribute("word",wordType));
    tokenHeading = typeFactory.createHeadingFromAttributes(attributes);

    LinkedList<Attribute> attributes2 = new LinkedList<Attribute>();
    attributes2.add(wordAtBeginAttribute);
    attributes2.add(labelAttribute);
    attributes2.add(weightAttribute);
    featureHeading = typeFactory.createHeadingFromAttributes(attributes2);

  }

  private void createAttributes() {
    wordAtBeginAttribute = typeFactory.createAttribute("word@begin", wordType);
    labelAttribute = typeFactory.createAttribute("label", labelType);
    weightAttribute = typeFactory.createAttribute("weight", doubleType);
  }

  private void createScalarTypes() {
    LinkedList<String> words = new LinkedList<String>();
    words.add("the");
    words.add("man");
    words.add("likes");
    words.add("boat");
    wordType = typeFactory.createCategoricalType(wordTypeID, words);

    LinkedList<String> labels = new LinkedList<String>();
    labels.add("NP");
    labels.add("VP");
    labels.add("PP");
    labelType = typeFactory.createCategoricalType(labelTypeID, labels);

    positionType = typeFactory.createIntType(positionTypeID, 0, 5);
    doubleType = server.typeFactory().doubleType();


  }

}
