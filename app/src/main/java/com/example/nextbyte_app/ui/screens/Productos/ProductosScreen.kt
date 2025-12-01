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
import com.example.nextbyte_app.ui.shared.ProductCard
import com.example.nextbyte_app.viewmodels.CartProduct
import com.example.nextbyte_app.viewmodels.CartViewModel
import com.example.nextbyte_app.viewmodels.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    navController: NavController,
    cartViewModel: CartViewModel // <-- Recibido como parámetro
) {
    val context = LocalContext.current
    val productViewModel: ProductViewModel = viewModel()
    val products by productViewModel.products.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_product") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar producto")
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CircularProgressIndicator()
                    Text("Cargando productos...")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)
            ) {
                item {
                    Column {
                        Text(
                            text = "🛍️ TODOS LOS PRODUCTOS",
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

                if (products.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text("📦 No hay productos", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Usa el botón + para agregar el primer producto", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                } else {
                    items(products) { product ->
                        ProductCard(
                            product = product,
                            onProductClick = {},
                            onAddToCartClick = {
                                val cartProduct = CartProduct(
                                    id = product.id,
                                    name = product.name,
                                    price = product.price.toDouble(),
                                    imageUrl = product.imageUrl
                                )
                                cartViewModel.addItem(cartProduct)
                                Toast.makeText(context, "✅ ${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
                            },
                            onFavoriteClick = {
                                Toast.makeText(context, "❤️ ${product.name} agregado a favoritos", Toast.LENGTH_SHORT).show()
                            }
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
