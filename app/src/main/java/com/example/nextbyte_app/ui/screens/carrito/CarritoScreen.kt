package com.example.nextbyte_app.ui.screens.carrito

// Imports de Data
import android.widget.Toast
import com.example.nextbyte_app.data.CartItem
import com.example.nextbyte_app.data.Product

// Imports de UI y Layout
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.activity.ComponentActivity

//Imports para la sincronizacion de imagenes (pantallas)
import coil.compose.AsyncImage
import coil.request.ImageRequest


/**
 * Screen principal del carrito de compras
 */
@Composable
fun CarritoScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    val cartItems: List<CartItem> = cartViewModel.cartItems

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🛒 MI CARRITO", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        if (cartItems.isEmpty()) {
            EmptyCartState()
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItems, key = { it.product.id }) { cartItem ->
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        CartItemCard(
                            cartItem = cartItem,
                            onUpdateQuantity = cartViewModel::updateQuantity,
                            onRemoveItem = cartViewModel::removeProduct
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                item {
                    DiscountSection(cartViewModel = cartViewModel)
                }
            }
            CheckoutSection(
                subtotal = cartViewModel.total,
                discount = cartViewModel.discountAmount,
                total = cartViewModel.finalTotal,
                onCheckout = cartViewModel::processCheckout
            )
        }
    }
}

@Composable
fun DiscountSection(cartViewModel: CartViewModel) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = cartViewModel.discountCode,
            onValueChange = { cartViewModel.discountCode = it },
            label = { Text("Código de descuento") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = {
            val message = cartViewModel.applyDiscountCode()
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }) {
            Text("Aplicar")
        }
    }
}


/**
 * Estado cuando el carrito está vacío
 */
@Composable
fun EmptyCartState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Carrito vacío",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tu carrito está vacío",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Agrega algunos productos increíbles",
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Tarjeta individual para cada producto en el carrito
 * (Versión con AsyncImage para que no se cierre)
 */
@Composable
fun CartItemCard(
    cartItem: CartItem,
    onUpdateQuantity: (Product, Int) -> Unit,
    onRemoveItem: (Product) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            // Usamos AsyncImage (de Coil) igual que en ProductoScreem
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cartItem.product.imageResId)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen de ${cartItem.product.name}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = cartItem.product.imageResId),
                error = painterResource(id = android.R.drawable.ic_menu_gallery)
            )

            // Información del producto
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxHeight()
            ) {

                // Nombre del producto
                Text(
                    text = cartItem.product.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Precio unitario
                Text(
                    text = "Precio: ${formatPrice(cartItem.product.price)}",
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Controles
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Contador de cantidad
                    QuantitySelector(
                        quantity = cartItem.quantity,
                        onIncrease = { onUpdateQuantity(cartItem.product, cartItem.quantity + 1) },
                        onDecrease = { onUpdateQuantity(cartItem.product, cartItem.quantity - 1) }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Precio total
                    Text(
                        text = formatPrice(cartItem.product.price * cartItem.quantity),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Botón eliminar
                    IconButton(
                        onClick = { onRemoveItem(cartItem.product) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar producto"
                        )
                    }
                }
            }
        }
    }
}

/**
 * Selector de cantidad con botones + y -
 */
@Composable
fun QuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onDecrease) {
            Icon(Icons.Default.Remove, "Disminuir")
        }
        Text(
            text = quantity.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(20.dp),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = onIncrease) {
            Icon(Icons.Default.Add, "Aumentar")
        }
    }
}


/**
 * Sección inferior con total y botón de checkout
 */
@Composable
fun CheckoutSection(
    subtotal: Int,
    discount: Int,
    total: Int,
    onCheckout: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal:")
                Text(formatPrice(subtotal))
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (discount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Descuento (PROMO20):")
                    Text("-${formatPrice(discount)}", color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Envío:")
                Text("Gratis")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // Total final
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "TOTAL:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatPrice(total),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de checkout
            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("PAGAR")
            }
        }
    }
}


/**
 * Función auxiliar para formatear el precio en formato CLP
 */
private fun formatPrice(price: Int): String {
    return "$${price.toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1.")}"
}