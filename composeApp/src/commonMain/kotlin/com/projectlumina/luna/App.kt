package com.projectlumina.luna

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.projectlumina.luna.component.AuthBrowserLogin
import com.projectlumina.luna.service.Service

import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import luna.composeapp.generated.resources.Res
import luna.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { Service.toggle()}) {
                Text("start relay")
            }

        }



    }
}

@Composable
fun AuthScreen() {
    var result by remember { mutableStateOf<String>("Waiting...") }

    AuthBrowserLogin { error ->
        result = error?.localizedMessage ?: "Success!"
    }

    Text(result)
}