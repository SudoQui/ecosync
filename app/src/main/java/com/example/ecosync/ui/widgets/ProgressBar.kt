package com.example.ecosync.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(label: String, value: Float, max: Float) {
    val pct = ((value / max).coerceIn(0f, 1f) * 100).toInt()
    Box(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text("$label $pct%")
        Box(
            Modifier
                .fillMaxWidth()
                .height(10.dp)
                .padding(top = 18.dp)
                .background(Color(0xFFE5E7EB), RoundedCornerShape(999.dp))
        )
        Box(
            Modifier
                .fillMaxWidth(pct / 100f)
                .height(10.dp)
                .padding(top = 18.dp)
                .background(Color(0xFF2563EB), RoundedCornerShape(999.dp))
        )
    }
}
