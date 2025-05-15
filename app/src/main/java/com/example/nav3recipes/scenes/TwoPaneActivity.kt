/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.nav3recipes.scenes

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
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberMutableStateListOf
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.nav3recipes.utils.serializable.rememberSaveableMutableStateListOf
import kotlinx.serialization.Serializable

@Serializable
object Home : NavKey()

@Serializable
data class Product(val id: String) : NavKey()

@Serializable
object SomethingElse : NavKey()

class TwoPaneActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Scaffold { paddingValues ->

                val backStack = rememberNavBackStack(Home)

                val twoPaneStrategy = remember { TwoPaneSceneStrategy<Any>() }

                NavDisplay(
                    backStack = backStack,
                    modifier = Modifier.padding(paddingValues),
                    onBack = { backStack.removeLastOrNull() },
                    sceneStrategy = twoPaneStrategy,
                    entryProvider = entryProvider {
                        entry<Home>(TwoPaneScene.twoPane()) {
                            Column {
                                Text("Welcome to Nav3")
                                Button(onClick = {
                                    backStack.add(Product("123"))
                                }) {
                                    Text("Click to navigate")
                                }
                            }
                        }
                        entry<Product>(TwoPaneScene.twoPane()) {
                            Column {
                                Text("Product ${it.id} ")
                                Button(onClick = {
                                    backStack.add(SomethingElse)
                                }) {
                                    Text("Click to add something else")
                                }
                            }

                        }
                        entry<SomethingElse> {
                            Text("This is a new screen")
                        }
                    }
                )
            }
        }
    }
}
