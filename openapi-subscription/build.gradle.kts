plugins {
    java
    `maven-publish`
    id("org.openapi.generator")
}

val springBootVersion = "3.4.2"
val springCloudVersion = "4.1.0"

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:$springCloudVersion")
    implementation("org.springframework.boot:spring-boot-starter-validation:$springBootVersion")

    implementation("io.github.openfeign:feign-jackson:13.2.1")
    implementation("io.github.openfeign:feign-okhttp:13.2.1")
    implementation("io.github.openfeign.form:feign-form:3.8.0")

    implementation("io.swagger.core.v3:swagger-annotations:2.2.22")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
}

openApiGenerate {
    generatorName.set("spring")
    library.set("spring-cloud")

    inputSpec.set("$projectDir/src/main/resources/static/openapi/openapi-subscription.yaml")
    outputDir.set(layout.buildDirectory.dir("generated/openapi").get().asFile.absolutePath)

    apiPackage.set("org.example.subscription.client.api")
    modelPackage.set("org.example.subscription.client.dto")

    configOptions.set(mapOf(
        "useSpringBoot3" to "true",
        "useJakartaEe" to "true",
        "openApiNullable" to "false",
        "interfaceOnly" to "true"
    ))
}


sourceSets {
    main {
        java {
            srcDir(tasks.openApiGenerate.map { it.outputDir.get() + "/src/main/java" })
        }
    }
}

tasks.openApiGenerate {
    dependsOn(":app-subscription:generateOpenApiDocs")
}

tasks.compileJava {
    dependsOn(tasks.openApiGenerate)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "org.example"
            artifactId = "openapi-subscription"
            version = "1.0.0"
        }
    }
    repositories {
        maven {
            name = "LocalNexus"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}

tasks.processResources {
    mustRunAfter(":app-subscription:generateOpenApiDocs")
}