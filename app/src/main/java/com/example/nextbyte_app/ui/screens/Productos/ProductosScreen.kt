package com.example.nextbyte_app.ui.screens.Productos

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nextbyte_app.data.UserRole
import com.example.nextbyte_app.ui.shared.ProductCard
import com.example.nextbyte_app.viewmodels.CartProduct
import com.example.nextbyte_app.viewmodels.CartViewModel
import com.example.nextbyte_app.viewmodels.ProductViewModel
import com.example.nextbyte_app.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    userViewModel: UserViewModel,
    productViewModel: ProductViewModel, // <<-- AHORA SE RECIBE, NO SE CREA
    category: String? = null 
) {
    val context = LocalContext.current
    // Ya no se crea una instancia local del viewModel
    val products by productViewModel.products.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    // Lógica para aplicar el filtro al entrar a la pantalla
    LaunchedEffect(category) {
        productViewModel.filterByCategory(category ?: "Todos")
    }

    // Lógica para limpiar el filtro al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            productViewModel.filterByCategory("Todos")
        }
    }

    Scaffold(
        floatingActionButton = {
            // El botón de añadir solo es visible para Admin o Manager
            if (currentUser?.role == UserRole.ADMIN || currentUser?.role == UserRole.MANAGER) {
                FloatingActionButton(
                    onClick = { navController.navigate("add_product") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar producto")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Cabecera de la sección
            Text(
                text = "🛍️ ${category?.uppercase() ?: "TODOS LOS PRODUCTOS"}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (isLoading && products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("📦 No hay productos en esta categoría")
                }
            } else {
                // Lista de productos
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(products, key = { it.id }) { product ->
                        val isFavorite = currentUser?.favoriteProductIds?.contains(product.id) == true
                        ProductCard(
                            product = product,
                            isFavorite = isFavorite,
                            onProductClick = { navController.navigate("product_detail/${product.id}") },
                            onAddToCartClick = {
                                val cartProduct = CartProduct(product.id, product.name, product.price, product.imageUrl)
                                cartViewModel.addItem(cartProduct)
                                Toast.makeText(context, "✅ ${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
                            },
                            onFavoriteClick = { userViewModel.toggleFavorite(product.id) }
                        )
                    }
                }
            }
        }
    }
}
