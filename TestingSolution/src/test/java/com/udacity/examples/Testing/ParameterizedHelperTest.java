package com.udacity.examples.Testing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ParameterizedHelperTest {

//    @Test
//    public void test() {
//        fail("Not yet implemented");
//    }

    private String inp;
    private String out;


    public ParameterizedHelperTest(String inp, String out) {
        super();
        this.inp = inp;
        this.out = out;
    }


    @Parameters
    public static Collection initData() {
        String[][] empNames = {{"sareeta", "sareeta"}, {"john", "john"}};
        return Arrays.asList(empNames);
    }


    /**
     * without parameters
     */
    @Test
    public void verify_number_is_the_same() {
        assertEquals(inp, out);
    }

}
