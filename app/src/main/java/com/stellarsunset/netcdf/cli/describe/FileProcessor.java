package com.stellarsunset.netcdf.cli.describe;

import ucar.nc2.NetcdfFile;

import java.io.Writer;

sealed interface FileProcessor permits DimensionPrinter, VariablePrinter {

    static FileProcessor dimensionJsonWriter() {
        return new DimensionPrinter();
    }

    static FileProcessor variableJsonWriter() {
        return new VariablePrinter();
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
