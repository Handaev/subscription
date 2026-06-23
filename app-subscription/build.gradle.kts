plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.springdoc.openapi-gradle-plugin")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.liquibase:liquibase-core")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")

    implementation("org.mapstruct:mapstruct:1.6.2")

    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.kafka:spring-kafka:3.2.4")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:testcontainers:1.21.4")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:kafka:1.21.4")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
}

openApi {
    apiDocsUrl.set("http://localhost:8086/subscription/v3/api-docs.yaml")
    outputFileName.set("openapi-subscription.yaml")
    outputDir.set(file("${project(":openapi-subscription").projectDir}/src/main/resources/static/openapi"))
}

tasks.bootRun {
    jvmArgs(
        "-Dserver.port=8086",
        "-Dserver.servlet.context-path=/subscription",
        "-Dspring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "-Dspring.datasource.driver-class-name=org.h2.Driver",
        "-Dspring.datasource.username=sa",
        "-Dspring.datasource.password=",
        "-Dspring.liquibase.enabled=false",
        "-Dspring.jpa.hibernate.ddl-auto=update"
    )
}

tasks.withType<Test> {
    useJUnitPlatform()
}