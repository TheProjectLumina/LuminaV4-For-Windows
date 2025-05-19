plugins {
    id("java-library")
    alias(libs.plugins.lombok)
    alias(libs.plugins.checkerframework)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}



dependencies {
    api(project(":Protocol:common"))
    api(platform(libs.fastutil.bom))
    api(libs.netty.buffer)
    api(libs.fastutil.long.common)
    api(libs.fastutil.long.obj.maps)
    api(libs.jose4j)
    api(libs.nbt)
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.0")

}