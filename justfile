# Re-generate the resource/reflection configs based on the unit tests
configure:
   ./gradlew -Pagent test
   cp -r ./app/build/native/agent-output/test/{reflect-config.json,resource-config.json} ./app/src/config

# Re-build the executable binary
make: configure
   ./gradlew nativeCompile
   mkdir -p ./bin
   cp -rf ./app/build/native/nativeCompile/netcdf ./bin/
