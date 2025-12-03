package com.example.nextbyte_app.ui.screens.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nextbyte_app.R
import com.example.nextbyte_app.data.User
import com.example.nextbyte_app.data.UserRole
import com.example.nextbyte_app.ui.screens.settings.SettingItem
import com.example.nextbyte_app.ui.screens.settings.SettingListItem
import com.example.nextbyte_app.viewmodels.AuthViewModel
import com.example.nextbyte_app.viewmodels.UserViewModel

@Composable
fun AccountScreen(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    onNavigate: (String) -> Unit
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()

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
            ProfileHeader(user = currentUser!!)
            Spacer(modifier = Modifier.height(24.dp))

            if (currentUser!!.role == UserRole.ADMIN || currentUser!!.role == UserRole.MANAGER) {
                AdminDashboard(onNavigate = onNavigate)
            } else {
                UserDashboard(onNavigate = onNavigate)
            }

            Spacer(modifier = Modifier.weight(1f))

            // LA CORRECCIÓN ESTÁ AQUÍ:
            LogoutButton {
                authViewModel.logout() // Solo iniciamos el logout
                // La navegación y la limpieza de datos se manejan centralmente
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se pudo cargar la información del usuario.")
            }
        }
    }
}

@Composable
fun UserDashboard(onNavigate: (String) -> Unit) {
    val userAccountItems = listOf(
        SettingItem("Cambiar número de teléfono", Icons.Default.Phone, "change_phone"),
        SettingItem("Cambiar contraseña", Icons.Default.Lock, "change_password"),
        SettingItem("Gestionar direcciones", Icons.Default.LocationOn, "change_address")
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Mi Cuenta", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
        SettingsCard {
            userAccountItems.forEachIndexed { index, item ->
                SettingListItem(item = item, onClick = { onNavigate(item.route) })
                if (index < userAccountItems.lastIndex) {
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
fun AdminDashboard(onNavigate: (String) -> Unit) {
    val adminItems = listOf(
        SettingItem("Gestionar Productos", Icons.Default.Category, "productos"),
        SettingItem("Gestionar Usuarios", Icons.Default.Group, "admin_panel"),
        SettingItem("Estadísticas de Ventas", Icons.Default.BarChart, "statistics")
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Panel de Administrador", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
        SettingsCard {
            adminItems.forEachIndexed { index, item ->
                SettingListItem(item = item, onClick = { onNavigate(item.route) })
                if (index < adminItems.lastIndex) {
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

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
            painter = painterResource(id = if(user.role == UserRole.ADMIN) R.drawable.img else R.drawable.perfil_image),
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = user.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        // Mostramos el teléfono si está disponible
        Text(text = user.phone.ifEmpty { user.email }, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
    }
}
