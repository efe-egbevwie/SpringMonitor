import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.5.10"
    id("app.cash.sqldelight") version "2.0.0"
}

group = "com.efe"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val sqlDelightVersion = "2.0.0"


        val commonMain by getting{
            dependencies{
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
                implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.5.21")
                implementation("io.github.oshai:kotlin-logging:5.1.0")

            }
        }

        val jvmMain by getting {
            dependencies {

                val voyagerVersion = "1.0.0-rc09"
                val ktorVersion = "2.3.3"
                val coroutinesVersion = "1.7.1"
                val logbackVersion = "1.4.11"



                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.compose.material3:material3-desktop:1.4.3")

                // Navigator
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
                // TabNavigator
                implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:1.5.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
                implementation("io.ktor:ktor-client-logging-jvm:1.5.0")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                implementation("io.ktor:ktor-client-logging-jvm:2.3.3")
                implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.4.3")

                implementation("com.squareup.sqldelight:runtime:1.5.5")

                implementation("app.cash.sqldelight:sqlite-driver:$sqlDelightVersion")
                implementation("app.cash.sqldelight:coroutines-extensions:$sqlDelightVersion")


            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {

        buildTypes.release {
            proguard {
                configurationFiles.from("compose-desktop.pro")
            }
        }

        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "SpringMonitor"
            packageVersion = "1.0.0"
            modules("java.sql")

            windows{
                dirChooser = true
                shortcut = true
                menu = true
            }
        }
    }
}


sqldelight {
    databases {
        create("applications") {
            packageName.set("com.efe")
        }
    }
}
