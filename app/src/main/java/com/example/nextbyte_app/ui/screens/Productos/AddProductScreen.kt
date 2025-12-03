package com.example.nextbyte_app.ui.screens.Productos

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.viewmodels.EditProductViewModel
import com.example.nextbyte_app.viewmodels.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    productId: String? = null,
    productViewModel: ProductViewModel = viewModel(),
    editViewModel: EditProductViewModel = viewModel()
) {
    val context = LocalContext.current
    val isEditMode = productId != null

    // --- STATE MANAGEMENT ---
    val productToEdit by editViewModel.product.collectAsState()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf("") }

    val uploadState by productViewModel.uploadState.collectAsState()

    // --- IMAGE PICKER ---
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    // --- EFFECT HANDLERS ---
    LaunchedEffect(productId) {
        if (isEditMode) {
            editViewModel.getProductById(productId!!)
        }
    }

    LaunchedEffect(productToEdit) {
        productToEdit?.let {
            name = it.name
            description = it.description
            code = it.code
            price = it.price.toString()
            cost = it.cost.toString()
            category = it.category
            stock = it.stock.toString()
            imageUrl = it.imageUrl
        }
    }

    LaunchedEffect(uploadState) {
        when (val state = uploadState) {
            is ProductViewModel.UploadState.Success -> {
                Toast.makeText(context, "✅ Producto guardado con éxito", Toast.LENGTH_SHORT).show()
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
            
            // --- IMAGE SELECTOR ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(model = imageUri, contentDescription = "Imagen seleccionada", contentScale = ContentScale.Crop)
                } else if (imageUrl.isNotEmpty()) {
                    AsyncImage(model = imageUrl, contentDescription = "Imagen del producto", contentScale = ContentScale.Crop)
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("Seleccionar Imagen")
                    }
                }
            }
            
            // --- FORM FIELDS ---
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del producto *") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Código/SKU *") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción *") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Precio Costo *") }, modifier = Modifier.weight(1f), prefix = { Text("$") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio Venta *") }, modifier = Modifier.weight(1f), prefix = { Text("$") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Categoría *") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock *") }, modifier = Modifier.weight(1f))
            }

            // --- SUBMIT BUTTON ---
            Button(
                onClick = {
                    val product = Product(
                        id = productId ?: "", 
                        name = name, 
                        description = description, 
                        code = code,
                        price = price.toDoubleOrNull() ?: 0.0, 
                        cost = cost.toDoubleOrNull() ?: 0.0, 
                        category = category, 
                        stock = stock.toIntOrNull() ?: 0,
                        imageUrl = imageUrl
                    )

                    if (imageUri != null) {
                        productViewModel.uploadProductImage(imageUri!!, product, isEditMode)
                    } else if (isEditMode) {
                        productViewModel.updateProduct(product)
                    } else {
                        productViewModel.addProduct(product)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = uploadState !is ProductViewModel.UploadState.Loading && name.isNotEmpty() && code.isNotEmpty() && price.isNotEmpty() && cost.isNotEmpty() && category.isNotEmpty() && stock.isNotEmpty()
            ) {
                if (uploadState is ProductViewModel.UploadState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (isEditMode) "Guardar Cambios" else "Agregar Producto")
                }
            }
        }
    }
}
