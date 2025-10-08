package com.example.nextbyte_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nextbyte_app.ui.screens.RegisterScreen
import com.example.nextbyte_app.ui.screens.WelcomeScreen // ← Solo este import
import com.example.nextbyte_app.ui.theme.NextbyteappTheme
import com.example.nextbyte_app.ui.screens.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NextbyteappTheme {
                val navController = rememberNavController()

                Surface(modifier = Modifier.fillMaxSize()) { // ← Agregar fillMaxSize()
                    NavHost(
                        navController = navController,
                        startDestination = "welcome"
                    ) {
                        composable("welcome") {
                            WelcomeScreen(
                                onNavigateToLogin = { // ← Cambiar a onNavigateToLogin
                                    navController.navigate("login") // ← Navegar a login
                                }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // AGREGAR la pantalla de Login
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    // Aquí va la navegación después del login exitoso
                                    navController.navigate("home") {
                                        // Opcional: Limpiar el back stack para que no pueda volver al login
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}




