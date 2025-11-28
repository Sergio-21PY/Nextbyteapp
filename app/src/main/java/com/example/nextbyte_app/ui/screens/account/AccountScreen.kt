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
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextbyte_app.R
import com.example.nextbyte_app.data.User
import com.example.nextbyte_app.data.UserRole
import com.example.nextbyte_app.viewmodels.AuthViewModel
import com.example.nextbyte_app.viewmodels.UserViewModel

@Composable
fun AccountScreen(
    navController: NavController
) {
    val userViewModel: UserViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    // CORREGIDO: Usar .value en lugar de delegación con 'by'
    val currentUserState = userViewModel.currentUser.collectAsState()
    val isLoadingState = userViewModel.isLoading.collectAsState()

    val currentUser = currentUserState.value
    val isLoading = isLoadingState.value

    // Cargar usuario si no está cargado
    LaunchedEffect(Unit) {
        val userId = authViewModel.getCurrentUserId()
        if (userId.isNotEmpty() && currentUser == null) {
            userViewModel.loadCurrentUser(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Cargando información...")
            }
        } else if (currentUser != null) {
            // Usuario logueado - mostrar información real
            ProfileHeader(user = currentUser)
            Spacer(modifier = Modifier.height(32.dp))
            SettingsSection(
                user = currentUser,
                navController = navController
            )
            Spacer(modifier = Modifier.weight(1f))
            LogoutButton(
                onClick = {
                    authViewModel.signOut()
                    userViewModel.clearUser()
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        } else {
            // No hay usuario logueado o error
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "No se pudo cargar la información del usuario",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Button(
                        onClick = {
                            val userId = authViewModel.getCurrentUserId()
                            if (userId.isNotEmpty()) {
                                userViewModel.loadCurrentUser(userId)
                            }
                        }
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(user: User) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Determinar imagen de perfil basada en el rol
        val profileImageRes = when {
            user.role == UserRole.ADMIN -> R.drawable.img
            user.role == UserRole.MANAGER -> R.drawable.img
            else -> R.drawable.perfil_image
        }

        Image(
            painter = painterResource(id = profileImageRes),
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(
                    if (user.role == UserRole.ADMIN || user.role == UserRole.MANAGER)
                        RoundedCornerShape(16.dp)
                    else
                        CircleShape
                )
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
        Spacer(modifier = Modifier.height(8.dp))
        // Mostrar rol del usuario - CORREGIDO: Usar función helper
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    when (user.role) {
                        UserRole.ADMIN -> Color(0xFFFF5252)
                        UserRole.MANAGER -> Color(0xFFFF9800)
                        UserRole.CUSTOMER -> MaterialTheme.colorScheme.primary
                        UserRole.GUEST -> MaterialTheme.colorScheme.secondary
                    }
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = getRoleDisplayName(user.role), // Función helper
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SettingsSection(
    user: User,
    navController: NavController
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            SettingsItem(
                icon = Icons.Default.Edit,
                title = "Editar Perfil"
            ) {
                navController.navigate("account_options")
            }

            SettingsItem(
                icon = Icons.Default.Settings,
                title = "Configuración"
            ) {
                navController.navigate("settings")
            }

            SettingsItem(
                icon = Icons.Default.CreditCard,
                title = "Métodos de Pago"
            ) {
                navController.navigate("my_cards")
            }

            SettingsItem(
                icon = Icons.Default.Security,
                title = "Seguridad"
            ) {
                navController.navigate("change_password")
            }

            SettingsItem(
                icon = Icons.Default.HelpOutline,
                title = "Ayuda y Soporte"
            ) {
                navController.navigate("help")
            }

            // Mostrar opciones de administrador si corresponde - CORREGIDO
            if (canViewAdminPanel(user)) {
                AdminSettings(navController = navController)
            }
        }
    }
}

@Composable
fun AdminSettings(navController: NavController) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Panel de Administración",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )
        SettingsItem(
            icon = Icons.Default.AdminPanelSettings,
            title = "Gestionar Usuarios"
        ) {
            navController.navigate("admin_panel")
        }
        SettingsItem(
            icon = Icons.Default.Person,
            title = "Estadísticas"
        ) {
            // navController.navigate("statistics") // Puedes crear esta pantalla después
        }
        SettingsItem(
            icon = Icons.Default.Edit,
            title = "Gestionar Productos"
        ) {
            navController.navigate("productos")
        }
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
            modifier = Modifier.weight(1f)
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

// Funciones helper para AccountScreen
private fun getRoleDisplayName(role: UserRole): String {
    return when (role) {
        UserRole.ADMIN -> "Administrador"
        UserRole.MANAGER -> "Gestor"
        UserRole.CUSTOMER -> "Cliente"
        UserRole.GUEST -> "Invitado"
    }
}

private fun canViewAdminPanel(user: User): Boolean {
    return user.role == UserRole.ADMIN || user.role == UserRole.MANAGER
}