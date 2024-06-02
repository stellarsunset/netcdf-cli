# Re-generate the reflection/resource config files we need for NetCDF + Graal based on our unit tests
configure:
   ./gradlew -Pagent test
   cp -r ./app/build/native/agent-output/test/{reflect-config.json,resource-config.json} ./app/src/config

make: configure
   ./gradlew nativeCompile

dimensions file:
   ./app/build/native/nativeCompile/netcdf {{file}} -d

variables file:
   ./app/build/native/nativeCompile/netcdf {{file}} -v