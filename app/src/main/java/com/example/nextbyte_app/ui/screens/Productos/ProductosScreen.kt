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
import androidx.lifecycle.viewmodel.compose.viewModel
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
    category: String? = null 
) {
    val context = LocalContext.current
    val productViewModel: ProductViewModel = viewModel()
    val products by productViewModel.products.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    LaunchedEffect(category, isLoading) {
        if (!isLoading) {
            productViewModel.filterByCategory(category ?: "Todos")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            productViewModel.filterByCategory("Todos")
        }
    }

    Scaffold(
        floatingActionButton = {
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
        if (isLoading && products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)
            ) {
                item {
                    Column {
                        Text(
                            text = "🛍️ ${category?.uppercase() ?: "TODOS LOS PRODUCTOS"}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        if (products.isNotEmpty()) {
                            Text(
                                text = "${products.size} productos encontrados",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                }

                if (products.isEmpty() && !isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📦 No hay productos en esta categoría", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(products, key = { it.id }) { product ->
                        // <<-- LÓGICA DE FAVORITOS CORREGIDA -->>
                        val isFavorite = currentUser?.favoriteProductIds?.contains(product.id) == true
                        ProductCard(
                            product = product,
                            isFavorite = isFavorite,
                            onProductClick = { navController.navigate("product_detail/${product.id}") }, 
                            onAddToCartClick = {
                                val cartProduct = CartProduct(
                                    id = product.id,
                                    name = product.name,
                                    price = product.price,
                                    imageUrl = product.imageUrl
                                )
                                cartViewModel.addItem(cartProduct)
                                Toast.makeText(context, "✅ ${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
                            },
                            onFavoriteClick = { userViewModel.toggleFavorite(product.id) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}