# Netcdf CLI

[![Test](https://github.com/stellarsunset/netcdf-cli/actions/workflows/test.yaml/badge.svg)](https://github.com/stellarsunset/netcdf-cli/actions/workflows/test.yaml)

Minimal CLI tool for working with [netcdf](https://github.com/stellarsunset/netcdf) supported data files.

### Build

- Activate GraalVM with [SDKMAN](https://sdkman.io/) `sdk use java 21.0.2-graalce` before building
- Run `just make` to build the `netcdf` binary and copy it into the `./bin` directory of the project