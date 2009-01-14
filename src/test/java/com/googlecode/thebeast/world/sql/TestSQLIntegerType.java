package com.googlecode.thebeast.world.sql;

import com.googlecode.thebeast.world.IntegerConstant;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 * @author Sebastian Riedel
 */
public class TestSQLIntegerType {

    /**
     * Test the getConstant methods.
     */
    @Test
    public void testGetConstant() {
        SQLSignature signature = new SQLSignature();
        SQLIntegerType type = new SQLIntegerType("Integer", signature);

        assertEquals(0, type.getConstant(0).getValue());
        assertEquals(0, ((IntegerConstant) type.getConstant("0")).getValue());
        assertEquals(type.getConstant(0), type.getConstant("0"));
    }

}