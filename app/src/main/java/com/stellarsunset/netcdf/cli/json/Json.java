package com.stellarsunset.netcdf.cli.json;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

@Command(
        name = "json",
        description = "Convert a Netcdf file to a stream of JSON records."
)
public final class Json implements Callable<Integer> {

    @Parameters(index = "0", description = "The file to extract content from as JSON.")
    private File file;

    @Override
    public Integer call() throws Exception {
        System.out.println("Hello from Json command");
        return 0;
    }
}
