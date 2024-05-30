package org.example.info;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Set;
import java.util.concurrent.Callable;

@Command(
        name = "info",
        description = "Print information about Netcdf-formatted files."
)
public final class Info implements Callable<Integer> {

    @Parameters(index = "0", description = "the file to analyze")
    private File file;

    @Option(names = {"-d", "--dimensions"}, description = "print information about the dimensions in the file")
    private String[] dimensions = new String[0];

    @Option(names = {"-v", "--variables"}, description = "show information about the variables in the file")
    private String[] variables = new String[0];

    @Option(names = {"-l", "--long"}, description = "include the long format information about dimensions/variables")
    private boolean verbose = false;

    @Override
    public Integer call() throws Exception {
        try (NetcdfFile netcdfFile = NetcdfFiles.open(file.getAbsolutePath());
             Writer writer = new OutputStreamWriter(System.out)) {

            Set<String> dimensionsSet = dimensionsSet();

            if (dimensions != null) {

                FileProcessor processor = FileProcessor.dimensionPrinter(
                        verbose ? DimensionPrinter.Formatter.verbose() : DimensionPrinter.Formatter.concise(),
                        dimensionsSet
                );

                int code = processor.process(netcdfFile, writer);
                if (code != 0) {
                    return code;
                }
            }
            if (variables != null) {

                FileProcessor processor = FileProcessor.variablePrinter(
                        verbose ? VariablePrinter.Formatter.verbose() : VariablePrinter.Formatter.concise(),
                        dimensionsSet,
                        variablesSet()
                );

                int code = processor.process(netcdfFile, writer);
                if (code != 0) {
                    return code;
                }
            }
        }
        return 0;
    }

    private Set<String> dimensionsSet() {
        return dimensions == null ? Set.of() : Set.of(dimensions);
    }

    private Set<String> variablesSet() {
        return variables == null ? Set.of() : Set.of(variables);
    }
}
