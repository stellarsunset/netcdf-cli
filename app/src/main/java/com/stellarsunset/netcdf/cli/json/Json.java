package com.stellarsunset.netcdf.cli.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.stellarsunset.netcdf.NetcdfRecordReader;
import com.stellarsunset.netcdf.SchemaBinding;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.concurrent.Callable;

@Command(
        name = "json",
        description = "Convert a Netcdf file to a stream of JSON records."
)
public final class Json implements Callable<Integer> {

    private static final JsonFactory FACTORY = new JsonFactory()
            .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

    @Parameters(index = "0", description = "The file to extract content from as JSON.")
    private File file;

    @Option(
            names = {"-d", "--dimension-variables"},
            arity = "1..7",
            description = "the dimension variables to include in the final json output records"
    )
    private String[] dimensionVariables;

    @Option(
            names = {"-c", "--coordinate-variables"},
            arity = "1..7",
            description = "the coordinate variables to include in the final json output records"
    )
    private String[] coordinateVariables;

    @Override
    public Integer call() throws Exception {
        try (NetcdfFile netcdfFile = NetcdfFiles.open(file.getAbsolutePath());
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
             JsonGenerator generator = FACTORY.createGenerator(writer)) {

            SchemaBinding<JsonGenerator> binding = BindingMaker.createBindingFor(
                    netcdfFile,
                    parseBinding(dimensionVariables, coordinateVariables),
                    writer,
                    generator
            );

            var sameGenerator = NetcdfRecordReader.schemaBound(binding)
                    .read(netcdfFile)
                    .reduce(generator, (g1, g2) -> g2);
        }
        return 0;
    }

    static JsonBinding parseBinding(String[] dimensionVariables, String[] coordinateVariables) {

        JsonBinding.Builder builder = JsonBinding.builder();

        for (String dimensionVariable : dimensionVariables) {
            builder.addDimensionVariable(parseVariable(dimensionVariable));
        }

        for (String coordinateVariable : coordinateVariables) {
            builder.addCoordinateVariable(parseVariable(coordinateVariable));
        }

        return builder.build();
    }

    static JsonBinding.AliasedVariable parseVariable(String variable) {
        String[] aliasAndName = variable.split("=");
        if (aliasAndName.length == 1) {
            return new JsonBinding.AliasedVariable(aliasAndName[0], aliasAndName[0]);
        } else {
            return new JsonBinding.AliasedVariable(aliasAndName[0], aliasAndName[1]);
        }
    }
}
