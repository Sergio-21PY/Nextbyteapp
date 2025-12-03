package com.example.nextbyte_app.ui.screens.account

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nextbyte_app.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEmailScreen(navController: NavController, authViewModel: AuthViewModel) {
    var newEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    // Estados para manejar la UI
    val isLoading by authViewModel.isLoading.collectAsState()
    val updateResult by authViewModel.updateResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Efecto para mostrar el resultado de la operación (éxito o error)
    LaunchedEffect(updateResult) {
        updateResult?.let {
            when (it) {
                is AuthViewModel.UpdateResult.Success -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Correo actualizado con éxito. Por favor, verifica la nueva dirección.")
                    }
                    navController.popBackStack() // Vuelve a la pantalla anterior si tiene éxito
                }
                is AuthViewModel.UpdateResult.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(it.message)
                    }
                }
            }
            authViewModel.resetUpdateResult() // Limpia el resultado para no volver a mostrarlo
        }
    }

    // Limpiar el resultado cuando la pantalla se va
    DisposableEffect(Unit) {
        onDispose {
            authViewModel.resetUpdateResult()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Cambiar Correo Electrónico") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text("Nuevo Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña Actual") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { 
                        authViewModel.changeEmail(password, newEmail)
                    },
                    enabled = newEmail.isNotBlank() && password.isNotBlank() && !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Actualizar Correo")
                    }
                }
            }
        }
    }
}
