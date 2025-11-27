package com.example.nextbyte_app.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AdminAccountScreen(navController: NavController, user: UserState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Panel de Administrador",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item { ManagementSection(navController) }
        item { AnalyticsSection(navController) }
    }
}

@Composable
private fun ManagementSection(navController: NavController) {
    Card(modifier = Modifier.padding(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column {
            AdminMenuItem(icon = Icons.Default.Group, title = "Gestionar Usuarios") { /* navController.navigate("manage_users") */ }
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            AdminMenuItem(icon = Icons.Default.Inventory, title = "Gestionar Productos") { /* navController.navigate("manage_products") */ }
        }
    }
}

@Composable
private fun AnalyticsSection(navController: NavController) {
    Card(modifier = Modifier.padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column {
            AdminMenuItem(icon = Icons.Default.Analytics, title = "Ver Estadísticas de Ventas") { /* navController.navigate("sales_stats") */ }
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            AdminMenuItem(icon = Icons.Default.Settings, title = "Ajustes de la Aplicación") { /* navController.navigate("app_settings") */ }
        }
    }
}

@Composable
private fun AdminMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
