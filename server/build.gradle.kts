plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "com.example.myapplication"
version = "1.0.0"
application {
    mainClass.set("com.example.myapplication.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)

    implementation("io.ktor:ktor-server-content-negotiation:2.3.11")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")
    implementation("io.ktor:ktor-server-netty:2.3.11")
    implementation("io.ktor:ktor-server-auth:2.3.11")
    implementation("io.ktor:ktor-server-sessions:2.3.11")
    implementation("org.jetbrains.exposed:exposed-core:0.50.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.0")
    implementation("com.h2database:h2:2.2.224")
    implementation("at.favre.lib:bcrypt:0.10.2")
    
    //testing
    testImplementation("io.ktor:ktor-server-test-host:2.3.11")
    testImplementation("io.ktor:ktor-client-content-negotiation:2.3.11")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.22")
}