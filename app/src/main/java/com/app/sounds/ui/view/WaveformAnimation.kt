package com.app.sounds.ui.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun WaveformAnimation(amplitude: Float) {
    val animatedAmplitude = animateFloatAsState(
        targetValue = amplitude,
        animationSpec = tween(durationMillis = 500)
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ){
        val width = size.width
        val height = size.height
        val centerY = height/2

        val path = Path().apply{
            moveTo(0f,centerY)
            lineTo(width,centerY)
            for(i in 0 until 100){
                val x = i *(width /100)
                val y = centerY + sin(i * 0.2f) * animatedAmplitude.value * 100
                lineTo(x, y)
            }

        }
        drawPath(path, Color(191, 214, 254), style = Stroke(width= 4.dp.toPx()))
    }



}

@Preview(showBackground = true)
@Composable
fun PreviewWaveformAnimation() {
    var amplitude by remember { mutableFloatStateOf(0.5f) }

    LaunchedEffect(Unit) {
        while (true) {
            amplitude = (0.2f + Math.random().toFloat() * (5.0f - 0.2f))

            delay(200.milliseconds)
        }
    }

    WaveformAnimation(amplitude)
}

