package com.example.nextbyte_app.ui.screens.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nextbyte_app.viewmodels.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePhoneNumberScreen(navController: NavController, userViewModel: UserViewModel) {
    val currentUser by userViewModel.currentUser.collectAsState()
    var newPhoneNumber by remember { mutableStateOf("") }

    // Sincronizar el estado inicial una vez que el usuario se carga
    LaunchedEffect(currentUser) {
        currentUser?.phone?.let { currentPhone ->
            if (newPhoneNumber.isEmpty()) { // Solo establecer si el campo está vacío
                newPhoneNumber = currentPhone
            }
        }
    }

    val isLoading by userViewModel.isLoading.collectAsState()
    val updateResult by userViewModel.updateResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(updateResult) {
        updateResult?.let {
            when (it) {
                is com.example.nextbyte_app.viewmodels.AuthViewModel.UpdateResult.Success -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Número de teléfono actualizado con éxito.")
                    }
                    navController.popBackStack()
                }
                is com.example.nextbyte_app.viewmodels.AuthViewModel.UpdateResult.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(it.message)
                    }
                }
            }
            userViewModel.resetUpdateResult() 
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            userViewModel.resetUpdateResult()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Cambiar Teléfono") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            // Mostrar el número de teléfono actual
            Text("Tu número actual es:", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currentUser?.phone?.ifEmpty { "No establecido" } ?: "Cargando...",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))
            
            // Campo para el nuevo número
            OutlinedTextField(
                value = newPhoneNumber,
                onValueChange = { newPhoneNumber = it },
                label = { Text("Nuevo Número de Teléfono") },
                placeholder = { Text("Ej: 123456789") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    currentUser?.uid?.let {
                        userViewModel.updatePhoneNumber(it, newPhoneNumber)
                    }
                },
                enabled = newPhoneNumber.isNotBlank() && !isLoading && newPhoneNumber != currentUser?.phone,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Actualizar Teléfono")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
