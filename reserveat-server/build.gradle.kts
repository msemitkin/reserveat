plugins {
    id("java")
    id("org.openapi.generator") version "6.2.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.0.3"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springdoc:springdoc-openapi-ui:1.6.4")
    implementation("org.openapitools:jackson-databind-nullable:0.2.3")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

val generatedSourcesDir = "$buildDir/generated/openapi"

openApiGenerate {
    validateSpec.set(true)
    inputSpec.set("${project.rootDir}/openapi/openapi.yaml")
    generatorName.set("spring")
    outputDir.set(generatedSourcesDir)
    packageName.set("com.reserveat.web")
    apiPackage.set("com.reserveat.web.api")
    modelPackage.set("com.reserveat.web.model")
    modelNameSuffix.set("Dto")
    typeMappings.put("OffsetDateTime", "LocalDateTime")
    importMappings.put("java.time.OffsetDateTime", "java.time.LocalDateTime")
    configOptions.set(
        mutableMapOf(
            "basePackage" to "com.reserveat.web",
            "configPackage" to "com.reserveat.web.configuration",
            "interfaceOnly" to "true",
            "useTags" to "true",
            "performBeanValidation" to "true",
            "useSpringBoot3" to "true"
        )
    )
}
tasks.openApiGenerate {
    doFirst {
        delete(generatedSourcesDir)
    }
    doLast {
        delete(
            "$generatedSourcesDir/.openapi-generator",
            "$generatedSourcesDir/.openapi-generator-ignore",
            "$generatedSourcesDir/README.md"
        )
    }
}

tasks.build {
    dependsOn("openApiGenerate")
}

sourceSets {
    getByName("main") {
        java {
            srcDir("$generatedSourcesDir/src/main/java")
        }
    }
}
