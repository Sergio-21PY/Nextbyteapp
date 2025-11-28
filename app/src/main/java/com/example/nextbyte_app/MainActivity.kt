package com.example.nextbyte_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nextbyte_app.ui.screens.FavoritosScreen
import com.example.nextbyte_app.ui.screens.Productos.ProductosScreen
import com.example.nextbyte_app.ui.screens.Productos.AddProductScreen
import com.example.nextbyte_app.ui.screens.carrito.CarritoScreen
import com.example.nextbyte_app.ui.screens.home.HomeScreen
import com.example.nextbyte_app.ui.screens.login.LoginScreen
import com.example.nextbyte_app.ui.screens.register.RegisterScreen
import com.example.nextbyte_app.ui.screens.WelcomeScreen
import com.example.nextbyte_app.ui.screens.settings.*
import com.example.nextbyte_app.ui.screens.admin.AdminPanelScreen
import com.example.nextbyte_app.ui.theme.NextbyteappTheme
import com.example.nextbyte_app.viewmodels.AuthViewModel
import com.example.nextbyte_app.viewmodels.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NextbyteappTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val userViewModel: UserViewModel = viewModel()

                // Observar el estado de autenticación
                val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()
                val isLoading by authViewModel.isLoading.collectAsState()
                val currentUser by authViewModel.currentUser.collectAsState()

                // Cargar información del usuario cuando se loguee
                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        val currentUserId = authViewModel.getCurrentUserId()
                        if (currentUserId.isNotEmpty()) {
                            userViewModel.loadCurrentUser(currentUserId)
                        }
                    } else {
                        userViewModel.clearUser()
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    if (isLoading) {
                        LoadingScreen()
                    } else {
                        NavHost(
                            navController = navController,
                            startDestination = if (isUserLoggedIn) "home" else "welcome"
                        ) {
                            // Pantallas de autenticación
                            composable("welcome") {
                                WelcomeScreen(
                                    onNavigateToLogin = { navController.navigate("login") },
                                    onNavigateToRegister = { navController.navigate("register") }
                                )
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

                            composable("login") {
                                LoginScreen(
                                    onLoginSuccess = {
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    },
                                    onNavigateToRegister = { navController.navigate("register") }
                                )
                            }

                            // Pantallas principales
                            composable("home") {
                                HomeScreen(
                                    navController = navController
                                )
                            }

                            composable("favorites") {
                                FavoritosScreen(
                                    navController = navController
                                )
                            }

                            composable("productos") {
                                ProductosScreen(
                                    navController = navController
                                )
                            }

                            composable("add_product") {
                                AddProductScreen(
                                    navController = navController
                                )
                            }

                            composable("carrito") {
                                CarritoScreen(
                                    navController = navController
                                )
                            }

                            // Pantallas de administración
                            composable("admin_panel") {
                                AdminPanelScreen(
                                    navController = navController
                                )
                            }

                            // Pantallas de configuración
                            composable("settings") {
                                SettingsScreen(
                                    navController = navController
                                )
                            }

                            composable("account_options") {
                                AccountOptionsScreen(
                                    navController = navController
                                )
                            }

                            composable("change_email") {
                                ChangeEmailScreen(
                                    navController = navController
                                )
                            }

                            composable("change_password") {
                                ChangePasswordScreen(
                                    navController = navController
                                )
                            }

                            composable("my_cards") {
                                MyCardsScreen(
                                    navController = navController
                                )
                            }

                            composable("addresses") {
                                AddressesScreen(
                                    navController = navController
                                )
                            }

                            composable("add_edit_address") {
                                AddEditAddressScreen(
                                    navController = navController
                                )
                            }

                            composable("past_orders") {
                                PastOrdersScreen(
                                    navController = navController
                                )
                            }

                            composable("physical_stores") {
                                PhysicalStoresScreen(navController = navController)
                            }

                            composable("notifications") {
                                NotificationsScreen(
                                    navController = navController
                                )
                            }

                            composable("help") {
                                HelpScreen(navController = navController)
                            }

                            composable("terms_and_conditions") {
                                TermsAndConditionsScreen(navController = navController)
                            }

                            composable("about_nextbyte") {
                                AboutNextByteScreen(navController = navController)
                            }

                            composable("logout") {
                                LaunchedEffect(Unit) {
                                    authViewModel.signOut()
                                    userViewModel.clearUser()
                                    navController.navigate("welcome") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }

                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        CircularProgressIndicator()
                                        Text("Cerrando sesión...")
                                    }
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator()
                Text("Cargando...")
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}