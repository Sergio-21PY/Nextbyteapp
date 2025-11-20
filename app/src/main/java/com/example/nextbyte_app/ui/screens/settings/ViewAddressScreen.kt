package com.example.nextbyte_app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAddressScreen(navController: NavController, addressViewModel: AddressViewModel = viewModel()) {
    val address = addressViewModel.address.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ver Dirección") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (address != null) {
                val parts = address.split(", ").map { it.trim() }
                val street = parts.getOrNull(0) ?: ""
                val comuna = parts.getOrNull(1) ?: ""
                val region = parts.getOrNull(2) ?: ""

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Text("Tu dirección guardada:", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    AddressDetailRow("Calle y número:", street)
                    Spacer(modifier = Modifier.height(8.dp))
                    AddressDetailRow("Comuna:", comuna)
                    Spacer(modifier = Modifier.height(8.dp))
                    AddressDetailRow("Región:", region)
                }
            } else {
                Text("Aún no tiene dirección agregada.")
                Button(
                    onClick = { navController.navigate("add_edit_address") },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Agregar Dirección")
                }
            }
        }
    }
}

@Composable
private fun AddressDetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}