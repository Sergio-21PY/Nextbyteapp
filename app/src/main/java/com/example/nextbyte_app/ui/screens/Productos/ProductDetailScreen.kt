package com.example.nextbyte_app.ui.screens.Productos

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.viewmodels.CartProduct
import com.example.nextbyte_app.viewmodels.CartViewModel
import com.example.nextbyte_app.viewmodels.ProductDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String,
    cartViewModel: CartViewModel,
    detailViewModel: ProductDetailViewModel = viewModel()
) {
    val product by detailViewModel.product.collectAsState()
    val isLoading by detailViewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(productId) {
        detailViewModel.loadProduct(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            // Barra inferior con precio y botón de añadir al carrito
            product?.let {
                BottomAppBar(modifier = Modifier.height(80.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Precio:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "$${it.price}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(
                            onClick = {
                                val cartProduct = CartProduct(it.id, it.name, it.price, it.imageUrl)
                                cartViewModel.addItem(cartProduct)
                                Toast.makeText(context, "✅ ${it.name} agregado al carrito", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.height(50.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Añadir al Carrito")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (isLoading || product == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Usamos el producto real
            val p = product!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Imagen del producto
                AsyncImage(
                    model = p.imageUrl,
                    contentDescription = p.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.White),
                    contentScale = ContentScale.Fit
                )

                // Contenido del detalle
                Column(modifier = Modifier.padding(16.dp)) {
                    // Categoría y Rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = p.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        // TODO: Rating con estrellas
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Nombre del producto
                    Text(p.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Descripción
                    Text("Descripción", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(p.description, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
                }
            }
        }
    }
}