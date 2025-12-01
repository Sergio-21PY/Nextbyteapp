package com.example.nextbyte_app.ui.screens.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
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
fun UserSettingsScreen(navController: NavController) {
    val accountSettingItems = listOf(
        SettingItem("Cambiar correo", Icons.Default.Email, "change_email"),
        SettingItem("Cambiar contraseña", Icons.Default.Lock, "change_password"),
        SettingItem("Cambiar dirección", Icons.Default.Home, "change_address")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Opciones de Cuenta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(accountSettingItems) { item ->
                SettingListItem(item = item, onClick = { navController.navigate(item.route) })
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            }
        }
    }
}