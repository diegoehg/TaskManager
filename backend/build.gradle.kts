plugins {
	java
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.encora"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web:3.3.3")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.3.3")
	implementation("org.springframework.boot:spring-boot-starter-validation:3.3.3")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.3")
	testImplementation(platform("org.junit:junit-bom:5.10.0"))
	testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
