package org.stash.project

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Stash",
        ) {
            MaterialTheme {
                App()
            }
        }
    }
