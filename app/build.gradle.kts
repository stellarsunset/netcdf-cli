plugins {
    application
    id("org.graalvm.buildtools.native") version "0.9.12"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://artifacts.unidata.ucar.edu/repository/unidata-all/")
        mavenContent {
            releasesOnly()
        }
    }
}

dependencies {

    annotationProcessor(libs.picocli.gen)
    implementation(libs.picocli)

    // Additional file type bindings for netcdf
    implementation(libs.netcdf)
    implementation(libs.grib)
    implementation(libs.bufr)
    implementation(libs.opendap)
    implementation(libs.zarr)

    implementation(libs.guava)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass = "com.stellarsunset.netcdf.cli.Netcdf"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

graalvmNative {
    binaries {
        all {
            imageName = "netcdf"

            javaLauncher = javaToolchains.launcherFor {
                languageVersion = JavaLanguageVersion.of(21)
            }

            resources.autodetect()
            configurationFileDirectories.from(file("src/config"))
        }
    }
}