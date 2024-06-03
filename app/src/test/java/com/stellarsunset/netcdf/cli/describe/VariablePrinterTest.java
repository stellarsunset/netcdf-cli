package com.stellarsunset.netcdf.cli.describe;

import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.fail;

class VariablePrinterTest {

    private static final File FILE = new File(System.getProperty("user.dir") + "/src/test/resources/grib/data.grib2");

    @Test
    void smokeTest() {
        try (NetcdfFile file = NetcdfFiles.open(FILE.getAbsolutePath());
             Writer writer = new PrintWriter(System.out)) {

            new VariablePrinter().process(file, writer);

        } catch (IOException e) {
            fail(e);
        }
    }
}
