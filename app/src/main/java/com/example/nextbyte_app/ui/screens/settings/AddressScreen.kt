package com.example.nextbyte_app.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nextbyte_app.viewmodels.AddressViewModel
import com.example.nextbyte_app.viewmodels.AuthViewModel
import com.example.nextbyte_app.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    navController: NavController, 
    userViewModel: UserViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val addresses by userViewModel.addresses.collectAsState()
    val userId = authViewModel.getCurrentUserId()
    var showAddDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val addressUpdateResult by userViewModel.addressUpdateResult.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            userViewModel.loadAddresses(userId)
        }
    }
    
    LaunchedEffect(addressUpdateResult) {
        when (val result = addressUpdateResult) {
            is AuthViewModel.UpdateResult.Success -> {
                Toast.makeText(context, "Dirección actualizada", Toast.LENGTH_SHORT).show()
                userViewModel.resetAddressUpdateResult()
            }
            is AuthViewModel.UpdateResult.Error -> {
                Toast.makeText(context, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                userViewModel.resetAddressUpdateResult()
            }
            null -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Direcciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Dirección")
                Spacer(modifier = Modifier.padding(start = 4.dp))
                Text("Agregar Dirección")
            }
        }
    ) { paddingValues ->
        if (addresses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No tienes direcciones guardadas.")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
                items(addresses) { address ->
                    AddressItem(
                        address = address, 
                        onDelete = { 
                            if (userId.isNotEmpty()) {
                                userViewModel.deleteAddress(userId, address) 
                            }
                        }, 
                        onEdit = { /* TODO: Navegar a editar dirección */ }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        var newAddress by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nueva Dirección") },
            text = {
                OutlinedTextField(
                    value = newAddress,
                    onValueChange = { newAddress = it },
                    label = { Text("Escribe tu dirección completa") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (userId.isNotEmpty()) {
                            userViewModel.addAddress(userId, newAddress)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun AddressItem(address: String, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = address, modifier = Modifier.weight(1f))
            IconButton(onClick = onEdit, enabled = false) { // Deshabilitado por ahora
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}