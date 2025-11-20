package com.example.nextbyte_app.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.WrongLocation
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressesScreen(navController: NavController) {
    val addressOptions = listOf(
        AccountOption("Agregar dirección", Icons.Default.AddLocationAlt) { navController.navigate("add_edit_address") },
        AccountOption("Cambiar dirección", Icons.Default.EditLocation) { navController.navigate("change_address") },
        AccountOption("Eliminar dirección", Icons.Default.WrongLocation) { navController.navigate("delete_address") }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Direcciones") },
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
                .padding(horizontal = 16.dp)
        ) {
            addressOptions.forEach { option ->
                AccountOptionItem(option = option)
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            }
        }
    }
}