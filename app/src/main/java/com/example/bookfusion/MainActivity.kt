package com.example.bookfusion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookfusion.navigation.AppNavigation
import com.example.bookfusion.ui.theme.BookFusionTheme
import com.example.bookfusion.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookFusionTheme {
                val viewModel: AuthViewModel = viewModel()
                AppNavigation(viewModel)
            }
        }
    }
}
