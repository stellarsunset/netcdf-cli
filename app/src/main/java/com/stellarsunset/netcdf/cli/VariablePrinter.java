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
        return dimensions.isEmpty()
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
            @Override
            public String header() {
                return String.format("%30s%50s%15s", "variable", "dimensions", "type");
            }

            @Override
            public String asText(VariableMatch variable) {
                return variable.variable().map(this::variableText).orElseGet(() -> missingText(variable.variableName()));
            }

            private String variableText(Variable variable) {
                return String.format("%30s%50s%15s", variable.getFullName(), dimensionsText(variable.getDimensions()), variable.getDataType());
            }

            private String dimensionsText(List<Dimension> dimensions) {
                return String.format("[%s]", dimensions.stream().map(Dimension::getName).collect(joining(",")));
            }

            private String missingText(String variableName) {
                return String.format("%30s%50s%15s?", variableName, "[]", "NONE");
            }
        }

        record Verbose() implements Formatter {
            @Override
            public String header() {
                return String.format("%30s%50s%15s%200s", "variable", "dimensions", "type", "description");
            }

            @Override
            public String asText(VariableMatch variable) {
                return variable.variable().map(this::variableText).orElseGet(() -> missingText(variable.variableName()));
            }

            private String variableText(Variable variable) {
                return String.format("%30s%50s%15s%200s", variable.getFullName(), dimensionsText(variable.getDimensions()), variable.getDataType(), variable.getDescription());
            }

            private String dimensionsText(List<Dimension> dimensions) {
                return String.format("[%s]", dimensions.stream().map(Dimension::getName).collect(joining(",")));
            }

            private String missingText(String variableName) {
                return String.format("%30s%50s%15s%200s?", variableName, "[]", "NONE", "NONE");
            }
        }
    }
}
