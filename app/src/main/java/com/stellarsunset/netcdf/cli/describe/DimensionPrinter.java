package com.stellarsunset.netcdf.cli.describe;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;

import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

record DimensionPrinter() implements FileProcessor {

    private static final JsonFactory FACTORY = new JsonFactory()
            .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

    @Override
    public int process(NetcdfFile file, Writer writer) {
        try (JsonGenerator generator = FACTORY.createGenerator(writer)) {

            int failures = 0;
            IOException composite = new JsonGenerationException("Error writing dimensions in file: " + file.getLocation(), generator);

            for (Dimension dimension : file.getDimensions()) {
                Optional<Exception> error = writeDimension(generator, dimension);

                if (error.isPresent()) {
                    composite.addSuppressed(error.get());
                    failures++;
                }

                if (failures > 10) {
                    throw composite;
                }
            }
        } catch (IOException e) {
            System.err.printf("Error reading dimensions of file: %s. Error was: %s", file.getLocation(), e);
            return 1;
        }
        return 0;
    }

    Optional<Exception> writeDimension(JsonGenerator generator, Dimension dimension) {
        try {
            generator.writeStartObject();
            generator.writeStringField("name", dimension.getName());
            generator.writeNumberField("length", dimension.getLength());
            generator.writeStringField("shortName", dimension.getShortName());
            generator.writeEndObject();
            generator.writeRaw('\n');
            return Optional.empty();
        } catch (IOException e) {
            return Optional.of(e);
        }
    }
}
