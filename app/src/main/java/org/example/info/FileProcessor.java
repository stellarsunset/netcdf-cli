package org.example.info;

import ucar.nc2.NetcdfFile;

import java.io.Writer;
import java.util.Set;

sealed interface FileProcessor permits DimensionPrinter, VariablePrinter {

    static FileProcessor dimensionPrinter(DimensionPrinter.Formatter formatter, Set<String> dimensions) {
        return new DimensionPrinter(formatter, dimensions);
    }

    static FileProcessor variablePrinter(VariablePrinter.Formatter formatter, Set<String> dimensions, Set<String> variables) {
        return new VariablePrinter(formatter, dimensions, variables);
    }

    /**
     * Process the contents of a {@link NetcdfFile} potentially writing content to the provided writer.
     *
     * @param file   the file to process
     * @param writer the writer to use to publish processed information
     * @return an error code or 0 for success
     */
    int process(NetcdfFile file, Writer writer);
}
