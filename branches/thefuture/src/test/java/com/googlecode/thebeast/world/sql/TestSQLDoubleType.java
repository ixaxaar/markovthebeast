package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.DoubleConstant;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 * @author Sebastian Riedel
 */
public class TestSQLDoubleType {

    /**
     * Test the getConstant methods.
     */
    @Test
    public void testGetConstant() {
        SQLSignature signature = new SQLSignature();
        SQLDoubleType type = new SQLDoubleType("Double", signature);

        assertEquals(0.0, type.getConstant(0).getValue());
        assertEquals(0.0, ((DoubleConstant) type.getConstant("0")).getValue());
        assertEquals(type.getConstant(0.0), type.getConstant("0"));
    }

}