package com.example.ecosync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.ecosync.ui.EcoSyncApp
import com.example.ecosync.ui.theme.EcoSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EcoSyncTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    EcoSyncApp()
                }
            }
        }
    }
}
