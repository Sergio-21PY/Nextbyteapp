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
    navController: NavController,
    onFavoriteClick: (Product) -> Unit,
    productViewModel: ProductViewModel = viewModel()
) {
    val products by productViewModel.products.collectAsState()
    val favoriteProducts = products.filter { it.isFavorited }

    if (favoriteProducts.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No tienes productos favoritos",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(favoriteProducts) { product ->
                ProductCard(
                    product = product,
                    onProductClick = {
                        // Navegar al detalle del producto
                    },
                    onAddToCartClick = {
                        // Agregar al carrito
                    },
                    onFavoriteClick = { onFavoriteClick(product) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}