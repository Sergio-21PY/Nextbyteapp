package com.example.nextbyte_app.ui.screens.account

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// CORRECCIÓN DEFINITIVA:
// La función ahora OBLIGATORIAMENTE recibe el 'userViewModel' como parámetro.
// Se elimina toda la lógica que lo creaba localmente, que era la causa de los errores.
@Composable
fun AccountScreen(navController: NavController, userViewModel: UserViewModel) {
    val userState by userViewModel.user.collectAsState()
    val user = userState

    if (user != null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                if (user.isAdmin) {
                    AdminAccountScreen(navController = navController, user = user)
                } else {
                    UserAccountScreen(navController = navController, user = user)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LogoutButton {
                userViewModel.logout()
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No has iniciado sesión.")
        }
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
