import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            // Exposed
            implementation("org.jetbrains.exposed:exposed-core:0.45.0")
            implementation("org.jetbrains.exposed:exposed-dao:0.45.0")
            implementation("org.jetbrains.exposed:exposed-jdbc:0.45.0")
            // SQLite JDBC
            implementation("org.xerial:sqlite-jdbc:3.44.1.0")
            // Ksoup
            implementation("com.fleeksoft.ksoup:ksoup:0.2.4")
            implementation("com.fleeksoft.ksoup:ksoup-network:0.2.4")
            // RichText Editor to render HTML content
            implementation("com.mohamedrejeb.richeditor:richeditor-compose:1.0.0-rc12")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "org.stash.project.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.stash.project"
            packageVersion = "1.0.0"
        }
    }
}
