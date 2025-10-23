package com.example.nextbyte_app.ui.screens.Productos


import com.example.nextbyte_app.data.Product
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.nextbyte_app.R
import com.example.nextbyte_app.ui.screens.carrito.CartViewModel

/**
 * Data class para Producto - Definida localmente para evitar dependencias
 * (NOTA: Esta definición ya no es necesaria aquí,
 * ahora usamos la que importamos de 'com.example.nextbyte_app.data.Product')
 */

/**
 * Lista local de productos para testing
 * (Esta lista la mantenemos aquí para mostrar los productos en esta pantalla)
 */
val localProducts = listOf(

    Product(
        id = 101,
        name = "Ajazz AK820",
        description = "Teclado mecánico silencioso con 5 capas de aislamiento acústico",
        price = 39999,
        imageResId = R.drawable.promo1 // Placeholder
    ),
    Product(
        id = 205,
        name = "HyperX Cloud III - Auriculares Gaming",
        description = "Auriculares gaming con espuma viscoelástica y transductores de 53mm",
        price = 79999,
        imageResId = R.drawable.promo2// Placeholder
    ),
    Product(
        id = 312,
        name = "Monitor Gamer Xiaomi G34WQi",
        description = "Monitor curvo de 34 pulgadas con resolución WQHD y 180Hz",
        price = 220999,
        imageResId = R.drawable.promo3// Placeholder
    )
)

/**
 * Screen principal de productos que muestra SOLO el contenido
 * La barra morada inferior se maneja desde HomeScreen
 */
@Composable
fun ProductosScreen(
    navController: NavController,

    // Forzamos que el ViewModel "pertenezca" a la Actividad principal
    cartViewModel: CartViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )


) {

    val context = LocalContext.current

    // LazyColumn para renderizado eficiente (igual que en HomeScreen)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        // Título de la sección de productos
        item {
            Text(
                text = "🛍️ TODOS LOS PRODUCTOS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        // Lista de productos usando items de LazyColumn para mejor performance
        items(localProducts) { product ->
            ProductCard(
                product = product,
                onProductClick = {
                    // Navegación al detalle del producto (usando el navController principal)
                    println("Producto clickeado: ${product.name}")
                    // En el futuro: navController.navigate("product_detail/${product.id}")
                },
                onAddToCartClick = { // <-- 4. Pasamos la acción de "agregar"
                    cartViewModel.addProduct(product)

                    // <-- 2. AÑADIDA LA NOTIFICACIÓN TOAST
                    Toast.makeText(context, "${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()

                    println("Agregado ${product.name} al carrito") // Opcional, para testing
                }
            )

            // Espaciado entre productos
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Espacio extra al final de la lista
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * Tarjeta individual para mostrar cada producto
 * Diseño similar a las tarjetas del carrusel pero adaptado para lista
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: Product,
    onProductClick: () -> Unit,
    onAddToCartClick: () -> Unit // <-- 5. Recibimos el nuevo lambda
) {

    Card(
        onClick = onProductClick, // <-- Esto es para ir al detalle
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            // Imagen del producto (lado izquierdo) - Usando placeholder
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imageResId) // Usa el placeholder
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen de ${product.name}",
                modifier = Modifier
                    .size(120.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = product.imageResId),
                error = painterResource(id = product.imageResId)
            )

            // Información del producto (lado derecho)
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // Nombre del producto
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                // Descripción corta (primeras palabras)
                Text(
                    text = getShortDescription(product.description),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                // Precio del producto
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatPrice(product.price),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Icono de "agregar al carrito"
                    IconButton(onClick = onAddToCartClick) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Agregar al carrito",
                            modifier = Modifier.size(24.dp), // Un poco más grande
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Función auxiliar para formatear el precio en formato CLP
 */
private fun formatPrice(price: Int): String {
    return "$${price.toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1.")}"
}

/**
 * Función auxiliar para obtener una descripción corta (primeras 50 caracteres)
 */
private fun getShortDescription(description: String): String {
    return if (description.length > 50) {
        description.substring(0, 50) + "..."
    } else {
        description
    }
}