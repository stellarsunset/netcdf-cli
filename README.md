# netcdf cli

[![Test](https://github.com/stellarsunset/netcdf-cli/actions/workflows/test.yaml/badge.svg)](https://github.com/stellarsunset/netcdf-cli/actions/workflows/test.yaml)

Minimal [CLI](https://picocli.info) tool for working with [netcdf](https://github.com/stellarsunset/netcdf) supported
data files.

## usage

compile the cli yourself for whatever your target platform is via graal

```bash
$ sdk use java 23.0.2-graalce

# build the binary
$ just binary

# test out the binary locally
$ just cli help
Usage: <main class> [-v] [COMMAND]
  -v, --version
Commands:
  help      Display help information about the specified command.
  json      Convert a Netcdf file to a stream of JSON records.
  describe  Describe the contents of a NetCDF file in terms of dimensions and
              variables.

# show help docs for subcommands          
$ just cli help describe
Usage: <main class> describe [-cds] <file>
Describe the contents of a NetCDF file in terms of dimensions and variables.
      <file>               the file to analyze
  -c, --coordinate-variables
                           print information about all 'coordinate' variables
                             in a file, that is variables which vary along
                             greater than one dimension
  -d, --dimension-variables
                           print information about all the 'dimension'
                             variables in the file, that is variables which
                             vary only along a single dimension
  -s, --scalar-variables   print information about all 'scalar' variables, that
                             is variables with only a single value in the file
                             these may contain coordinate system information,
                             file publication time, etc.
```

use the cli to generate json or explore the contents of various netcdf files