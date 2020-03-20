description = "Kotlin IDEA Gradle Importing support"

plugins {
    kotlin("jvm")
    id("jps-compatible")
}

jvmTarget = "1.6"

dependencies {
    compile(kotlinStdlib())
    implementation(project(":idea"))
    implementation(project(":idea:idea-core"))
    implementation(project(":idea:idea-gradle-tooling-api"))
    implementation(project(":kotlin-scripting-compiler-impl"))

    compileOnly(intellijDep())
    compileOnly(intellijPluginDep("gradle"))
    compileOnly(intellijDep()) { includeJars("slf4j-api-1.7.25") }
}

sourceSets {
    "main" { projectDefault() }
    "test" {}
}

runtimeJar()

sourcesJar()

javadocJar()

apply(from = "$rootDir/gradle/kotlinPluginPublication.gradle.kts")
