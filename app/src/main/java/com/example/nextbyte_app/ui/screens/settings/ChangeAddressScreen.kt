package com.example.nextbyte_app.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeAddressScreen(navController: NavController, addressViewModel: AddressViewModel = viewModel()) {
    val addresses = addressViewModel.addresses.value
    val firstAddress = addresses.firstOrNull()
    var address by remember(firstAddress) { mutableStateOf(firstAddress ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cambiar Dirección") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (firstAddress != null) {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { 
                        addressViewModel.updateAddress(firstAddress, address)
                        navController.popBackStack()
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Guardar Cambios")
                }
            } else {
                Text("Aún no tiene dirección agregada.")
            }
        }
    }
}