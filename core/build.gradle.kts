plugins {
    kotlin("plugin.jpa")
    kotlin("plugin.spring")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")

    compileOnly("jakarta.persistence:jakarta.persistence-api:3.2.0")
}