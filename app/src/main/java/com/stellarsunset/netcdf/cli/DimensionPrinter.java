package com.stellarsunset.netcdf.cli;

import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

record DimensionPrinter(Formatter formatter, Set<String> dimensions) implements FileProcessor {

    @Override
    public int process(NetcdfFile file, Writer writer) {
        Iterable<DimensionMatch> matches = () -> dimensionIterator(file, dimensions);
        try {
            writer.write(formatter.header());
            for (DimensionMatch match : matches) {
                writer.write(formatter.asText(match));
                writer.write("\n");
            }
        } catch (IOException e) {
            System.err.printf("Error reading dimensions of file: %s. Error was: %s", file.getLocation(), e);
            return 1;
        }
        return 0;
    }

    @SuppressWarnings("deprecation")
    private Iterator<DimensionMatch> dimensionIterator(NetcdfFile file, Set<String> dimensions) {
        return dimensions.isEmpty()
                ? file.getDimensions().stream().map(DimensionMatch::new).iterator()
                : dimensions.stream().map(dn -> new DimensionMatch(dn, dimensionFor(file, dn))).iterator();
    }

    private Optional<Dimension> dimensionFor(NetcdfFile file, String dimensionName) {
        return Optional.ofNullable(file.findDimension(dimensionName));
    }

    public record DimensionMatch(String dimensionName, Optional<Dimension> dimension) {

        DimensionMatch(String dimensionName) {
            this(dimensionName, Optional.empty());
        }

        DimensionMatch(Dimension dimension) {
            this(dimension.getName(), dimension);
        }

        DimensionMatch(String dimensionName, Dimension dimension) {
            this(dimensionName, Optional.of(dimension));
        }
    }

    sealed interface Formatter {

        static Formatter concise() {
            return new Concise();
        }

        String header();

        String asText(DimensionMatch dimension);

        record Concise() implements Formatter {

            @Override
            public String header() {
                return String.format("%30s%8s", "dimension", "length");
            }

            @Override
            public String asText(DimensionMatch dimension) {
                return dimension.dimension().map(this::dimensionText).orElseGet(() -> missingText(dimension.dimensionName()));
            }

            private String dimensionText(Dimension dimension) {
                return String.format("%30s%8s", dimension.getName(), dimension.getLength());
            }

            private String missingText(String dimensionName) {
                return String.format("%30s%8s?", dimensionName, "0");
            }
        }
    }
}
