package com.example.nextbyte_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.nextbyte_app.viewmodels.ProductViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NextbyteappTheme {
                val navController = rememberNavController()

                // Lista temporal de productos - será reemplazada por Firebase
                var products by remember { mutableStateOf(emptyList<Product>()) }

                val onFavoriteClick: (Product) -> Unit = { product ->
                    // Esta función se actualizará cuando tengas Firebase
                    val updatedProducts = products.map {
                        if (it.id == product.id) {
                            it.copy() // Aquí actualizarás el estado de favorito en Firebase
                        } else {
                            it
                        }
                    }
                    products = updatedProducts
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = "welcome"
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
                                onFavoriteClick = onFavoriteClick
                            )
                        }

                        composable("favorites") {
                            FavoritosScreen(
                                navController = navController,
                                onFavoriteClick = onFavoriteClick
                            )
                        }

                        composable("productos") {
                            ProductosScreen(
                                navController = navController,
                                onFavoriteClick = onFavoriteClick
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

                        // Navegación de direcciones (simplificada por ahora)
                        composable("addresses") { AddressesScreen(navController = navController) }
                        composable("add_edit_address") { AddEditAddressScreen(navController = navController) }

                        composable("past_orders") { PastOrdersScreen(navController = navController) }
                        composable("physical_stores") { PhysicalStoresScreen(navController = navController) }
                        composable("notifications") { NotificationsScreen(navController = navController) }
                        composable("help") { HelpScreen(navController = navController) }
                        composable("terms_and_conditions") { TermsAndConditionsScreen(navController = navController) }
                        composable("about_nextbyte") { AboutNextByteScreen(navController = navController) }

                        composable("logout") {
                            // Aquí agregarás lógica de logout de Firebase después
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Extension function para shared view models (si la necesitas)
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}