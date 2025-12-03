package com.example.nextbyte_app.ui.screens.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nextbyte_app.viewmodels.AuthViewModel
import com.example.nextbyte_app.viewmodels.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAddressScreen(navController: NavController, userViewModel: UserViewModel) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val addresses by userViewModel.addresses.collectAsState()
    val addressUpdateResult by userViewModel.addressUpdateResult.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(addressUpdateResult) {
        addressUpdateResult?.let {
            when (it) {
                is AuthViewModel.UpdateResult.Success -> {
                    scope.launch { snackbarHostState.showSnackbar("Dirección actualizada con éxito.") }
                }
                is AuthViewModel.UpdateResult.Error -> {
                    scope.launch { snackbarHostState.showSnackbar(it.message) }
                }
            }
            userViewModel.resetAddressUpdateResult()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            userViewModel.resetAddressUpdateResult()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Dirección")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (addresses.isEmpty()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text("No tienes direcciones guardadas.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(addresses) { address ->
                        ListItem(
                            headlineContent = { Text(address) },
                            trailingContent = {
                                IconButton(onClick = {
                                    currentUser?.uid?.let { userId -> // <-- CORRECCIÓN
                                        userViewModel.deleteAddress(userId, address)
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar Dirección", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }

        if (showAddDialog) {
            AddAddressDialog(
                onDismiss = { showAddDialog = false },
                onAddAddress = { street, city, zip ->
                    currentUser?.uid?.let { userId -> // <-- CORRECCIÓN
                        val fullAddress = "$street, $city, $zip"
                        userViewModel.addAddress(userId, fullAddress)
                    }
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun AddAddressDialog(onDismiss: () -> Unit, onAddAddress: (String, String, String) -> Unit) {
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Nueva Dirección") },
        text = {
            Column {
                OutlinedTextField(value = street, onValueChange = { street = it }, label = { Text("Calle y Número") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Ciudad") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = zipCode, onValueChange = { zipCode = it }, label = { Text("Código Postal") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (street.isNotBlank() && city.isNotBlank() && zipCode.isNotBlank()) {
                        onAddAddress(street, city, zipCode)
                    }
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
