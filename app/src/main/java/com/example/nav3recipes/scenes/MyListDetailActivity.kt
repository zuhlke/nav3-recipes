//package com.example.nav3recipes.scenes
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.navigation3.runtime.NavEntry
//import androidx.navigation3.runtime.entryProvider
//import androidx.navigation3.ui.NavDisplay
//import androidx.navigation3.ui.SinglePaneSceneStrategy
//import com.example.nav3recipes.utils.serializable.rememberSaveableMutableStateListOf
//import kotlinx.serialization.Serializable
//
//@Serializable data object ProductListRoute
//@Serializable data class ProductDetailRoute(val id: String)
//
//class MyListDetailActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//
//            val backStack = remember { mutableStateListOf<Any>(ProductListRoute) }
//            val listDetailStrategy = MyListDetailSceneStrategy<Any>()
//
//            NavDisplay(
//                backStack = backStack,
//                onBack = { backStack.removeLastOrNull() },
//                entryProvider = { key ->
//                    when (key) {
//                        is ProductListRoute -> NavEntry(
//                            key = key,
//                            metadata = MyListDetailSceneStrategy.listPane { // The placeholder is still defined here
//                                Box(
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    Text("Select a product to view details")
//                                }
//                            }
//                        ) {
//                            Column {
//                                Text("Product List")
//                                Button(onClick = { backStack.add(ProductDetailRoute("ABC")) }) {
//                                    Text("View Details for ABC")
//                                }
//                                Button(onClick = { backStack.add(ProductDetailRoute("XYZ")) }) {
//                                    Text("View Details for XYZ")
//                                }
//                            }
//                        }
//                        is ProductDetailRoute -> NavEntry(
//                            key = key,
//                            metadata = MyListDetailSceneStrategy.detailPane()
//                        ) {
//                            Text("Product Detail: ${key.id}")
//                        }
//                        else -> NavEntry(key) { Text("Unknown route: $key") }
//                    }
//                },
//                sceneStrategy = listDetailStrategy
//            )
//        }
//    }
//}
