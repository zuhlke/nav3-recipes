package com.example.nav3recipes.scenes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.Scene
import androidx.navigation3.ui.SceneStrategy
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.example.nav3recipes.scenes.TwoPaneScene.Companion.TWO_PANE_KEY

class TwoPaneScene<T: Any>(
    override val key: Any,
    override val entries: List<NavEntry<T>>,
    override val previousEntries: List<NavEntry<T>>,
    val left: NavEntry<T>,
    val right: NavEntry<T>
) : Scene<T> {
    override val content: @Composable (() -> Unit) = {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.5f)) {
                // Render the list content
                left.content.invoke(left.key)
            }
            Column(modifier = Modifier.weight(0.5f)) {
                // Render the detail content, or retrieve and display the placeholder
                right.content.invoke(right.key)
            }
        }
    }

    public companion object {
        internal const val TWO_PANE_KEY = "TwoPane"

        fun twoPane()  = mapOf(TWO_PANE_KEY to true)
    }
}

class TwoPaneSceneStrategy<T: Any> : SceneStrategy<T> {
    @Composable
    override fun calculateScene(
        entries: List<NavEntry<T>>,
        onBack: (Int) -> Unit
    ): Scene<T>? {
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

        // Only return a scene if the screen width is 600dp or greater
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        // Only return a scene if there's a list in the back stack
        val lastTwoEntries = entries.takeLast(2)

        if (lastTwoEntries.all {
            it.metadata.containsKey(TWO_PANE_KEY)
        }) {
            return TwoPaneScene(
                key = Pair(lastTwoEntries.first(), lastTwoEntries.last()),
                entries = lastTwoEntries,
                previousEntries = entries.dropLast(2),
                left = lastTwoEntries.first(),
                right = lastTwoEntries.last()
            )
        }

        return null
    }

}

