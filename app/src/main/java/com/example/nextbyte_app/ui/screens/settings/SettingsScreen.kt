package com.example.nextbyte_app.ui.screens.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Store
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

// Las definiciones duplicadas han sido eliminadas.
// Ahora se importan desde SettingItems.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val generalSettingItems = listOf(
        SettingItem("Notificaciones", Icons.Default.Notifications, "notifications"),
        SettingItem("Tiendas físicas", Icons.Default.Store, "physical_stores"),
    )

    val legalAndHelpItems = listOf(
        SettingItem("Ayuda", Icons.AutoMirrored.Filled.HelpOutline, "help"),
        SettingItem("Términos y condiciones", Icons.AutoMirrored.Filled.Notes, "terms_and_conditions"),
        SettingItem("Acerca de Next Byte", Icons.Default.Info, "about_nextbyte"),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes Generales") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    "General", 
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), 
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(generalSettingItems) { item ->
                SettingListItem(item = item, onClick = { navController.navigate(item.route) })
            }
            item { Spacer(modifier = Modifier.padding(8.dp)) }
            item {
                Text(
                    "Información y Soporte", 
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), 
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(legalAndHelpItems) { item ->
                SettingListItem(item = item, onClick = { navController.navigate(item.route) })
            }
        }
    }
}
