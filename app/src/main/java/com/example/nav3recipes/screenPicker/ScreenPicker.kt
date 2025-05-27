package com.example.nav3recipes.screenPicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nav3recipes.animations.AnimatedActivity
import com.example.nav3recipes.basic.BasicActivity
import com.example.nav3recipes.basicdsl.BasicDslActivity
import com.example.nav3recipes.basicsaveable.BasicSaveableActivity
import com.example.nav3recipes.commonui.CommonUiActivity
import com.example.nav3recipes.conditional.ConditionalActivity
import com.example.nav3recipes.scenes.materiallistdetail.MaterialListDetailActivity
import com.example.nav3recipes.scenes.twopane.TwoPaneActivity

/**
 * Screen to pick which navigation recipe to try out
 */


private data class ActivityButton(
    val text: String,
    val activityClass: Class<out Activity>
)

private val buttons = listOf(
    ActivityButton("Basic", BasicActivity::class.java),
    ActivityButton("Basic Dsl", BasicDslActivity::class.java),
    ActivityButton("Basic Saveable", BasicSaveableActivity::class.java),
    ActivityButton("Animated", AnimatedActivity::class.java),
    ActivityButton("Conditional", ConditionalActivity::class.java),
    ActivityButton("Common Ui", CommonUiActivity::class.java),
    ActivityButton("Material List Detail", MaterialListDetailActivity::class.java),
    ActivityButton("Two Pane", TwoPaneActivity::class.java)
)

class ScreenPicker : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold { contentPadding ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize()
                ) {
                    item {
                        Text(
                            text = "Choose a navigation recipe",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                    items(buttons) { (name, activityClass) ->
                        Button(onClick = { navigateTo(activityClass) }) {
                            Text(name)
                        }
                    }
                }
            }
        }
    }

    private fun navigateTo(cls: Class<out Activity>) {
        val intent = Intent(this@ScreenPicker, cls)
        startActivity(intent)
    }
}
