package com.example.nextbyte_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.ui.screens.FavoritosScreen
import com.example.nextbyte_app.ui.screens.Productos.ProductosScreen
import com.example.nextbyte_app.ui.screens.carrito.CarritoScreen
import com.example.nextbyte_app.ui.screens.home.HomeScreen
import com.example.nextbyte_app.ui.screens.login.LoginScreen
import com.example.nextbyte_app.ui.screens.register.RegisterScreen
import com.example.nextbyte_app.ui.screens.WelcomeScreen
import com.example.nextbyte_app.ui.screens.settings.*
import com.example.nextbyte_app.ui.theme.NextbyteappTheme
import com.example.nextbyte_app.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NextbyteappTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                // Observar el estado de autenticación
                val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()
                val isLoading by authViewModel.isLoading.collectAsState()

                Surface(modifier = Modifier.fillMaxSize()) {
                    if (isLoading) {
                        // Mostrar pantalla de carga mientras verifica la sesión
                        LoadingScreen()
                    } else {
                        NavHost(
                            navController = navController,
                            startDestination = if (isUserLoggedIn) "home" else "welcome"
                        ) {
                            composable("welcome") {
                                WelcomeScreen(onNavigateToLogin = { navController.navigate("login") })
                            }

                            composable("register") {
                                RegisterScreen(
                                    onBack = { navController.popBackStack() },
                                    onRegisterSuccess = {
                                        navController.navigate("home") {
                                            popUpTo("register") { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable("login"){
                                LoginScreen(
                                    onLoginSuccess = {
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    },
                                    onNavigateToRegister = { navController.navigate("register") }
                                )
                            }

                            composable("home") {
                                HomeScreen(
                                    navController = navController,
                                    onFavoriteClick = { product ->
                                        // Lógica de favoritos
                                    }
                                )
                            }

                            composable("favorites") {
                                FavoritosScreen(
                                    navController = navController,
                                    onFavoriteClick = { product ->
                                        // Lógica de favoritos
                                    }
                                )
                            }

                            composable("productos") {
                                ProductosScreen(
                                    navController = navController,
                                    onFavoriteClick = { product ->
                                        // Lógica de favoritos
                                    }
                                )
                            }

                            composable("carrito") {
                                CarritoScreen(navController = navController)
                            }

                            // Pantallas de configuración
                            composable("settings") { SettingsScreen(navController = navController) }
                            composable("account_options") { AccountOptionsScreen(navController = navController) }
                            composable("change_email") { ChangeEmailScreen(navController = navController) }
                            composable("change_password") { ChangePasswordScreen(navController = navController) }
                            composable("my_cards") { MyCardsScreen(navController = navController) }
                            composable("addresses") { AddressesScreen(navController = navController) }
                            composable("add_edit_address") { AddEditAddressScreen(navController = navController) }
                            composable("past_orders") { PastOrdersScreen(navController = navController) }
                            composable("physical_stores") { PhysicalStoresScreen(navController = navController) }
                            composable("notifications") { NotificationsScreen(navController = navController) }
                            composable("help") { HelpScreen(navController = navController) }
                            composable("terms_and_conditions") { TermsAndConditionsScreen(navController = navController) }
                            composable("about_nextbyte") { AboutNextByteScreen(navController = navController) }

                            composable("logout") {
                                // Cerrar sesión y redirigir al login
                                authViewModel.signOut()
                                navController.navigate("welcome") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        androidx.compose.material3.CircularProgressIndicator()
    }
}

// Extension function para shared view models
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}