set positional-arguments

default:
   just --list

# generate graal resource/reflection configs from unit test coverage
@configure:
   ./gradlew -Pagent test
   cp -r ./app/build/native/agent-output/run/{reflect-config.json,resource-config.json} ./app/src/config

# run the unit tests of the project
@test:
  ./gradlew test

# build the binary
@binary: configure
   ./gradlew nativeCompile

# invoke the cli binary with the provided arguments (assuming its been built)
@cli *args='':
  ./app/build/native/nativeCompile/netcdf $@
