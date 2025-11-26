package com.example.nextbyte_app.ui.screens.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.nextbyte_app.R

@Composable
fun UserAccountScreen(navController: NavController, user: UserState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 16.dp)
    ) {
        item {
            ProfileHeader(user = user) { navController.navigate("edit_profile") }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Mi Cuenta",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
        item { AccountSection(navController) }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ajustes de la App",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
        item { AppSettingsSection(navController) }
    }
}

@Composable
private fun ProfileHeader(user: UserState, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = user.profileImageUri ?: R.drawable.perfil_image),
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Ver y editar perfil",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onEditClick)
            )
        }
    }
}

@Composable
private fun AccountSection(navController: NavController) {
    Card(modifier = Modifier.padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column {
            UserMenuItem(icon = Icons.Default.ListAlt, title = "Mis Pedidos") { /* navController.navigate("orders") */ }
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            UserMenuItem(icon = Icons.Default.LocationOn, title = "Direcciones de Envío") { /* navController.navigate("addresses") */ }
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            UserMenuItem(icon = Icons.Default.CreditCard, title = "Métodos de Pago") { /* navController.navigate("payment_methods") */ }
        }
    }
}

@Composable
private fun AppSettingsSection(navController: NavController) {
    Card(modifier = Modifier.padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column {
            UserMenuItem(icon = Icons.Default.Notifications, title = "Notificaciones") { /* navController.navigate("notifications") */ }
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            UserMenuItem(icon = Icons.Default.Security, title = "Seguridad") { /* navController.navigate("security") */ }
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            UserMenuItem(icon = Icons.Default.HelpOutline, title = "Ayuda y Soporte") { /* navController.navigate("help") */ }
        }
    }
}

@Composable
private fun UserMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
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
