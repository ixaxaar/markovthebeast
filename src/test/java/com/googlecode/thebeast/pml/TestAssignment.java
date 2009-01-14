package com.googlecode.thebeast.pml;

import com.googlecode.thebeast.query.QueryFactory;
import com.googlecode.thebeast.world.Signature;
import com.googlecode.thebeast.world.UserPredicate;
import com.googlecode.thebeast.world.UserType;
import com.googlecode.thebeast.world.World;
import com.googlecode.thebeast.world.sql.SQLSignature;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Sebastian Riedel
 */
public class TestAssignment {
    private GroundMarkovNetwork gmn;
    private UserPredicate observed;
    private UserPredicate hidden;
    private Signature signature;

    @Test
    public void testCreateAssignmentFromRelationalWorldHasValueOneForTrueObservedAtom() {
        World world = signature.createWorld();
        world.getRelation(hidden).setOpen(true);
        world.getRelation(observed).addTuple("A", "A");
        Assignment assignment = new Assignment(gmn, world);
        assertEquals(assignment.getValue(observed, "A", "A"), 1.0);
    }

    @Test
    public void testCreateAssignmentFromRelationalWorldHasValueZeroForFalseObservedAtom() {
        World world = signature.createWorld();
        world.getRelation(hidden).setOpen(true);
        world.getRelation(observed).addTuple("A", "A");
        Assignment assignment = new Assignment(gmn, world);
        assertEquals(assignment.getValue(observed, "A", "B"), 0.0);
    }

    @Test
    public void testCreateAssignmentFromRelationalWorldHasNoValueForHiddenAtom() {
        World world = signature.createWorld();
        world.getRelation(hidden).setOpen(true);
        world.getRelation(observed).addTuple("A", "A");
        Assignment assignment = new Assignment(gmn, world);
        assertEquals(assignment.hasValue(observed, "A", "B"), false);
    }

    @Test
    public void testCreateWorldFromAssignmentContainsTupleWithValueOne(){
        World observation = signature.createWorld();
        observation.getRelation(hidden).setOpen(true);
        observation.getRelation(observed).addTuple("A", "A");
        Assignment assignment = new Assignment(gmn, observation);
        assignment.setValue(1.0, hidden, "B", "B");
        World result = assignment.createWorld(observation);
        assertTrue(result.containsGroundAtom(hidden, "B", "B"));
    }

      @Test
    public void testCreateWorldFromAssignmentDoesNotContainTupleWithValueZero(){
        World observation = signature.createWorld();
        observation.getRelation(hidden).setOpen(true);
        observation.getRelation(observed).addTuple("A", "A");
        Assignment assignment = new Assignment(gmn, observation);
        assignment.setValue(0.0, hidden, "B", "B");
        World result = assignment.createWorld(observation);
        assertFalse(result.containsGroundAtom(hidden, "B", "B"));
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        gmn = new GroundMarkovNetwork();
        signature = SQLSignature.createSignature();
        UserType type = signature.createType("Type", false, "A", "B");
        hidden = signature.createPredicate("hidden", type, type);
        observed = signature.createPredicate("observed", type, type);
        ClauseBuilder builder = new ClauseBuilder(QueryFactory.getInstance(), signature);
        PMLClause clause = builder.atom(observed, "x", "y").head(hidden, "x", "y").clause();
        gmn.ground(clause, "x/A y/A", "x/A y/B");
    }
}
