package com.example.nextbyte_app.ui.screens // O el nombre de tu paquete


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextbyte_app.data.allProducts
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun ProductDetailScreen(productId: Int) {
    // 1. Buscamos el producto en nuestra lista usando el ID que recibimos
    val product = allProducts.find { it.id == productId }
    val formatSymbols = DecimalFormatSymbols(Locale("es", "CL"))
    formatSymbols.currencySymbol = "$"
    val priceFormatter = DecimalFormat("#,##0", formatSymbols)

    // Usamos una Columna para mostrar los detalles
    if (product != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding() // agrega relleno a la parte superior para no tapar los iconos del telefono.
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp) //
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Espacio inicial arriba

            // IMAGEN DEL PRODUCTO
            Image(
                painter = painterResource(id = product.imageResId),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // NOMBRE DEL PRODUCTO
            Text(
                text = product.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // PRECIO
            Text(

                text = "$" + priceFormatter.format(product.price),
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // DESCRIPCIÓN
            Text(
                text = product.description,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp)) // Espacio final abajo
        }
    } else {
        // Pantalla por si el producto no se encuentra
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(), //
            contentAlignment = Alignment.Center
        ) {
            Text("Producto no encontrado!!.")
        }
    }
}