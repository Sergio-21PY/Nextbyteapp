package com.example.nextbyte_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.ui.shared.ProductCard
import com.example.nextbyte_app.viewmodels.ProductViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun FavoritosScreen(
    navController: NavController
) {
    val productViewModel: ProductViewModel = viewModel()
    val products by productViewModel.products.collectAsState()
    val favoriteProducts = products.filter { it.isFavorited }

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

            items(favoriteProducts) { product ->
                ProductCard(
                    product = product,
                    onProductClick = {
                        // Navegar al detalle del producto
                        // navController.navigate("product_detail/${product.id}")
                    },
                    onAddToCartClick = {
                        // Agregar al carrito - puedes implementar esto después
                    },
                    onFavoriteClick = {
                        // Lógica para quitar de favoritos
                        // Por ahora, puedes implementar un toggle simple
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