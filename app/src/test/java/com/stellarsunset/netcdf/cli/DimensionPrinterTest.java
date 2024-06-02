package com.stellarsunset.netcdf.cli;

import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

class DimensionPrinterTest {

    private static final File FILE = new File(System.getProperty("user.dir") + "/src/test/resources/grib/data.grib2");

    /**
     * Quickly visualize the formatting for a few variables.
     */
    @Test
    void conciseFormat_SmokeTest() {
        try (NetcdfFile file = NetcdfFiles.open(FILE.getAbsolutePath());
             Writer writer = new PrintWriter(System.out)) {

            new DimensionPrinter(DimensionPrinter.Formatter.concise(), Set.of())
                    .process(file, writer);

        } catch (IOException e) {
            fail(e);
        }
    }
}
