package org.example.info;

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
            String header = String.format("DimensionsOf(%s){\n", file.getLocation());
            writer.write(header);
            for (DimensionMatch match : matches) {
                writer.write("\t");
                writer.write(formatter.asText(match));
                writer.write("\n");
            }
            writer.write("}");
        } catch (IOException e) {
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

        static Formatter verbose() {
            return new Verbose();
        }

        String asText(DimensionMatch dimension);

        record Concise() implements Formatter {
            @Override
            public String asText(DimensionMatch dimension) {
                return dimension.dimension().map(this::dimensionText).orElseGet(() -> missingText(dimension.dimensionName()));
            }

            private String dimensionText(Dimension dimension) {
                return String.format("%s(length=%d)", dimension.getName(), dimension.getLength());
            }

            private String missingText(String dimensionName) {
                return String.format("%s(NotFound)", dimensionName);
            }
        }

        record Verbose() implements Formatter {
            @Override
            public String asText(DimensionMatch dimension) {
                return dimension.dimension().map(this::dimensionText).orElseGet(() -> missingText(dimension.dimensionName()));
            }

            private String dimensionText(Dimension dimension) {
                return String.format("%s(length=%d, isShared=%b)", dimension.getName(), dimension.getLength(), dimension.isShared());
            }

            private String missingText(String dimensionName) {
                return String.format("%s(NotFound)", dimensionName);
            }
        }
    }
}
