package com.example.nextbyte_app.ui.screens.carrito

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Data class para Producto - Definida localmente
 */
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val imageResId: Int
)

/**
 * Data class para representar un item en el carrito con cantidad
 */
data class CartItem(
    val product: Product,
    var quantity: Int = 1
)

/**
 * Lista local de productos para testing
 */
val localProducts = listOf(
    Product(101, "Ajazz AK820", "Teclado mecánico", 39999, android.R.drawable.ic_menu_gallery),
    Product(205, "HyperX Cloud III", "Auriculares gaming", 79999, android.R.drawable.ic_menu_gallery),
    Product(312, "Monitor Xiaomi", "Monitor curvo 34\"", 220999, android.R.drawable.ic_menu_gallery)
)

/**
 * Screen principal del carrito de compras
 */
@Composable
fun CarritoScreen(navController: NavController) {

    val cartItems = remember { mutableStateListOf<CartItem>() }

    LaunchedEffect(Unit) {
        if (cartItems.isEmpty()) {
            cartItems.addAll(
                listOf(
                    CartItem(localProducts[0], 1),
                    CartItem(localProducts[1], 2),
                    CartItem(localProducts[2], 1)
                )
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header simple SIN background problemático
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🛒 MI CARRITO",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        if (cartItems.isEmpty()) {
            EmptyCartState()
        } else {
            // Usamos fillMaxSize con fraction en lugar de weight
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f) // 80% del espacio
            ) {
                items(cartItems, key = { it.product.id }) { cartItem ->
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        CartItemCard(
                            cartItem = cartItem,
                            onUpdateQuantity = { product, newQuantity ->
                                val item = cartItems.find { it.product.id == product.id }
                                item?.let {
                                    if (newQuantity > 0) {
                                        it.quantity = newQuantity
                                    } else {
                                        cartItems.remove(it)
                                    }
                                }
                            },
                            onRemoveItem = { product ->
                                cartItems.removeAll { it.product.id == product.id }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            CheckoutSection(
                cartItems = cartItems,
                onCheckout = {
                    println("Procesando compra... Total: ${calculateTotal(cartItems)}")
                }
            )
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

            // Imagen del producto
            Image(
                painter = painterResource(id = cartItem.product.imageResId),
                contentDescription = "Imagen de ${cartItem.product.name}",
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
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
        // Botón disminuir
        IconButton(onClick = onDecrease) {
            Icon(Icons.Default.Remove, "Disminuir")
        }

        // Cantidad actual
        Text(
            text = quantity.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(20.dp),
            textAlign = TextAlign.Center
        )

        // Botón aumentar
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
    cartItems: List<CartItem>,
    onCheckout: () -> Unit
) {
    val total = calculateTotal(cartItems)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Subtotal y total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal:")
                Text(formatPrice(total))
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                Text("PROCEDER AL CHECKOUT")
            }
        }
    }
}

/**
 * Calcula el total del carrito
 */
private fun calculateTotal(cartItems: List<CartItem>): Int {
    return cartItems.sumOf { it.product.price * it.quantity }
}

/**
 * Función auxiliar para formatear el precio en formato CLP
 */
private fun formatPrice(price: Int): String {
    return "$${price.toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1.")}"
}