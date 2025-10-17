package com.example.nextbyte_app.ui.screens.home.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.nextbyte_app.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

//DATOS para las ofertas
data class Offer(
    val imageResId: Int,
    val description: String,
    val offerText: String,
    val productId: Int
)

@Composable
fun HomeCarousel(navController: NavController) {
    //LISTA ACTUALIZADA DE OFERTAS
    val offers = listOf(
        Offer(R.drawable.promo1, "Oferta Laptops", "¡20% OFF!", productId = 101),
        Offer(R.drawable.promo2, "Oferta Monitores", "Ahorra $15.000", productId = 205),
        Offer(R.drawable.promo3, "Oferta Cámaras", "¡Envío Gratis!", productId = 312)
    )

    // Un simple Row deslizante para simular el carrusel
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp) // Aumenté el espaciado
    ) {

        items(offers) { offer ->

            Card(
                onClick = { // <-- 3. Usamos el onClick del Card
                    // Definimos la ruta de destino y le adjuntamos el ID como argumento
                    navController.navigate("product_detail_route/${offer.productId}")
                },
                modifier = Modifier
                    .width(300.dp)
                    .height(180.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                //Usamos Box para superponer el texto sobre la imagen
                Box(modifier = Modifier.fillMaxSize()) {

                    //IMAGEN DE FONDO
                    Image(
                        painter = painterResource(id = offer.imageResId),
                        contentDescription = offer.description,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )



                    //Cartel de oferta en la esquina superior derecha.
                    Text(
                        text = offer.offerText,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .align(Alignment.TopEnd) // Alineacion en la esquina superior derecha
                            .background(
                                color = MaterialTheme.colorScheme.error, // Color de error para que destaque (rojo)
                                shape = RoundedCornerShape(bottomStart = 12.dp) // Bordes abajo a la derecha.
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )


                }
            }
        }
    }
}