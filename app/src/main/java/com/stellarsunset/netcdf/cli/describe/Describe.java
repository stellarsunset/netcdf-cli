package com.stellarsunset.netcdf.cli.describe;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

import java.io.*;
import java.util.concurrent.Callable;

import static java.util.Objects.requireNonNull;

@Command(
        name = "describe",
        description = "Describe the contents of a NetCDF file in terms of dimensions and variables."
)
public final class Describe implements Callable<Integer> {

    @Parameters(index = "0", description = "the file to analyze")
    private File file;

    @Option(
            names = {"-d", "--dimension-variables"},
            description = "print information about all the 'dimension' variables in the file, that is variables which vary only " +
                    "along a single dimension"
    )
    private boolean dimensions = false;

    @Option(
            names = {"-c", "--coordinate-variables"},
            description = "print information about all 'coordinate' variables in a file, that is variables which vary along " +
                    "greater than one dimension"
    )
    private boolean variables = false;

    @Override
    public Integer call() {
        return invoke(System.out);
    }

    int invoke(OutputStream outputStream) {
        try (NetcdfFile netcdfFile = NetcdfFiles.open(file.getAbsolutePath());
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {

            if (dimensions) {

                FileProcessor processor = FileProcessor.dimensionJsonWriter();

                int code = processor.process(netcdfFile, writer);
                if (code != 0) {
                    return code;
                }
                writer.newLine();
            }
            if (variables) {

                FileProcessor processor = FileProcessor.variableJsonWriter();

                int code = processor.process(netcdfFile, writer);
                if (code != 0) {
                    return code;
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.printf("Error occurred reading the provided Netcdf file: %s. Error was: %s.%n", file, e);
            return 1;
        }
        return 0;
    }

    /**
     * Useful for instantiating an instance of the command line runner for use in local unit tests.
     */
    static Describe make(File file, boolean dimensions, boolean variables) {
        Describe describe = new Describe();
        describe.file = requireNonNull(file);
        describe.dimensions = dimensions;
        describe.variables = variables;
        return describe;
    }
}
