package com.example.dogizzy.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay


@Composable
fun Splash(navController: NavHostController) {
    val dots = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.onPrimary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        dots.forEachIndexed { index, animatable ->
            LaunchedEffect(animatable) {
                delay(index * 100L)
                animatable.animateTo(
                    targetValue = 1f, animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 2000
                            0.0f at 0 with LinearOutSlowInEasing
                            1.0f at 200 with LinearOutSlowInEasing
                            0.0f at 400 with LinearOutSlowInEasing
                            0.0f at 2000
                        }
                    )
                )
            }
        }

        LaunchedEffect(key1 = true){
            delay(1000L)
            navController.navigate("login")
        }
        val dys = dots.map { it.value }
        val travelDistance = with(LocalDensity.current) { 15.dp.toPx() }
        var i = 0

        Row() {
            dys.forEachIndexed { index, dy ->
                i++
                if(i == 1){
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                translationY = -dy * travelDistance
                            }
                    ) {
                        Text(
                            "DO", color = Color.White,
                            style = MaterialTheme.typography.titleLarge)
                    }
                }
                if(i == 2){
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                translationY = -dy * travelDistance
                            }
                    ) {
                        Text(
                            "GI", color = Color.White,
                            style = MaterialTheme.typography.titleLarge)
                    }
                }
                if(i == 3){
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                translationY = -dy * travelDistance
                            }
                    ) {
                        Text(
                            "ZZY", color = Color.White,
                            style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }

    }



}