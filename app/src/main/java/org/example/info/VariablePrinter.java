package org.example.info;

import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.Writer;
import java.util.Set;

record VariablePrinter(Formatter formatter, Set<String> dimensions, Set<String> variables) implements FileProcessor {

    @Override
    public int process(NetcdfFile file, Writer writer) {
        return 0;
    }

    sealed interface Formatter {

        static Formatter concise() {
            return new Concise();
        }

        static Formatter verbose() {
            return new Verbose();
        }

        String asText(Variable variable);

        record Concise() implements Formatter {
            @Override
            public String asText(Variable variable) {
                return "";
            }
        }

        record Verbose() implements Formatter {
            @Override
            public String asText(Variable variable) {
                return "";
            }
        }
    }
}
