package com.example.dogizzy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dogizzy.R

val Gilroy = FontFamily(
    Font(R.font.gilroy_light, FontWeight.Light),
    Font(R.font.gilroy_bold, FontWeight.ExtraBold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    displaySmall = TextStyle(
        fontFamily = Gilroy,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Gilroy,
        fontWeight = FontWeight.Light,
        fontSize = 19.sp
    ),
    displayMedium = TextStyle(
        fontFamily = Gilroy,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp
    ),
    displayLarge = TextStyle(
        fontFamily = Gilroy,
        fontWeight = FontWeight.Light,
        fontSize = 40.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Gilroy,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Gilroy,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 45.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Gilroy,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 19.sp
    ),


    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)
