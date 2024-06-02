package com.stellarsunset.netcdf.cli;

import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.joining;

record VariablePrinter(Formatter formatter, Set<String> dimensions, Set<String> variables) implements FileProcessor {

    @Override
    public int process(NetcdfFile file, Writer writer) {
        Iterable<VariableMatch> matches = () -> variableIterator(file);
        try {
            writer.write(formatter.header());
            writer.write("\n");
            for (VariableMatch match : matches) {
                writer.write(formatter.asText(match));
                writer.write("\n");
            }
        } catch (IOException e) {
            System.err.printf("Error reading variables of file: %s. Error was: %s", file.getLocation(), e);
            return 1;
        }
        return 0;
    }

    private Iterator<VariableMatch> variableIterator(NetcdfFile file) {
        return variables.isEmpty()
                ? file.getVariables().stream().filter(dimensionsMatch()).map(VariableMatch::new).iterator()
                : variables.stream().map(vn -> new VariableMatch(vn, variableFor(file, vn))).iterator();
    }

    private Optional<Variable> variableFor(NetcdfFile file, String variableName) {
        return Optional.ofNullable(file.findVariable(variableName)).filter(dimensionsMatch());
    }

    private Predicate<Variable> dimensionsMatch() {
        return dimensions.isEmpty()
                ? v -> true
                : v -> v.getDimensions().stream().allMatch(d -> dimensions.contains(d.getName()));
    }

    public record VariableMatch(String variableName, Optional<Variable> variable) {

        VariableMatch(String variableName) {
            this(variableName, Optional.empty());
        }

        VariableMatch(Variable variable) {
            this(variable.getFullName(), variable);
        }

        VariableMatch(String variableName, Variable variable) {
            this(variableName, Optional.of(variable));
        }
    }

    sealed interface Formatter {

        static Formatter concise() {
            return new Concise();
        }

        static Formatter verbose() {
            return new Verbose();
        }

        String header();

        String asText(VariableMatch variable);

        record Concise() implements Formatter {

            private static final String ROW_PATTERN = "|%-40s|%-75s|%-15s|";

            private static final int VAR_MAX = 40;

            private static final int DIM_MAX = 75;

            @Override
            public String header() {
                return String.join(
                        "\n",
                        String.format(ROW_PATTERN, "variable", "dimensions", "type"),
                        String.format(ROW_PATTERN, "-".repeat(VAR_MAX), "-".repeat(DIM_MAX), "-".repeat(15))
                );
            }

            @Override
            public String asText(VariableMatch variable) {
                return variable.variable().map(this::variableText).orElseGet(() -> missingText(variable.variableName()));
            }

            String variableText(Variable variable) {
                return String.format(ROW_PATTERN,
                        truncateTo(variable.getFullName(), VAR_MAX),
                        dimensionsText(variable.getDimensions()),
                        variable.getDataType()
                );
            }

            String dimensionsText(List<Dimension> dimensions) {

                int maxWidth = DIM_MAX / Math.max(dimensions.size(), 1);

                String dimensionsText = dimensions.stream()
                        .map(Dimension::getName)
                        .map(dn -> truncateTo(dn, maxWidth))
                        .collect(joining(","));

                return String.format("[%s]", dimensionsText);
            }

            String missingText(String variableName) {
                return String.format(ROW_PATTERN, truncateTo(variableName, VAR_MAX - 1) + "?", "[]", "NONE");
            }

            String truncateTo(String text, int maxChars) {
                if (text.length() <= maxChars) {
                    return text;
                } else {
                    return text.substring(0, maxChars - 3) + "...";
                }
            }
        }

        record Verbose() implements Formatter {

            private static final String ROW_PATTERN = "|%-75s|%-75s|%-15s|%-200s|";

            private static final int VAR_MAX = 75;

            private static final int DIM_MAX = 75;

            private static final int DESC_MAX = 200;

            @Override
            public String header() {
                return String.join(
                        "\n",
                        String.format(ROW_PATTERN, "variable", "dimensions", "type", "description"),
                        String.format(ROW_PATTERN, "-".repeat(VAR_MAX), "-".repeat(DIM_MAX), "-".repeat(15), "-".repeat(DESC_MAX))
                );
            }

            @Override
            public String asText(VariableMatch variable) {
                return variable.variable().map(this::variableText).orElseGet(() -> missingText(variable.variableName()));
            }

            String variableText(Variable variable) {
                return String.format(ROW_PATTERN,
                        truncateTo(variable.getFullName(), VAR_MAX),
                        dimensionsText(variable.getDimensions()),
                        variable.getDataType(),
                        truncateTo(variable.getDescription(), DESC_MAX)
                );
            }

            String dimensionsText(List<Dimension> dimensions) {

                int maxWidth = DIM_MAX / Math.max(dimensions.size(), 1);

                String dimensionsText = dimensions.stream()
                        .map(Dimension::getName)
                        .map(dn -> truncateTo(dn, maxWidth))
                        .collect(joining(","));

                return String.format("[%s]", dimensionsText);
            }

            String missingText(String variableName) {
                return String.format(ROW_PATTERN, truncateTo(variableName, VAR_MAX - 1) + "?", "[]", "NONE", "NONE");
            }

            String truncateTo(String text, int maxChars) {
                if (text.length() <= maxChars) {
                    return text;
                } else {
                    return text.substring(0, maxChars - 3) + "...";
                }
            }
        }
    }
}
