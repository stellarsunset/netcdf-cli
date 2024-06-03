package com.stellarsunset.netcdf.cli.describe;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

record VariablePrinter() implements FileProcessor {

    private static final JsonFactory FACTORY = new JsonFactory()
            .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

    @Override
    public int process(NetcdfFile file, Writer writer) {
        try (JsonGenerator generator = FACTORY.createGenerator(writer)) {

            int failures = 0;
            IOException composite = new JsonGenerationException("Error writing dimensions in file: " + file.getLocation(), generator);

            for (Variable variable : file.getVariables()) {
                Optional<Exception> error = writeVariable(generator, variable);

                if (error.isPresent()) {
                    composite.addSuppressed(error.get());
                    failures++;
                }

                if (failures > 10) {
                    throw composite;
                }
            }
        } catch (IOException e) {
            System.err.printf("Error reading variables of file: %s. Error was: %s", file.getLocation(), e);
            return 1;
        }
        return 0;
    }

    Optional<Exception> writeVariable(JsonGenerator generator, Variable variable) {
        try {
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
            generator.writeRaw('\n');
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }
}
