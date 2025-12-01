package com.example.nextbyte_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.nextbyte_app.ui.screens.Productos.AddProductScreen
import com.example.nextbyte_app.ui.screens.Productos.ProductosScreen
import com.example.nextbyte_app.ui.screens.WelcomeScreen
import com.example.nextbyte_app.ui.screens.account.AccountScreen
import com.example.nextbyte_app.ui.screens.carrito.CarritoScreen
import com.example.nextbyte_app.ui.screens.home.HomeScreen
import com.example.nextbyte_app.ui.screens.login.LoginScreen
import com.example.nextbyte_app.ui.screens.register.RegisterScreen
import com.example.nextbyte_app.ui.screens.settings.*
import com.example.nextbyte_app.ui.theme.NextbyteappTheme
import com.example.nextbyte_app.viewmodels.AuthViewModel
import com.example.nextbyte_app.viewmodels.CartViewModel
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
                val cartViewModel: CartViewModel = viewModel()

                val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()
                val isLoading by authViewModel.isLoading.collectAsState()
                val currentUser by authViewModel.currentUser.collectAsState()

                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        val uid = authViewModel.getCurrentUserId()
                        if (uid.isNotEmpty()) userViewModel.loadCurrentUser(uid)
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

                            composable("welcome") { WelcomeScreen({ navController.navigate("login") }, { navController.navigate("register") }) }
                            composable("register") { RegisterScreen({ navController.popBackStack() }, { navController.navigate("home") { popUpTo("register") { inclusive = true } } }) }
                            composable("login") { LoginScreen({ navController.navigate("home") { popUpTo("login") { inclusive = true } } }, { navController.navigate("register") }) }
                            composable("home") { HomeScreen(navController, cartViewModel, authViewModel, userViewModel) }
                            composable("productos") { ProductosScreen(navController, cartViewModel) }
                            composable("carrito") { CarritoScreen(navController, cartViewModel) }
                            composable("favorites") { FavoritosScreen(navController, cartViewModel) }
                            composable("add_product") { AddProductScreen(navController) }

                            // --- Rutas de Ajustes ---
                            composable("settings") { SettingsScreen(navController = navController) }
                            composable("account_options") { UserSettingsScreen(navController = navController) }
                            composable("change_email") { PlaceholderScreen(navController = navController, "Cambiar Correo") }
                            composable("change_password") { PlaceholderScreen(navController = navController, "Cambiar Contraseña") }
                            composable("change_address") { PlaceholderScreen(navController = navController, "Cambiar Dirección") }
                            composable("past_orders") { PlaceholderScreen(navController = navController, "Pedidos Anteriores") }
                            composable("physical_stores") { PhysicalStoresScreen(navController = navController) }
                            composable("notifications") { NotificationsScreen(navController = navController) }
                            composable("help") { HelpScreen(navController = navController) }
                            composable("terms_and_conditions") { TermsAndConditionsScreen(navController = navController) }
                            composable("about_nextbyte") { AboutNextByteScreen(navController = navController) }
                            composable("logout") {
                                LaunchedEffect(Unit) {
                                    authViewModel.logout()
                                    userViewModel.clearUser()
                                    navController.navigate("welcome") { popUpTo("home") { inclusive = true } }
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
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator()
            Text("Cargando...")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(navController: NavController, text: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás") } }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
            Text("Pantalla de '$text' en construcción.")
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