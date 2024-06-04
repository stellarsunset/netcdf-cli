package com.stellarsunset.netcdf.cli;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class ZarrTest {

    // In ZARR the data is stored in a folder structure, this allows cloud-hosted services to only load the indexes and
    // variables they need from a cloud data hosting service like S3 as the logical file is split across multiple objects
    private static final File FILE = new File(System.getProperty("user.dir") + "/src/test/resources/zarr/data");

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
