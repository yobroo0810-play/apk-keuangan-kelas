package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.KasKelasApp
import com.example.ui.KasKelasViewModel
import com.example.ui.theme.KasKelasTheme

class MainActivity : ComponentActivity() {

    private val viewModel: KasKelasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KasKelasTheme {
                KasKelasApp(viewModel = viewModel)
            }
        }
    }
}
