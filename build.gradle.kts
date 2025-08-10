plugins {
    id("io.micronaut.application") version "4.5.4"
}

version = "0.1"
group = "com.piandphi"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut.micrometer:micronaut-micrometer-core")
    implementation("io.micronaut.micrometer:micronaut-micrometer-registry-prometheus")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-management")
    implementation("io.projectreactor:reactor-core:3.5.6")
    implementation("io.micronaut.cache:micronaut-cache-caffeine")
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:8.0.1")
    implementation("org.jsoup:jsoup:1.17.2")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("org.yaml:snakeyaml:2.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("io.micronaut.test:micronaut-test-junit5") // Added for MicronautTest
}

application {
    mainClass = "com.piandphi.Application"
}

java {
    sourceCompatibility = JavaVersion.toVersion("21")
    targetCompatibility = JavaVersion.toVersion("21")
}
