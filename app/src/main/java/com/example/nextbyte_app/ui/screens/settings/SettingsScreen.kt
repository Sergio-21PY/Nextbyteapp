package com.example.nextbyte_app.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Se restaura la clase de datos que fue eliminada por error
data class SettingItem(val title: String, val icon: ImageVector, val route: String)

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

@Composable
fun SettingListItem(item: SettingItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            modifier = Modifier.size(26.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}