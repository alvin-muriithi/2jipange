package com.strathmore.groupworkmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.strathmore.groupworkmanager.di.AppContainer
import com.strathmore.groupworkmanager.ui.navigation.AppNavigation
import com.strathmore.groupworkmanager.ui.theme.GroupWorkTheme

/**
 * The entry point of the application. Instantiates the [AppContainer]
 * dependency graph and provides it to Composables via CompositionLocal.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = AppContainer(applicationContext)
        setContent {
            GroupWorkTheme {
                // Provide the AppContainer to all composables below it
                CompositionLocalProvider(LocalAppContainer provides appContainer) {
                    Surface {
                        AppNavigation(appContainer)
                    }
                }
            }
        }
    }
}

// A CompositionLocal to pass the AppContainer down the Compose tree when needed
val LocalAppContainer = compositionLocalOf<AppContainer> { error("AppContainer not provided") }