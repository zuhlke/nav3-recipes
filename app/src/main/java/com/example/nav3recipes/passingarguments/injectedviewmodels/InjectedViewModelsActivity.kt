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

package com.example.nav3recipes.passingarguments.injectedviewmodels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.example.nav3recipes.content.ContentBlue
import com.example.nav3recipes.content.ContentGreen
import com.example.nav3recipes.ui.enableEdgeToEdgeCompat
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel

/**
 * Passing navigation arguments to a Hilt injected ViewModel
 *
 * - ViewModelStoreNavEntryDecorator ensures that ViewModels are scoped to the NavEntry
 * - Assisted injection is used to construct ViewModels with the navigation key
 */

data object RouteA
data class RouteB(val id: String)

@AndroidEntryPoint
class InjectedViewModelsActivity : ComponentActivity() {

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
                                backStack.add(RouteB("123"))
                            }) {
                                Text("Click to navigate")
                            }
                        }
                    }
                    entry<RouteB> { key ->
                        val viewModel = hiltViewModel<RouteBViewModel, RouteBViewModel.Factory>(
                            creationCallback = { factory ->
                                factory.create(key)
                            }
                        )
                        ScreenB(viewModel = viewModel)
                    }
                }
            )
        }
    }
}

@Composable
fun ScreenB(viewModel: RouteBViewModel) {
    ContentBlue("Route id: ${viewModel.navKey.id} ")
}

@HiltViewModel(assistedFactory = RouteBViewModel.Factory::class)
class RouteBViewModel @AssistedInject constructor(
    @Assisted val navKey: RouteB
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(navKey: RouteB): RouteBViewModel
    }
}
