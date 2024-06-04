package com.stellarsunset.netcdf.cli;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class BufrTest {

    private static final File FILE = new File(System.getProperty("user.dir") + "/src/test/resources/bufr/data.bufr");

    @Test
    void testRead() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int code = Describe.all(FILE).invoke(baos);

        assertAll(
                () -> assertEquals(0, code, "Should terminate with a successful exit code"),
                () -> assertTrue(baos.toByteArray().length > 0, "Should have generated some text")
        );
    }
}
