package com.example.nextbyte_app.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun UserSettingsScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Ajustes Generales",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item { Spacer(modifier = Modifier.size(16.dp)) }

        // Sección de Preferencias
        item {
            SettingsSectionCard(
                title = "Preferencias",
                items = listOf(
                    SettingsItemData.Switch(Icons.Default.DarkMode, "Modo Oscuro", true) { /* TODO */ },
                    SettingsItemData.Switch(Icons.Default.Notifications, "Activar Notificaciones", true) { /* TODO */ },
                    SettingsItemData.Navigation(Icons.Default.Language, "Idioma") { /* TODO */ }
                )
            )
        }

        item { Spacer(modifier = Modifier.size(16.dp)) }

        // Sección de Información y Soporte
        item {
            SettingsSectionCard(
                title = "Información y Soporte",
                items = listOf(
                    // ¡NUEVO ITEM AÑADIDO!
                    SettingsItemData.Navigation(Icons.Default.Store, "Nuestras Tiendas") { navController.navigate("physical_stores") },
                    SettingsItemData.Navigation(Icons.AutoMirrored.Filled.HelpOutline, "Centro de Ayuda") { navController.navigate("help") },
                    SettingsItemData.Navigation(Icons.Default.Policy, "Términos y Condiciones") { navController.navigate("terms_and_conditions") },
                    SettingsItemData.Navigation(Icons.Default.Info, "Acerca de Next-Byte") { navController.navigate("about_nextbyte") }
                )
            )
        }
    }
}

private sealed class SettingsItemData {
    data class Navigation(val icon: ImageVector, val title: String, val onClick: () -> Unit) : SettingsItemData()
    data class Switch(val icon: ImageVector, val title: String, val isChecked: Boolean, val onCheckedChange: (Boolean) -> Unit) : SettingsItemData()
}

@Composable
private fun SettingsSectionCard(title: String, items: List<SettingsItemData>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    when (item) {
                        is SettingsItemData.Navigation -> SettingsRow(icon = item.icon, title = item.title, onClick = item.onClick)
                        is SettingsItemData.Switch -> SettingsSwitchRow(icon = item.icon, title = item.title, isChecked = item.isChecked, onCheckedChange = item.onCheckedChange)
                    }
                    if (index < items.lastIndex) {
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsSwitchRow(icon: ImageVector, title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    var localChecked by remember { mutableStateOf(isChecked) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                localChecked = !localChecked
                onCheckedChange(localChecked) 
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = localChecked, onCheckedChange = { 
            localChecked = it
            onCheckedChange(it) 
        })
    }
}
