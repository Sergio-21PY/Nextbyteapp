package com.example.nextbyte_app.ui.screens.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nextbyte_app.R

// Modelo de datos del usuario
data class User(
    val name: String,
    val email: String,
    val isAdmin: Boolean
)

@Composable
fun AccountScreen(navController: NavController) {
    // --- SIMULACIÓN DE INICIO DE SESIÓN ---
    // admin:
    val loginEmail by remember { mutableStateOf("admin@admin.cl") }
    val loginPassword by remember { mutableStateOf("1234") }
    // --- LÓGICA PARA DETERMINAR EL TIPO DE USUARIO ---
    val currentUser: User? = when {
        loginEmail == "admin@admin.cl" && loginPassword == "1234" -> {
            User(name = "Administrador", email = "admin@admin.cl", isAdmin = true)
        }
        loginEmail == "user@user.cl" && loginPassword == "1234" -> {
            User(name = "Usuario", email = "user@user.cl", isAdmin = false)
        }
        else -> {
            null // Si los datos no coinciden, no hay usuario
        }
    }

    // Solo mostramos el contenido si el inicio de sesión fue "exitoso"
    if (currentUser != null) {

        val profileImageRes = if (currentUser.isAdmin) {
            R.drawable.img // Cambia "img" si tu archivo se llama diferente (ej: logo_admin)
        } else {
            R.drawable.perfil_image // Cambia "perfil_image" si tu archivo se llama diferente (ej: perfil_usuario)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileHeader(user = currentUser, profileImageRes = profileImageRes)
            Spacer(modifier = Modifier.height(32.dp))
            SettingsSection(user = currentUser, navController = navController)
            Spacer(modifier = Modifier.weight(1f))
            LogoutButton {
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: Credenciales incorrectas.")
        }
    }
}
@Composable
fun ProfileHeader(user: User, profileImageRes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = profileImageRes),
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(if (user.isAdmin) RoundedCornerShape(16.dp) else CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SettingsSection(user: User, navController: NavController) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            SettingsItem(icon = Icons.Default.Edit, title = "Editar Perfil") { /* Navigar */ }
            SettingsItem(icon = Icons.Default.CreditCard, title = "Métodos de Pago") { /* Navigar */ }
            SettingsItem(icon = Icons.Default.Security, title = "Seguridad") { /* Navigar */ }

            if (user.isAdmin) {
                AdminSettings()
            }
        }
    }
}

@Composable
fun AdminSettings() {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Panel de Administrador",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )
        SettingsItem(icon = Icons.Default.AdminPanelSettings, title = "Gestionar Usuarios") { /* Navigar */ }
        SettingsItem(icon = Icons.Default.Person, title = "Ver Estadísticas") { /* Navigar */ }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f) // Este usa el weight correcto
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF5252),
            contentColor = Color.White
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = "Cerrar Sesión"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Cerrar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

