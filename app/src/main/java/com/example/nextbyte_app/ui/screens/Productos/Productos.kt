package com.example.nextbyte_app.ui.screens.Productos

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.ui.screens.carrito.CartViewModel
import com.example.nextbyte_app.ui.shared.ProductCard

@Composable
fun ProductosScreen(
    navController: NavController,
    products: List<Product>,
    onFavoriteClick: (Product) -> Unit,
    cartViewModel: CartViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Text(
                text = "🛍️ TODOS LOS PRODUCTOS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        items(products) { product ->
            ProductCard(
                product = product,
                onProductClick = {
                    // Navegación al detalle del producto
                },
                onAddToCartClick = {
                    cartViewModel.addProduct(product)
                    Toast.makeText(context, "${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
                },
                onFavoriteClick = { onFavoriteClick(product) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}