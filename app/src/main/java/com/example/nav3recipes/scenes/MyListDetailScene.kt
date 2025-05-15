//package com.example.nav3recipes.scenes
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.navigation3.runtime.NavEntry
//import androidx.navigation3.ui.Scene
//import androidx.navigation3.ui.SceneStrategy
//import androidx.compose.ui.platform.LocalWindowInfo
//import androidx.compose.ui.unit.dp
//import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
//import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
//import androidx.window.core.layout.WindowSizeClass
//import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
//
//// --- Metadata for List Pane ---
///**
// * Metadata for a list pane, including a composable placeholder to display
// * when no detail item is selected.
// */
//data class ListPaneMetadata(
//    val placeholder: @Composable () -> Unit
//)
//
//// --- MyListDetailScene ---
///**
// * A custom [Scene] to display two [NavEntry]s side-by-side,
// * representing a list and an optional detail view.
// */
//internal data class MyListDetailScene<T : Any>(
//    override val key: Any, // A key to identify this scene instance
//    override val entries: List<NavEntry<T>>,
//    override val previousEntries: List<NavEntry<T>>,
//    private val listEntry: NavEntry<T>,
//    private val detailEntry: NavEntry<T>?, // detailEntry remains nullable
//) : Scene<T> {
//
//    override val content: @Composable (() -> Unit) = {
//        Row(modifier = Modifier.fillMaxSize()) {
//            Column(modifier = Modifier.weight(0.4f)) {
//                // Render the list content
//                listEntry.content.invoke(listEntry.key)
//            }
//            Column(modifier = Modifier.weight(0.6f)) {
//                // Render the detail content, or retrieve and display the placeholder
//                if (detailEntry != null) {
//                    detailEntry.content.invoke(detailEntry.key)
//                } else {
//                    // Access the placeholder directly from listEntry's metadata
//                    val listMetadata = listEntry.metadata[MyListDetailSceneStrategy.LIST_PANE_KEY] as? ListPaneMetadata
//                    listMetadata?.placeholder?.invoke() // Invoke the placeholder if found
//                }
//            }
//        }
//    }
//}
//
//// --- MyListDetailSceneStrategy ---
///**
// * A [SceneStrategy] that creates a [MyListDetailScene] based on the type of the top
// * one or two entries in the back stack.
// */
//public class MyListDetailSceneStrategy<T : Any> : SceneStrategy<T> {
//    @Composable
//    override fun calculateScene(
//        entries: List<NavEntry<T>>,
//        onBack: (count: Int) -> Unit,
//    ): Scene<T>? {
//        if (entries.isEmpty()) {
//            return null
//        }
//
//        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
//
//        // Only return a scene if the screen width is 600dp or greater
//        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
//            return null
//        }
//
//        // Only return a scene if there's a list in the back stack
//        val listEntry = entries.findLast { it.metadata.containsKey(LIST_PANE_KEY) }
//        if (listEntry == null) return null
//
//        val detailEntry = entries.findLast { it.metadata.containsKey(DETAIL_PANE_KEY) }
//
//        return MyListDetailScene(
//            key = listEntry.key,
//            entries = listOfNotNull(listEntry, detailEntry),
//            previousEntries = entries.drop
//        )
//
//        val topEntry = entries.last()
//        val secondToTopEntry = entries.getOrNull(entries.lastIndex - 1)
//
//        // Check if the top entry is a DetailEntry
//        if (topEntry.metadata.containsKey(DETAIL_PANE_KEY)) {
//            // Scenario: Detail is top of the stack, with List as the second entry
//            // Back stack: [..., ListEntry (secondToTopEntry), DetailEntry (topEntry)]
//            if (secondToTopEntry != null) {
//                val listMetadata = secondToTopEntry.metadata[LIST_PANE_KEY] as? ListPaneMetadata
//                if (listMetadata != null) {
//                    // We found a valid list entry at second-to-top and a detail entry at top
//                    return MyListDetailScene(
//                        key = secondToTopEntry.key, // Scene's key is the list entry's key
//                        entries = listOf(secondToTopEntry, topEntry),
//                        previousEntries = entries.dropLast(2), // Back stack before these two entries
//                        listEntry = secondToTopEntry,
//                        detailEntry = topEntry
//                    )
//                }
//            }
//        }
//
//        // Check if the top entry is a ListEntry (and the previous check didn't apply)
//        // Scenario: List is top of the stack (no detail has been selected)
//        // Back stack: [..., ListEntry (topEntry)]
//        val listMetadataOnTop = topEntry.metadata[LIST_PANE_KEY] as? ListPaneMetadata
//        if (listMetadataOnTop != null) {
//            return MyListDetailScene(
//                key = topEntry.key, // Scene's key is the list entry's key
//                entries = listOf(topEntry),
//                previousEntries = entries.dropLast(1), // Back stack before the top entry
//                listEntry = topEntry,
//                detailEntry = null // No detail entry selected yet
//            )
//        }
//
//        // If neither scenario matches, this strategy cannot form a Scene
//        return null
//    }
//
//    public companion object {
//        internal const val LIST_PANE_KEY = "ListPane"
//        internal const val DETAIL_PANE_KEY = "DetailPane"
//
//        /**
//         * Helper to mark a NavEntry as the list pane content, providing a composable
//         * placeholder for the detail pane when no detail is selected.
//         * @param placeholder The composable to display in the detail pane when empty.
//         */
//        public fun listPane(
//            placeholder: @Composable () -> Unit
//        ): Map<String, Any> = mapOf(LIST_PANE_KEY to ListPaneMetadata(placeholder))
//
//        /**
//         * Helper to mark a NavEntry as the detail pane content.
//         */
//        public fun detailPane(): Map<String, Any> = mapOf(DETAIL_PANE_KEY to true)
//    }
//}