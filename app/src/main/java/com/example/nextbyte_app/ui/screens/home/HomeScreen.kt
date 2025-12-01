package com.example.nextbyte_app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.data.UserRole
import com.example.nextbyte_app.ui.screens.AppDestinations
import com.example.nextbyte_app.ui.screens.ECommerceBottomBar
import com.example.nextbyte_app.ui.screens.Productos.ProductosScreen
import com.example.nextbyte_app.ui.screens.account.AccountScreen
import com.example.nextbyte_app.ui.screens.carrito.CarritoScreen
import com.example.nextbyte_app.ui.shared.MainTopBar
import com.example.nextbyte_app.viewmodels.AuthViewModel
import com.example.nextbyte_app.viewmodels.CartViewModel
import com.example.nextbyte_app.viewmodels.ProductViewModel
import com.example.nextbyte_app.viewmodels.UserViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentUserState = userViewModel.currentUser.collectAsState()
    val currentUser = currentUserState.value
    val userName = currentUser?.name ?: "Usuario"
    val userRole = currentUser?.role?.let { getRoleDisplayName(it) } ?: "Cliente"

    val title = when (currentRoute) {
        AppDestinations.Home.route -> "Inicio"
        AppDestinations.Productos.route -> "Productos"
        AppDestinations.Cart.route -> "Carrito"
        AppDestinations.Account.route -> "Mi Cuenta"
        else -> "Next-Byte"
    }

    Scaffold(
        topBar = {
            MainTopBar(
                title = title,
                onSettingsClick = { navController.navigate("settings") }
            )
        },
        bottomBar = {
            ECommerceBottomBar(navController = bottomNavController)
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = AppDestinations.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(AppDestinations.Home.route) {
                HomeContent(
                    navController = navController,
                    userName = userName,
                    userRole = userRole
                )
            }

            composable(AppDestinations.Productos.route) {
                ProductosScreen(
                    navController = navController,
                    cartViewModel = cartViewModel
                )
            }

            composable(AppDestinations.Cart.route) {
                CarritoScreen(
                    navController = navController,
                    cartViewModel = cartViewModel
                )
            }

            composable(AppDestinations.Account.route) {
                AccountScreen(
                    navController = bottomNavController,
                    authViewModel = authViewModel,
                    userViewModel = userViewModel
                )
            }
        }
    }
}

@Composable
fun HomeContent(
    navController: NavController,
    userName: String,
    userRole: String,
    productViewModel: ProductViewModel = viewModel()
) {
    val products by productViewModel.products.collectAsState()
    val featuredProducts = products.take(4)
    val bestSellers = products.filter { it.rating >= 4.5 }.take(6)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { UserGreetingCard(userName = userName, userRole = userRole) }
        item { SpecialOfferCard(navController = navController) }
        item {
            SectionHeader(
                title = "🔥 Productos Destacados",
                subtitle = "Lo más vendido esta semana",
                onSeeAllClick = { navController.navigate(AppDestinations.Productos.route) }
            )
        }
        item {
            if (featuredProducts.isEmpty()) {
                EmptyProductsMessage()
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    items(featuredProducts) { product ->
                        FeaturedProductCard(product = product, onProductClick = {})
                    }
                }
            }
        }
        item {
            SectionHeader(
                title = "⭐ Mejor Calificados",
                subtitle = "Productos con mejores reseñas",
                onSeeAllClick = { navController.navigate(AppDestinations.Productos.route) }
            )
        }
        item {
            if (bestSellers.isEmpty()) {
                EmptyProductsMessage()
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    items(bestSellers) { product ->
                        RatedProductCard(product = product, onProductClick = {})
                    }
                }
            }
        }
        item { SectionHeader(title = "📱 Categorías", subtitle = "Explora por categoría") }
        item { CategoriesRow(navController = navController) }
        item { AdminQuickAccess(navController = navController) }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun UserGreetingCard(userName: String, userRole: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "¡Hola, $userName! 👋",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rol: $userRole",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = userRole,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SpecialOfferCard(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp).height(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6A1B9A))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    brush = Brush.verticalGradient(colors = listOf(Color(0xFF8E24AA), Color(0xFF6A1B9A)))
                )
            )
            Row(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("OFERTA ESPECIAL", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Hasta 40% OFF", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("En smartphones seleccionados", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate(AppDestinations.Productos.route) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF6A1B9A))
                    ) {
                        Text("Ver Ofertas", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String, onSeeAllClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
        onSeeAllClick?.let {
            TextButton(onClick = it) {
                Text("Ver todo", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun FeaturedProductCard(product: Product, onProductClick: () -> Unit) {
    Card(
        modifier = Modifier.width(280.dp).height(120.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onProductClick
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(product.imageUrl).crossfade(true).build(),
                contentDescription = product.name,
                modifier = Modifier.width(100.dp).fillMaxHeight().clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp).weight(1f)) {
                Text(product.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text("$${product.price}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = "Rating", modifier = Modifier.size(16.dp), tint = Color(0xFFFFA000))
                    Text("${product.rating}", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}

@Composable
fun RatedProductCard(product: Product, onProductClick: () -> Unit) {
    Card(
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onProductClick
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(product.imageUrl).crossfade(true).build(),
                contentDescription = product.name,
                modifier = Modifier.height(120.dp).fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(product.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text("$${product.price}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun CategoriesRow(navController: NavController) {
    val categories = listOf(
        "Smartphones" to "📱", "Laptops" to "💻", "Tablets" to "📟",
        "Audio" to "🎧", "Smartwatch" to "⌚", "Accesorios" to "🔌"
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        items(categories) { (category, emoji) ->
            CategoryChip(category, emoji) { navController.navigate(AppDestinations.Productos.route) }
        }
    }
}

@Composable
fun CategoryChip(category: String, emoji: String, onClick: () -> Unit) {
    FilterChip(selected = false, onClick = onClick, label = { Text("$emoji $category") })
}

@Composable
fun AdminQuickAccess(navController: NavController) {
    val userViewModel: UserViewModel = viewModel()
    val currentUser by userViewModel.currentUser.collectAsState()
    if (currentUser?.role == UserRole.ADMIN || currentUser?.role == UserRole.MANAGER) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("⚡ Acceso Rápido", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { navController.navigate("add_product") }, modifier = Modifier.weight(1f)) {
                        Text("➕ Agregar Producto")
                    }
                    if (currentUser?.role == UserRole.ADMIN) {
                        Button(
                            onClick = { navController.navigate("admin_panel") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                        ) {
                            Text("👑 Admin Panel")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyProductsMessage() {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("📦 No hay productos disponibles", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Text("Agrega algunos productos para comenzar", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}

private fun getRoleDisplayName(role: UserRole): String {
    return when (role) {
        UserRole.ADMIN -> "Administrador"
        UserRole.MANAGER -> "Manager"
        UserRole.CUSTOMER -> "Cliente"
        UserRole.GUEST -> "Invitado"
    }
}
