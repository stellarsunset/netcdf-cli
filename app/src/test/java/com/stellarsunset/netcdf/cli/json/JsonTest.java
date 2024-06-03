package com.stellarsunset.netcdf.cli.json;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonTest {

    @Test
    void testParseBinding() {

        JsonBinding binding = Json.parseBinding(
                new String[]{"a=aDimensionVar", "bDimensionVar"},
                new String[]{"c=cCoordinateVar", "dCoordinateVar"}
        );

        Set<JsonBinding.AliasedVariable> expectedDimVars = Set.of(
                new JsonBinding.AliasedVariable("a", "aDimensionVar"),
                new JsonBinding.AliasedVariable("bDimensionVar", "bDimensionVar")
        );

        Set<JsonBinding.AliasedVariable> expectedCoordVars = Set.of(
                new JsonBinding.AliasedVariable("c", "cCoordinateVar"),
                new JsonBinding.AliasedVariable("dCoordinateVar","dCoordinateVar")
        );

        assertAll(
                () -> assertEquals(expectedDimVars, binding.dimensionVariables(), "Dimension Variables"),
                () -> assertEquals(expectedCoordVars, binding.coordinateVariables(), "Coordinate Variables")
        );
    }
}
