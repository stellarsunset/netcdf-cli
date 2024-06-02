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
            writer.write("\n");
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

            private static final String ROW_PATTERN = "|%-30s|%-8s|";

            private static final int VAR_MAX = 30;

            private static final int LEN_MAX = 8;

            @Override
            public String header() {
                return String.join(
                        "\n",
                        String.format(ROW_PATTERN, "dimension", "length"),
                        String.format(ROW_PATTERN, "-".repeat(VAR_MAX), "-".repeat(LEN_MAX))
                );
            }

            @Override
            public String asText(DimensionMatch dimension) {
                return dimension.dimension().map(this::dimensionText).orElseGet(() -> missingText(dimension.dimensionName()));
            }

            String dimensionText(Dimension dimension) {
                return String.format(ROW_PATTERN,
                        truncateTo(dimension.getName(), VAR_MAX),
                        dimension.getLength()
                );
            }

            String missingText(String dimensionName) {
                return String.format(ROW_PATTERN,
                        truncateTo(dimensionName, VAR_MAX - 1) + "?",
                        "0"
                );
            }

            String truncateTo(String text, int maxChars) {
                int n = Math.min(text.length(), maxChars - 3);
                return text.substring(0, n) + (n == text.length() ? "" : "...");
            }
        }
    }
}
