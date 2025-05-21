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

package com.example.nav3recipes.passingarguments.basicviewmodels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.example.nav3recipes.content.ContentBlue
import com.example.nav3recipes.content.ContentGreen
import com.example.nav3recipes.ui.enableEdgeToEdgeCompat

/**
 * Passing navigation arguments to a ViewModel.
 *
 * - ViewModelStoreNavEntryDecorator ensures that ViewModels are scoped to the NavEntry
 */
data object RouteA

data class RouteB(val id: String)

class BasicViewModelsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdgeCompat()
        super.onCreate(savedInstanceState)
        setContent {
            val backStack = remember { mutableStateListOf<Any>(RouteA) }

            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSceneSetupNavEntryDecorator(),
                    rememberSavedStateNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<RouteA> {
                        ContentGreen("Welcome to Nav3") {
                            Button(onClick = {
                                backStack.add(
                                    RouteB("123")
                                )
                            }) {
                                Text("Click to navigate")
                            }
                        }
                    }
                    entry<RouteB> { key ->
                        ScreenB(viewModel = viewModel(factory = RouteBViewModel.Factory(key)))
                    }
                }
            )
        }
    }
}

@Composable
fun ScreenB(viewModel: RouteBViewModel = viewModel()) {
    ContentBlue("Route id: ${viewModel.key.id} ")
}

class RouteBViewModel(
    val key: RouteB
) : ViewModel() {
    class Factory(
        private val key: RouteB,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RouteBViewModel(key) as T
        }
    }
}
