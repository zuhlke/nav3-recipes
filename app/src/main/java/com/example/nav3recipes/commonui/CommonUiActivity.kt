package com.example.nav3recipes.commonui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.SceneNavDisplay
import androidx.navigation3.entry
import androidx.navigation3.entryProvider
import com.example.nav3recipes.content.TopLevelRoute
import kotlinx.serialization.Serializable

@Serializable data object Home
@Serializable data object ChatList
@Serializable data object ChatDetail
@Serializable data object Camera

val TOP_LEVEL_ROUTES = listOf(
    TopLevelRoute(key = Home, icon = Icons.Default.Home),
    TopLevelRoute(key = ChatList, icon = Icons.Default.Face),
    TopLevelRoute(key = Camera, icon = Icons.Filled.PlayArrow)
)

class CommonUiActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val topLevelBackStack = remember { TopLevelBackStack(Home) }

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        TOP_LEVEL_ROUTES.forEach { topLevelRoute ->
                            NavigationBarItem(
                                selected = topLevelRoute.key == topLevelBackStack.topLevelKey,
                                onClick = {
                                    topLevelBackStack.addTopLevel(topLevelRoute.key)
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
            ) {
                SceneNavDisplay(
                    backStack = topLevelBackStack.backStack,
                    onBack = { topLevelBackStack.removeLast() },
                    modifier = Modifier.padding(it),
                    entryProvider = entryProvider {
                        entry<Home>{
                            Text("Home screen")
                        }
                        entry<ChatList>{
                            Column {
                                Text("Chat list screen")
                                Button(onClick = { topLevelBackStack.add(ChatDetail) }) {
                                    Text("Go to conversation")
                                }
                            }
                        }
                        entry<ChatDetail>{
                            Text("Chat detail screen")
                        }
                        entry<Camera>{
                            Text("Camera screen")
                        }
                    }
                )
            }
        }
    }
}

class TopLevelBackStack(startKey: Any) {

    var topLevelStacks : LinkedHashMap<Any, SnapshotStateList<Any>> = linkedMapOf(
        startKey to mutableStateListOf(startKey)
    )

    var topLevelKey by mutableStateOf(startKey)

    val backStack = mutableStateListOf(startKey)

    private fun updateBackStack(){
        backStack.clear()
        backStack.addAll(topLevelStacks.flatMap { it.value })
    }
    fun addTopLevel(key: Any){

        // If the top level doesn't exist, add it
        if (topLevelStacks[key] == null){
            topLevelStacks.put(key, mutableStateListOf(key))
        } else {
            // Otherwise just move it to the end of the stacks
            topLevelStacks.remove(key)?.let {
                topLevelStacks.put(key, it)
            }
        }
        topLevelKey = key
        updateBackStack()
    }

    fun add(key: Any){
        topLevelStacks[topLevelKey]?.add(key)
        updateBackStack()
    }

    fun removeLast(){
        val removedKey = topLevelStacks[topLevelKey]?.removeLastOrNull()
        // If the removed key was a top level key, remove it
        topLevelStacks.remove(removedKey)
        topLevelKey = topLevelStacks.keys.last()
        updateBackStack()
    }
}

