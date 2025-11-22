package com.example.nextbyte_app.ui.screens

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.ui.screens.carrito.CartViewModel
import com.example.nextbyte_app.ui.shared.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    navController: NavController,
    products: List<Product>,
    onFavoriteClick: (Product) -> Unit,
    cartViewModel: CartViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)
) {
    val favoriteProducts = products.filter { it.isFavorited }
    val context = LocalContext.current

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (favoriteProducts.isEmpty()) {
                item {
                    Text(
                        text = "No tienes productos favoritos todavía.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(favoriteProducts) { product ->
                    ProductCard(
                        product = product,
                        onProductClick = { /* Lógica para ir al detalle si es necesario */ },
                        onAddToCartClick = {
                            cartViewModel.addProduct(product)
                            Toast.makeText(context, "${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
                        },
                        onFavoriteClick = { onFavoriteClick(product) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}