import com.rohanprabhu.gradle.plugins.kdjooq.*

plugins {
    java
    kotlin("jvm") version "1.3.61"
    id("com.rohanprabhu.kotlin-dsl-jooq") version "0.4.4"
    kotlin("plugin.serialization") version "1.3.61"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":framework"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    testImplementation("junit", "junit", "4.12")
    implementation("org.jooq:jooq:3.12.4")
    implementation("org.jooq:jooq-meta:3.12.4")
    implementation("org.jooq:jooq-codegen:3.12.4")
    implementation("io.undertow:undertow-core:2.0.1.Final")
    implementation("org.postgresql:postgresql:42.2.5")
    jooqGeneratorRuntime("org.postgresql:postgresql:42.2.5")
    implementation("com.zaxxer:HikariCP:3.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

jooqGenerator {
    configuration("primary", sourceSets.getByName("main")) {
        configuration = jooqCodegenConfiguration {
            jdbc {
                username = "undertow"
                password = "12345"
                driver = "org.postgresql.Driver"
                url = "jdbc:postgresql://localhost:5432/postgres"
            }

            generator {
                target {
                    packageName = "xyz.mazuninky.jooq"
                    directory = "${project.buildDir}/generated/jooq/primary"
                }

                database {
                    name = "org.jooq.meta.postgres.PostgresDatabase"
                    inputSchema = "public"
                }
            }
        }
    }
}
