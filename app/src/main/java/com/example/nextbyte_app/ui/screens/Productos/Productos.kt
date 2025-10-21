import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.data.allProducts
import com.example.nextbyte_app.ui.screens.home.content.ECommerceBottomBar
import com.example.nextbyte_app.ui.screens.home.content.AppDestinations

/**
 * Screen principal de productos que muestra la lista de todos los productos disponibles
 * Utiliza el mismo patrón de navegación que HomeScreen con bottom bar
 */
@Composable
fun ProductosScreen(navController: NavController) {

    // NavController DEDICADO para la navegación de la barra inferior (igual que HomeScreen)
    val bottomNavController = rememberNavController()

    Scaffold(
        // Barra inferior idéntica a la de HomeScreen
        bottomBar = {
            ECommerceBottomBar(navController = bottomNavController)
        }
    ) { paddingValues ->

        // NavHost para el contenido que cambia con la barra inferior
        NavHost(
            navController = bottomNavController,
            startDestination = AppDestinations.Productos.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            // Pantalla principal de productos
            composable(AppDestinations.Productos.route) {
                ProductListContent(navController = navController)
            }

            // Otras pantallas de la barra inferior (igual que en HomeScreen)
            composable(AppDestinations.Home.route) {
                // En una implementación real, aquí iría la navegación al HomeScreen
                Text(text = "Redirigiendo a Home...")
            }
            composable(AppDestinations.Cart.route) {
                Text(text = "Contenido del Carrito")
            }
            composable(AppDestinations.Account.route) {
                Text(text = "Contenido de Cuenta")
            }
        }
    }
}

/**
 * Contenido principal de la lista de productos
 * Muestra todos los productos en formato de tarjetas
 */
@Composable
fun ProductListContent(navController: NavController) {

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
        items(allProducts) { product ->
            ProductCard(
                product = product,
                onProductClick = {
                    // Navegación al detalle del producto (usando el navController principal)
                    // Aquí puedes implementar la navegación a ProductDetailScreen.kt
                    println("Producto clickeado: ${product.name}")
                    // navController.navigate("product_detail/${product.id}")
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
fun ProductCard(product: Product, onProductClick: () -> Unit) {

    Card(
        onClick = onProductClick,
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

            // Imagen del producto (lado izquierdo)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imageResId)
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

                    // Icono de "agregar al carrito" (puedes implementar la funcionalidad después)
                    Icon(
                        painter = painterResource(id = androidx.compose.material.icons.Icons.Filled.ShoppingCart),
                        contentDescription = "Agregar al carrito",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
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