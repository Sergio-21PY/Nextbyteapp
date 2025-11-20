package com.example.nextbyte_app.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAddressScreen(navController: NavController, addressViewModel: AddressViewModel = viewModel()) {
    val addresses = addressViewModel.addresses
    var selectedAddresses by remember { mutableStateOf(emptySet<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eliminar Dirección") },
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
            if (addresses.value.isNotEmpty()) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(addresses.value) { address ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    selectedAddresses = if (selectedAddresses.contains(address)) {
                                        selectedAddresses - address
                                    } else {
                                        selectedAddresses + address
                                    }
                                 },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedAddresses.contains(address),
                                onCheckedChange = { isChecked ->
                                    selectedAddresses = if (isChecked) {
                                        selectedAddresses + address
                                    } else {
                                        selectedAddresses - address
                                    }
                                }
                            )
                            Text(text = address, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
                Button(
                    onClick = { 
                        selectedAddresses.forEach { addressViewModel.deleteAddress(it) }
                        selectedAddresses = emptySet()
                     },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Eliminar Seleccionadas")
                }
            } else {
                Text("Aún no tiene dirección agregada.")
            }
        }
    }
}