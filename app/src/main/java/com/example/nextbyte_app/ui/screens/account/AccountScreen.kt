package com.example.nextbyte_app.ui.screens.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.nextbyte_app.data.User
import com.example.nextbyte_app.data.UserRole
import com.example.nextbyte_app.viewmodels.AuthViewModel
import com.example.nextbyte_app.viewmodels.UserViewModel

// --- Pantalla Principal de Cuenta (Distribuidor) ---
@Composable
fun AccountScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()

    // Carga el usuario si no está ya cargado
    LaunchedEffect(Unit) {
        if (currentUser == null) {
            val userId = authViewModel.getCurrentUserId()
            if (userId.isNotEmpty()) {
                userViewModel.loadCurrentUser(userId)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading && currentUser == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (currentUser != null) {
            // 1. Cabecera del perfil (común para todos)
            ProfileHeader(user = currentUser!!)
            Spacer(modifier = Modifier.height(24.dp))

            // 2. Panel de control DINÁMICO según el rol
            if (currentUser!!.role == UserRole.ADMIN || currentUser!!.role == UserRole.MANAGER) {
                AdminDashboard(navController = navController)
            } else {
                UserDashboard(navController = navController)
            }

            Spacer(modifier = Modifier.weight(1f))

            // 3. Botón de cerrar sesión (común para todos)
            LogoutButton {
                authViewModel.logout()
                userViewModel.clearUser()
            }
        } else {
            // Estado de error o sin usuario logueado
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se pudo cargar la información del usuario.")
            }
        }
    }
}

// --- Paneles Específicos por Rol ---

@Composable
fun UserDashboard(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Mi Cuenta", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
        SettingsCard {
            SettingsItem(icon = Icons.Default.Email, title = "Cambiar correo") { navController.navigate("change_email") }
            Divider()
            SettingsItem(icon = Icons.Default.Lock, title = "Cambiar contraseña") { navController.navigate("change_password") }
            Divider()
            SettingsItem(icon = Icons.Default.LocationOn, title = "Gestionar direcciones") { navController.navigate("change_address") }
        }
    }
}

@Composable
fun AdminDashboard(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Panel de Administrador", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
        SettingsCard {
            SettingsItem(icon = Icons.Default.Category, title = "Gestionar Productos") { navController.navigate("productos") }
            Divider()
            SettingsItem(icon = Icons.Default.Group, title = "Gestionar Usuarios") { navController.navigate("admin_panel") }
            Divider()
            SettingsItem(icon = Icons.Default.BarChart, title = "Estadísticas de Ventas") { /* navController.navigate("statistics") */ }
        }
    }
}


// --- Componentes Reutilizables ---

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
fun ProfileHeader(user: User) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = if(user.role == UserRole.ADMIN) R.drawable.img else R.drawable.perfil_image), // Example of role-based image
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = user.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = user.email, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp) 
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = Color.White)
    ) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
        Spacer(modifier = Modifier.width(8.dp))
        Text("Cerrar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}