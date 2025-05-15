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

class TwoPaneScene<T : Any>(
    override val key: Any,
    override val entries: List<NavEntry<T>>,
    override val previousEntries: List<NavEntry<T>>,
    val left: NavEntry<T>,
    val right: NavEntry<T>
) : Scene<T> {
    override val content: @Composable (() -> Unit) = {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.5f)) {
                left.content.invoke(left.key)
            }
            Column(modifier = Modifier.weight(0.5f)) {
                right.content.invoke(right.key)
            }
        }
    }

    companion object {
        internal const val TWO_PANE_KEY = "TwoPane"
        fun twoPane() = mapOf(TWO_PANE_KEY to true)
    }
}

class TwoPaneSceneStrategy<T : Any> : SceneStrategy<T> {
    @Composable
    override fun calculateScene(
        entries: List<NavEntry<T>>,
        onBack: (Int) -> Unit
    ): Scene<T>? {
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

        // Only return a Scene if the window is sufficiently wide to render two panes
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        val lastTwoEntries = entries.takeLast(2)

        // Only return a Scene if there are two entries, and that both have declared that they can
        // be displayed in a two pane scene.
        return if (lastTwoEntries.size == 2
            && lastTwoEntries.all { it.metadata.containsKey(TWO_PANE_KEY) }
        ) {

            val leftEntry = lastTwoEntries.first()
            val rightEntry = lastTwoEntries.last()

            TwoPaneScene(
                key = leftEntry,
                entries = lastTwoEntries,
                previousEntries = entries.dropLast(2),
                left = leftEntry,
                right = rightEntry
            )
        } else {
            null
        }
    }
}

