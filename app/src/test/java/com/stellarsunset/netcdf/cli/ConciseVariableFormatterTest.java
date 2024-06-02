package com.stellarsunset.netcdf.cli;

import org.junit.jupiter.api.Test;
import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import ucar.nc2.Group;
import ucar.nc2.Variable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConciseVariableFormatterTest {

    private static final VariablePrinter.Formatter.Concise FORMATTER = new VariablePrinter.Formatter.Concise();

    @Test
    void testTruncateTo() {
        assertAll(
                () -> assertEquals("123456789", FORMATTER.truncateTo("123456789", 9), "Exact length should not abbreviate"),
                () -> assertEquals("123...", FORMATTER.truncateTo("123456789", 6), "Over length should abbreviate")
        );
    }

    @Test
    void testMissingText() {
        String missing = FORMATTER.missingText("someVariable");

        assertAll(
                () -> assertTrue(missing.contains("someVariable?"), "Should contain name with '?'"),
                () -> assertTrue(missing.contains("[]"), "Should indicate no dimensions")
        );
    }

    @Test
    void testDimensionsText() {

        Dimension d1 = new Dimension("dimension1", 10);
        Dimension d2 = new Dimension("dimension2", 20);

        assertEquals("[dimension1,dimension2]", FORMATTER.dimensionsText(List.of(d1, d2)));
    }

    @Test
    void testVariableText() {

        Dimension d1 = new Dimension("dimension1", 10);
        Dimension d2 = new Dimension("dimension2", 20);

        Group group = Group.builder()
                .setName("myGroup")
                .addDimensions(List.of(d1, d2))
                .build();

        Variable variable = Variable.builder()
                .setName("myVariable")
                .setDataType(DataType.CHAR)
                .addDimensions(List.of(d1, d2))
                .build(group);

        String text = FORMATTER.variableText(variable);

        assertAll(
                () -> assertTrue(text.contains(variable.getFullName()), "Should contain variable name"),
                () -> assertTrue(text.contains(d1.getName()) && text.contains(d2.getName()), "Dimension names should be included"),
                () -> assertTrue(text.contains(variable.getDataType().toString()), "Should contain variable type")
        );
    }
}
