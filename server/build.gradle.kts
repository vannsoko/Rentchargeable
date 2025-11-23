plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
    application
}

group = "com.example.myapplication"
version = "1.0.0"
application {
    mainClass.set("com.example.myapplication.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}
val ktor_version = "3.3.2"
val exposed_version = "1.0.0-rc-3" // Vérifiez la dernière version
val h2_version = "2.4.240"
dependencies {
    // Core Server
    //implementation("io.ktor:ktor-server-core:${ktor_version}")
    //implementation("io.ktor:ktor-server-netty:${ktor_version}")
    // Plugins nécessaires
    implementation("io.ktor:ktor-server-auth:${ktor_version}")
    implementation("io.ktor:ktor-server-auth-jwt:${ktor_version}")
    implementation("io.ktor:ktor-server-content-negotiation:${ktor_version}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor_version}")
    //implementation("ch.qos.logback:logback-classic:1.4.14")

    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)


/*
dependencies {
    implementation("io.ktor:ktor-server-auth:3.3.1")
    implementation("io.ktor:ktor-server-auth-jwt:3.3.1")
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}*/

    implementation("org.jetbrains.exposed:exposed-core:0.46.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.46.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.46.0")
    // Your chosen database driver, e.g., for H2:
    implementation("com.h2database:h2:2.2.224")

    //testing
    testImplementation("io.ktor:ktor-server-test-host:2.3.11")
    testImplementation("io.ktor:ktor-client-content-negotiation:2.3.11")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.22")
}
