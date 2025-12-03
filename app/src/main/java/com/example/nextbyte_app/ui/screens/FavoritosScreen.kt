package com.example.nextbyte_app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val productViewModel: ProductViewModel = viewModel()
    val allProducts by productViewModel.products.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    val favoriteProducts = remember(allProducts, currentUser) {
        allProducts.filter { product -> currentUser?.favoriteProductIds?.contains(product.id) == true }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Favoritos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (favoriteProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("❤️", style = MaterialTheme.typography.headlineLarge)
                    Text("No tienes productos favoritos", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Agrega productos a favoritos para verlos aquí", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "${favoriteProducts.size} productos encontrados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(favoriteProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        isFavorite = true,
                        onProductClick = { navController.navigate("product_detail/${product.id}") },
                        onAddToCartClick = {
                            val cartProduct = CartProduct(product.id, product.name, product.price, product.imageUrl)
                            cartViewModel.addItem(cartProduct)
                            Toast.makeText(context, "✅ ${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
                        },
                        onFavoriteClick = {
                            userViewModel.toggleFavorite(product.id)
                            Toast.makeText(context, "💔 ${product.name} eliminado de favoritos", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}
