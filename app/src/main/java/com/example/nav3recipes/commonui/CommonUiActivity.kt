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

package com.example.nav3recipes.commonui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.nav3recipes.content.ContentBlue
import com.example.nav3recipes.content.ContentGreen
import com.example.nav3recipes.content.ContentPurple
import com.example.nav3recipes.content.ContentRed
import com.example.nav3recipes.ui.setEdgeToEdgeConfig
import kotlinx.serialization.Serializable

/**
 * Common navigation UI example. This app has three top level routes: Home, ChatList and Camera.
 * ChatList has a sub-route: ChatDetail.
 *
 * The app back stack state is modeled in `TopLevelBackStack`. This creates a back stack for each
 * top level route. It flattens those maps to create a single back stack for `NavDisplay`. This allows
 * `NavDisplay` to know where to go back to.
 *
 * Note that in this example, the Home route can move above the ChatList and Camera routes, meaning
 * navigating back from Home doesn't necessarily leave the app. The app will exit when the user goes
 * back from a single remaining top level route in the back stack.
 */

private sealed interface TopLevelRoute : NavKey {
    val icon: ImageVector
}

@Serializable
private data object Home : TopLevelRoute {
    override val icon = Icons.Default.Home
}

@Serializable
private data object ChatList : TopLevelRoute {
    override val icon = Icons.Default.Face
}

@Serializable
private data object ChatDetail : NavKey

@Serializable
private data object Camera : TopLevelRoute {
    override val icon = Icons.Default.PlayArrow
}

private val TOP_LEVEL_ROUTES: List<TopLevelRoute> = listOf(Home, ChatList, Camera)

class CommonUiActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setEdgeToEdgeConfig()
        super.onCreate(savedInstanceState)
        setContent {
            val topLevelBackStack = rememberSaveable(saver = TopLevelBackStack.Saver) {
                TopLevelBackStack(startKey = Home)
            }

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        TOP_LEVEL_ROUTES.forEach { topLevelRoute ->

                            val isSelected = topLevelRoute == topLevelBackStack.topLevelKey
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    topLevelBackStack.addTopLevel(topLevelRoute)
                                },
                                icon = {
                                    Icon(
                                        imageVector = topLevelRoute.icon,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            ) { _ ->
                NavDisplay(
                    backStack = topLevelBackStack.backStack,
                    onBack = { topLevelBackStack.removeLast() },
                    entryProvider = entryProvider {
                        entry<Home> {
                            ContentRed("Home screen")
                        }
                        entry<ChatList> {
                            ContentGreen("Chat list screen") {
                                Button(onClick = { topLevelBackStack.add(ChatDetail) }) {
                                    Text("Go to conversation")
                                }
                            }
                        }
                        entry<ChatDetail> {
                            ContentBlue("Chat detail screen")
                        }
                        entry<Camera> {
                            ContentPurple("Camera screen")
                        }
                    },
                )
            }
        }
    }
}

class TopLevelBackStack<T : Any>() {

    constructor(startKey: T) : this() {
        topLevelStacks.put(startKey, mutableStateListOf(startKey))
        updateBackStack()
        topLevelKey = startKey
    }

    private constructor(topLevelStacks: LinkedHashMap<T, List<T>>) : this() {
        this.topLevelStacks.putAll(topLevelStacks.mapValues { it.value.toMutableStateList() })
        updateBackStack()
        topLevelKey = topLevelStacks.entries.last().key
    }

    // Maintain a stack for each top level route
    private val topLevelStacks = linkedMapOf<T, SnapshotStateList<T>>()

    // Expose the current top level route for consumers
    var topLevelKey by mutableStateOf<T?>(null)
        private set

    // Expose the back stack so it can be rendered by the NavDisplay
    val backStack = mutableStateListOf<T>()

    private fun updateBackStack() =
        backStack.apply {
            clear()
            addAll(topLevelStacks.flatMap { it.value })
        }

    fun addTopLevel(key: T) {
        // If the top level doesn't exist, add it
        if (topLevelStacks[key] == null) {
            topLevelStacks.put(key, mutableStateListOf(key))
        } else {
            // Otherwise just move it to the end of the stacks
            topLevelStacks.apply {
                remove(key)?.let {
                    put(key, it)
                }
            }
        }
        topLevelKey = key
        updateBackStack()
    }

    fun add(key: T) {
        topLevelStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    fun removeLast() {
        val removedKey = topLevelStacks[topLevelKey]?.removeLastOrNull()
        // If the removed key was a top level key, remove the associated top level stack
        topLevelStacks.remove(removedKey)
        topLevelKey = topLevelStacks.keys.last()
        updateBackStack()
    }

    object Saver : androidx.compose.runtime.saveable.Saver<TopLevelBackStack<NavKey>, List<Any>> {
        override fun SaverScope.save(value: TopLevelBackStack<NavKey>): List<Any>? {
            return buildList {
                value.topLevelStacks.forEach { topLevelRoute, childRoutes ->
                    add(topLevelRoute)
                    add(ArrayList(childRoutes))
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun restore(value: List<Any>): TopLevelBackStack<NavKey>? {
            val list = value
            val topLevelStacks = linkedMapOf<NavKey, List<NavKey>>()
            check(list.size.rem(2) == 0) { "non-zero remainder" }
            var index = 0
            while (index < list.size) {
                val key = list[index] as NavKey
                val value = list[index + 1] as ArrayList<NavKey>
                topLevelStacks[key] = value
                index += 2
            }
            return TopLevelBackStack(topLevelStacks)
        }
    }
}

