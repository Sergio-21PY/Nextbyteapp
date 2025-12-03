package com.example.nextbyte_app.ui.screens.Productos

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.viewmodels.EditProductViewModel
import com.example.nextbyte_app.viewmodels.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    productId: String? = null, // Parámetro para saber si estamos editando
    productViewModel: ProductViewModel = viewModel(),
    editViewModel: EditProductViewModel = viewModel()
) {
    val context = LocalContext.current
    val isEditMode = productId != null

    // Cargar los datos del producto si estamos en modo edición
    LaunchedEffect(productId) {
        if (isEditMode) {
            editViewModel.getProductById(productId!!)
        }
    }

    val productToEdit by editViewModel.product.collectAsState()

    // Estados del formulario
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    // Rellenar campos cuando los datos del producto a editar estén listos
    LaunchedEffect(productToEdit) {
        productToEdit?.let {
            name = it.name
            description = it.description
            price = it.price.toString()
            category = it.category
            imageUrl = it.imageUrl
            rating = it.rating.toString()
            stock = it.stock.toString()
        }
    }

    val categories by productViewModel.categories.collectAsState()
    val uploadState by productViewModel.uploadState.collectAsState()

    // Observar el estado de la operación (añadir o editar)
    LaunchedEffect(uploadState) {
        when (val state = uploadState) {
            is ProductViewModel.UploadState.Success -> {
                Toast.makeText(context, "✅ Operación exitosa", Toast.LENGTH_SHORT).show()
                productViewModel.resetUploadState()
                navController.popBackStack()
            }
            is ProductViewModel.UploadState.Error -> {
                Toast.makeText(context, "❌ ${state.message}", Toast.LENGTH_LONG).show()
                productViewModel.resetUploadState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Producto" else "Agregar Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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

            // --- CAMPOS DEL FORMULARIO ---
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del producto *") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción *") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio *") }, modifier = Modifier.fillMaxWidth(), prefix = { Text("$ ") })
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Categoría *") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("URL de la imagen") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = rating, onValueChange = { rating = it }, label = { Text("Rating (0-5)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock disponible *") }, modifier = Modifier.fillMaxWidth())

            // --- BOTÓN DE ACCIÓN ---
            Button(
                onClick = {
                    val product = Product(
                        id = productId ?: "", // Usar el id existente si estamos editando
                        name = name,
                        description = description,
                        price = price.toIntOrNull() ?: 0,
                        category = category,
                        imageUrl = imageUrl.ifEmpty { "https://via.placeholder.com/150" },
                        rating = rating.toDoubleOrNull() ?: 0.0,
                        stock = stock.toIntOrNull() ?: 0
                    )

                    if (isEditMode) {
                        productViewModel.updateProduct(product)
                    } else {
                        productViewModel.addProduct(product)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = uploadState !is ProductViewModel.UploadState.Loading && name.isNotEmpty() && description.isNotEmpty() && price.isNotEmpty() && category.isNotEmpty() && stock.isNotEmpty()
            ) {
                if (uploadState is ProductViewModel.UploadState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEditMode) "Guardando..." else "Agregando...")
                } else {
                    Text(if (isEditMode) "Guardar Cambios" else "Agregar Producto")
                }
            }
        }
    }
}
