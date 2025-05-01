package com.example.nav3recipes.basic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation3.NavEntry
import androidx.navigation3.SceneNavDisplay
import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data class Product(val id: String)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Scaffold { paddingValues ->

                val backStack = rememberSaveable { mutableStateListOf<Any>(Home) }

                SceneNavDisplay(
                    backStack = backStack,
                    modifier = Modifier.padding(paddingValues),
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = { key ->
                        when (key) {
                            is Home -> NavEntry(key) {
                                Column {
                                    Text("Welcome to Nav3")
                                    Button(onClick = {
                                        backStack.add(Product("123"))
                                    }) {
                                        Text("Click to navigate")
                                    }
                                }
                            }
                            is Product -> NavEntry(key) {
                                Text("Product ${key.id} ")
                            }
                            else -> NavEntry(Unit) { Text("Unknown route") }
                        }
                    }
                )
            }
        }
    }
}
