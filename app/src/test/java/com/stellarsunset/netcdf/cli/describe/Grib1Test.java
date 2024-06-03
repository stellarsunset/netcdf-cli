package com.stellarsunset.netcdf.cli.describe;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class Grib1Test {

    private static final File FILE = new File(System.getProperty("user.dir") + "/src/test/resources/grib/data.grib1");

    @Test
    void testRead() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int code = Describe.make(FILE, true, true).invoke(baos);

        assertAll(
                () -> assertEquals(0, code, "Should terminate with a successful exit code"),
                () -> assertTrue(baos.toByteArray().length > 0, "Should have generated some text")
        );
    }
}
