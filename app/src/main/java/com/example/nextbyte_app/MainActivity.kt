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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nextbyte_app.ui.screens.FavoritosScreen
import com.example.nextbyte_app.ui.screens.Productos.AddProductScreen
import com.example.nextbyte_app.ui.screens.Productos.ProductosScreen
import com.example.nextbyte_app.ui.screens.WelcomeScreen
import com.example.nextbyte_app.ui.screens.account.ChangePasswordScreen
import com.example.nextbyte_app.ui.screens.account.ChangePhoneNumberScreen
import com.example.nextbyte_app.ui.screens.account.ManageAddressScreen
import com.example.nextbyte_app.ui.screens.admin.AdminPanelScreen
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

                val authState by authViewModel.currentUser.collectAsState()
                // Usamos el isLoading del AuthViewModel para el estado de carga general de la app
                val isLoading by authViewModel.isLoading.collectAsState()

                // << LA LÓGICA DE GESTIÓN DE SESIÓN DEFINITIVA ESTÁ AQUÍ >>
                LaunchedEffect(authState) {
                    val firebaseUser = authState
                    if (firebaseUser != null) {
                        // Si hay un usuario y no es el que ya tenemos cargado, recargamos
                        if (userViewModel.currentUser.value?.uid != firebaseUser.uid) {
                            userViewModel.clearUser() // Limpiamos CUALQUIER dato de un usuario anterior
                            userViewModel.loadCurrentUser(firebaseUser.uid)
                        }
                    } else {
                        // Si no hay usuario (logout), limpiamos todo y vamos a la bienvenida
                        userViewModel.clearUser()
                        // cartViewModel.clearCart() // Buena práctica: limpiar carrito también
                        navController.navigate("welcome") {
                            // Limpiamos todo el historial de navegación para que el usuario no pueda volver atrás
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    // La pantalla de carga se muestra solo mientras se resuelve la autenticación inicial
                    val showLoadingScreen = isLoading && authState == null
                    
                    if (showLoadingScreen) {
                        LoadingScreen()
                    } else {
                        val startDestination = if (authState != null) "home" else "welcome"
                        NavHost(
                            navController = navController,
                            startDestination = startDestination
                        ) {

                            composable("welcome") { WelcomeScreen({ navController.navigate("login") }, { navController.navigate("register") }) }
                            composable("register") { RegisterScreen({ navController.popBackStack() }, { navController.navigate("home") { popUpTo("register") { inclusive = true } } }) }
                            composable("login") { LoginScreen({ navController.navigate("home") { popUpTo("login") { inclusive = true } } }, { navController.navigate("register") }) }
                            composable("home") { HomeScreen(navController, cartViewModel, authViewModel, userViewModel) }
                            composable("productos") { ProductosScreen(navController, cartViewModel) }
                            composable("carrito") { CarritoScreen(navController, cartViewModel) }
                            composable("favorites") { FavoritosScreen(navController, cartViewModel) }
                            composable("add_product") { AddProductScreen(navController) }

                            // --- Rutas de Ajustes y Cuenta ---
                            composable("settings") { SettingsScreen(navController = navController) }
                            composable("change_phone") { ChangePhoneNumberScreen(navController = navController, userViewModel = userViewModel) }
                            composable("change_password") { ChangePasswordScreen(navController = navController, authViewModel = authViewModel) }
                            composable("change_address") { ManageAddressScreen(navController = navController, userViewModel = userViewModel) }
                            composable("past_orders") { PlaceholderScreen(navController = navController, "Pedidos Anteriores") }
                            composable("physical_stores") { PhysicalStoresScreen(navController = navController) }
                            composable("notifications") { NotificationsScreen(navController = navController) }
                            composable("help") { HelpScreen(navController = navController) }
                            composable("terms_and_conditions") { TermsAndConditionsScreen(navController = navController) }
                            composable("about_nextbyte") { AboutNextByteScreen(navController = navController) }
                            composable("admin_panel") { AdminPanelScreen(navController = navController) }
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
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(navController: NavController, title: String) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(title) }, navigationIcon = { 
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                }
            })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
            Text("Pantalla en construcción")
        }
    }
}