package com.example.nextbyte_app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nextbyte_app.ui.shared.ProductCard
import com.example.nextbyte_app.viewmodels.CartProduct
import com.example.nextbyte_app.viewmodels.CartViewModel
import com.example.nextbyte_app.viewmodels.ProductViewModel
import com.example.nextbyte_app.viewmodels.UserViewModel

@Composable
fun FavoritosScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    userViewModel: UserViewModel // <-- ViewModel de Usuario AÑADIDO
) {
    val context = LocalContext.current
    val productViewModel: ProductViewModel = viewModel()
    val allProducts by productViewModel.products.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    // <<-- LÓGICA DE FAVORITOS CORREGIDA -->>
    // Filtramos la lista completa de productos basándonos en los IDs de favoritos del usuario actual
    val favoriteProducts = remember(allProducts, currentUser) {
        allProducts.filter { product -> currentUser?.favoriteProductIds?.contains(product.id) == true }
    }

    if (favoriteProducts.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "❤️",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = "No tienes productos favoritos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Agrega productos a favoritos para verlos aquí",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "❤️ Mis Favoritos",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${favoriteProducts.size} productos favoritos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(favoriteProducts, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    isFavorite = true, // <-- PARÁMETRO AÑADIDO
                    onProductClick = {
                        navController.navigate("product_detail/${product.id}")
                    },
                    onAddToCartClick = {
                        val cartProduct = CartProduct(
                            id = product.id,
                            name = product.name,
                            price = product.price, // Corregido: ya es Double
                            imageUrl = product.imageUrl
                        )
                        cartViewModel.addItem(cartProduct)
                        Toast.makeText(context, "✅ ${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
                    },
                    onFavoriteClick = {
                        userViewModel.toggleFavorite(product.id)
                        Toast.makeText(context, "💔 ${product.name} eliminado de favoritos", Toast.LENGTH_SHORT).show()
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
