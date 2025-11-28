package com.example.nextbyte_app.ui.screens.Productos

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.viewmodels.ProductViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel()
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    val categories by productViewModel.categories.collectAsState()
    val uploadState by productViewModel.uploadState.collectAsState()

    // Observar estado de subida
    LaunchedEffect(uploadState) {
        when (uploadState) {
            is ProductViewModel.UploadState.Success -> {
                Toast.makeText(
                    context,
                    "✅ Producto agregado exitosamente",
                    Toast.LENGTH_SHORT
                ).show()

                // Limpiar el formulario
                name = ""
                description = ""
                price = ""
                category = ""
                imageUrl = ""
                rating = ""
                stock = ""

                // Esperar un momento para que Firebase se actualice
                delay(1000)

                // Regresar a la pantalla anterior
                navController.popBackStack()
            }
            is ProductViewModel.UploadState.Error -> {
                Toast.makeText(
                    context,
                    (uploadState as ProductViewModel.UploadState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Nuevo Producto") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            productViewModel.resetUploadState()
                            navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo Nombre
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del producto *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = name.isEmpty()
            )

            // Campo Descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción *") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                isError = description.isEmpty()
            )

            // Campo Precio
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Precio *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                prefix = { Text("$ ") },
                isError = price.isEmpty() || price.toDoubleOrNull() == null,
                supportingText = {
                    if (price.isNotEmpty() && price.toDoubleOrNull() == null) {
                        Text("Ingresa un precio válido")
                    }
                }
            )

            // Campo Categoría
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Categoría *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Ej: Smartphones, Laptops, Tablets, etc.") },
                isError = category.isEmpty()
            )

            // Sugerencias de categorías
            if (categories.isNotEmpty()) {
                Text(
                    text = "💡 Sugerencias: ${categories.take(5).joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Campo URL de imagen
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL de la imagen") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("https://ejemplo.com/imagen.jpg") }
            )

            // Campo Rating
            OutlinedTextField(
                value = rating,
                onValueChange = { rating = it },
                label = { Text("Rating (0-5)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("4.5") },
                supportingText = {
                    Text("Opcional - por defecto será 0.0")
                }
            )

            // Campo Stock
            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock disponible *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("10") },
                isError = stock.isEmpty() || stock.toIntOrNull() == null,
                supportingText = {
                    if (stock.isNotEmpty() && stock.toIntOrNull() == null) {
                        Text("Ingresa un número válido")
                    }
                }
            )

            // Botón de agregar
            Button(
                onClick = {
                    if (validateFields(name, description, price, category, stock)) {
                        val product = Product(
                            name = name,
                            description = description,
                            price = price.toInt(),
                            category = category,
                            imageUrl = if (imageUrl.isEmpty()) "https://via.placeholder.com/150" else imageUrl,
                            rating = rating.toDoubleOrNull() ?: 0.0,
                            stock = stock.toInt()
                        )
                        productViewModel.addProduct(product)
                    } else {
                        Toast.makeText(
                            context,
                            "❌ Por favor completa todos los campos requeridos correctamente",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = uploadState !is ProductViewModel.UploadState.Loading &&
                        name.isNotEmpty() &&
                        description.isNotEmpty() &&
                        price.isNotEmpty() &&
                        category.isNotEmpty() &&
                        stock.isNotEmpty()
            ) {
                if (uploadState is ProductViewModel.UploadState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregando...")
                } else {
                    Text("Agregar Producto")
                }
            }

            // Información de campos
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "📝 Información importante:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Los campos marcados con * son obligatorios")
                    Text("• El precio debe ser un número válido")
                    Text("• El stock debe ser un número entero")
                    Text("• La URL de imagen es opcional")
                }
            }
        }
    }
}

// Función de validación
private fun validateFields(
    name: String,
    description: String,
    price: String,
    category: String,
    stock: String
): Boolean {
    return name.isNotEmpty() &&
            description.isNotEmpty() &&
            price.isNotEmpty() &&
            price.toDoubleOrNull() != null &&
            category.isNotEmpty() &&
            stock.isNotEmpty() &&
            stock.toIntOrNull() != null
}