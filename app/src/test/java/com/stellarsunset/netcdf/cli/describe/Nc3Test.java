package com.stellarsunset.netcdf.cli.describe;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class Nc3Test {

    private static File FILE;

    @BeforeAll
    static void setup(@TempDir Path temp) {
        FILE = temp.resolve("data.nc").toFile();
        new Nc3FileWriter().write(FILE);
    }

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
