package com.stellarsunset.netcdf.cli;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import static java.util.Objects.requireNonNull;

@Command(
        name = "describe",
        description = "Describe the contents of a NetCDF file in terms of dimensions and variables."
)
final class Describe implements Callable<Integer> {

    private static final JsonFactory FACTORY = new JsonFactory()
            .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

    @Parameters(index = "0", description = "the file to analyze")
    private File file;

    @Option(
            names = {"-s", "--scalar-variables"},
            description = "print information about all 'scalar' variables, that is variables with only a single value in the file " +
                    "these may contain coordinate system information, file publication time, etc."
    )
    private boolean scalarVariables = false;

    @Option(
            names = {"-d", "--dimension-variables"},
            description = "print information about all the 'dimension' variables in the file, that is variables which vary only " +
                    "along a single dimension"
    )
    private boolean dimensionVariables = false;

    @Option(
            names = {"-c", "--coordinate-variables"},
            description = "print information about all 'coordinate' variables in a file, that is variables which vary along " +
                    "greater than one dimension"
    )
    private boolean coordinateVariables = false;

    @Override
    public Integer call() {
        return invoke(System.out);
    }

    int invoke(OutputStream outputStream) {
        try (NetcdfFile netcdfFile = NetcdfFiles.open(file.getAbsolutePath());
             JsonGenerator generator = FACTORY.createGenerator(outputStream)) {

            for (Variable variable : netcdfFile.getVariables()) {
                if (shouldWrite(variable)) {
                    writeVariable(variable, generator);
                    generator.writeRaw(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.err.printf("Error occurred reading the provided Netcdf file: %s. Error was: %s.%n", file, e);
            return 1;
        }
        return 0;
    }

    private boolean shouldWrite(Variable variable) {
        int dims = variable.getDimensions().size();
        return (dims == 0 && scalarVariables) || (dims == 1 && dimensionVariables) || (dims > 1 && coordinateVariables);
    }

    /**
     * Write the provided variable to the output stream via the given generator as JSON.
     */
    private void writeVariable(Variable variable, JsonGenerator generator) throws IOException {
        generator.writeStartObject();
        generator.writeStringField("name", variable.getFullName());
        generator.writeStringField("shortName", variable.getShortName());

        generator.writeArrayFieldStart("dimensions");
        for (Dimension dimension : variable.getDimensions()) {
            generator.writeString(dimension.getName());
        }
        generator.writeEndArray();
        generator.writeStringField("type", variable.getDataType().toString());
        generator.writeStringField("description", variable.getDescription());
        generator.writeEndObject();
    }

    /**
     * Useful for instantiating an instance of the command line runner for use in local unit tests.
     */
    static Describe all(File file) {
        Describe describe = new Describe();
        describe.file = requireNonNull(file);
        describe.scalarVariables = true;
        describe.dimensionVariables = true;
        describe.coordinateVariables = true;
        return describe;
    }
}
