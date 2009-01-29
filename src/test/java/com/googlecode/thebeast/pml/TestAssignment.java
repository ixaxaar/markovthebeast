package com.googlecode.thebeast.pml;

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
    private GroundFactorGraph gmn;
    private UserPredicate observed;
    private UserPredicate hidden;
    private Signature signature;

    @Test
    public void testCreateAssignmentFromRelationalWorldHasValueOneForTrueObservedAtom() {
        World world = signature.createWorld();
        world.getRelation(hidden).setOpen(true);
        world.getRelation(observed).addTuple("A", "A");
        GroundAtomAssignment assignment = new GroundAtomAssignment(gmn, world);
        assertEquals(assignment.getValue(observed, "A", "A"), true);
    }

    @Test
    public void testCreateAssignmentFromRelationalWorldHasValueZeroForFalseObservedAtom() {
        World world = signature.createWorld();
        world.getRelation(hidden).setOpen(true);
        world.getRelation(observed).addTuple("A", "A");
        GroundAtomAssignment assignment = new GroundAtomAssignment(gmn, world);
        assertEquals(assignment.getValue(observed, "A", "B"), false);
    }

    @Test
    public void testCreateAssignmentFromRelationalWorldHasNoValueForHiddenAtom() {
        World world = signature.createWorld();
        world.getRelation(hidden).setOpen(true);
        GroundAtomAssignment assignment = new GroundAtomAssignment(gmn, world);
        assertEquals(assignment.hasValue(hidden, "A", "B"), false);
    }

    @Test
    public void testCreateWorldFromAssignmentContainsTupleInAssignment(){
        World observation = signature.createWorld();
        observation.getRelation(hidden).setOpen(true);
        observation.getRelation(observed).addTuple("A", "A");
        GroundAtomAssignment assignment = new GroundAtomAssignment(gmn, observation);
        assignment.setValue(true, hidden, "A", "A");
        World result = assignment.createWorld(observation);
        assertTrue(result.containsGroundAtom(hidden, "A", "A"));
    }

      @Test
    public void testCreateWorldFromAssignmentDoesNotContainTupleWithValueZero(){
        World observation = signature.createWorld();
        observation.getRelation(hidden).setOpen(true);
        observation.getRelation(observed).addTuple("A", "A");
        GroundAtomAssignment assignment = new GroundAtomAssignment(gmn, observation);
        assignment.setValue(true, hidden, "A", "B");
        World result = assignment.createWorld(observation);
        assertFalse(result.containsGroundAtom(hidden, "A", "A"));
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        gmn = new GroundFactorGraph();
        signature = SQLSignature.createSignature();
        UserType type = signature.createType("Type", false, "A", "B");
        hidden = signature.createPredicate("hidden", type, type);
        observed = signature.createPredicate("observed", type, type);
        PMLFormula formula = PMLFormula.createFormula(signature, "observed(x,y)=>hidden(x,y)");

        gmn.ground(formula, "x/A y/A", "x/A y/B");
    }
}
