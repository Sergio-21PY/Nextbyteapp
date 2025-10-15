package com.example.nextbyte_app.ui.screens.home.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import com.example.nextbyte_app.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Text


@Composable
fun HomeCarousel() {
    val images = listOf(R.drawable.promo1, R.drawable.promo2, R.drawable.promo3) // Simula tus recursos

    // Un simple Row deslizante para simular el carrusel
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(images) { imageResId ->

            Text(
                text = "Ofertas!!"
            )
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .height(180.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Oferta Promocional!",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

            }
        }
    }
}