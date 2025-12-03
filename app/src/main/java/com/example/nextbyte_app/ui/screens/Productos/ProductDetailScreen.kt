package com.example.nextbyte_app.ui.screens.Productos

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
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
import com.example.nextbyte_app.data.Review
import com.example.nextbyte_app.viewmodels.*
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String,
    cartViewModel: CartViewModel,
    userViewModel: UserViewModel = viewModel(),
    productViewModel: ProductViewModel = viewModel() // USAMOS EL VIEWMODEL CENTRAL
) {
    val product by productViewModel.selectedProduct.collectAsState()
    val reviews by productViewModel.reviews.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    var showReviewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        productViewModel.loadProductDetails(productId)
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
            product?.let {
                BottomAppBar(modifier = Modifier.height(80.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("$${it.price}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
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
            val p = product!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item { AsyncImage(model = p.imageUrl, contentDescription = p.name, modifier = Modifier.fillMaxWidth().height(300.dp), contentScale = ContentScale.Fit) }

                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(p.category.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(p.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Descripción", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(p.description, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
                    }
                }

                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Valoraciones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val averageRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else p.rating
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RatingBar(rating = averageRating)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("(${reviews.size} reseñas)", style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(onClick = { showReviewDialog = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Escribir una reseña")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                if (reviews.isNotEmpty()) {
                    items(reviews) { review ->
                        ReviewCard(review = review)
                    }
                } else {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                            Text("Todavía no hay reseñas. ¡Sé el primero!")
                        }
                    }
                }
            }
        }
    }

    if (showReviewDialog) {
        ReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, comment ->
                val review = Review(
                    userId = currentUser?.uid ?: "",
                    userName = currentUser?.name ?: "Anónimo",
                    rating = rating,
                    comment = comment
                )
                productViewModel.addReview(productId, review)
                showReviewDialog = false
            }
        )
    }
}

@Composable
fun RatingBar(rating: Double, maxRating: Int = 5) {
    Row {
        for (i in 1..maxRating) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (i <= rating.toInt()) Color(0xFFFFC107) else Color.Gray.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ReviewCard(review: Review) {
    val formatter = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }
    
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(review.userName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(formatter.format(review.createdAt.toDate()), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        RatingBar(rating = review.rating)
        Spacer(modifier = Modifier.height(8.dp))
        Text(review.comment, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ReviewDialog(onDismiss: () -> Unit, onSubmit: (Double, String) -> Unit) {
    var rating by remember { mutableStateOf(0.0) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Qué te pareció el producto?") },
        text = {
            Column {
                Text("Tu calificación:")
                RatingBarSelector(rating = rating, onRatingChange = { rating = it })
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Escribe tu opinión (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, comment) },
                enabled = rating > 0
            ) {
                Text("Enviar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun RatingBarSelector(rating: Double, onRatingChange: (Double) -> Unit) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        (1..5).forEach { star ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (star <= rating) Color(0xFFFFC107) else Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onRatingChange(star.toDouble()) }
                    .padding(4.dp)
            )
        }
    }
}