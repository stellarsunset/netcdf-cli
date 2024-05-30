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
    runtimeOnly(libs.grib)
    runtimeOnly(libs.bufr)
    runtimeOnly(libs.opendap)

    implementation(libs.guava)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass = "org.example.Netcdf"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

graalvmNative {
    binaries {
        all {
            resources.autodetect()
            javaLauncher = javaToolchains.launcherFor {
                languageVersion = JavaLanguageVersion.of(21)
            }
        }
    }
}