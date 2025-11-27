package com.example.nextbyte_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.data.allProducts
import com.example.nextbyte_app.ui.screens.FavoritosScreen
import com.example.nextbyte_app.ui.screens.ProductDetailScreen
import com.example.nextbyte_app.ui.screens.account.AccountScreen
import com.example.nextbyte_app.ui.screens.account.EditProfileScreen
import com.example.nextbyte_app.ui.screens.account.UserViewModel
import com.example.nextbyte_app.ui.screens.home.HomeScreen
import com.example.nextbyte_app.ui.screens.login.LoginScreen
import com.example.nextbyte_app.ui.screens.register.RegisterScreen
import com.example.nextbyte_app.ui.screens.WelcomeScreen
import com.example.nextbyte_app.ui.screens.settings.*
import com.example.nextbyte_app.ui.theme.NextbyteappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NextbyteappTheme {
                val navController = rememberNavController()
                var products by remember { mutableStateOf(allProducts) }
                // 1. CREAR UNA ÚNICA INSTANCIA DEL VIEWMODEL AQUÍ
                val userViewModel: UserViewModel = viewModel()

                val onFavoriteClick: (Product) -> Unit = { product ->
                    val index = products.indexOfFirst { it.id == product.id }
                    if (index != -1) {
                        products = products.toMutableList().also {
                            it[index] = it[index].copy(isFavorited = !it[index].isFavorited)
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = "welcome"
                    ) {
                        composable("welcome") { WelcomeScreen(onNavigateToLogin = { navController.navigate("login") }) }
                        composable("register") { RegisterScreen(onBack = { navController.popBackStack() }) }
                        composable("login"){
                            // 2. PASAR LA MISMA INSTANCIA A LOGINSCREEN
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = { navController.navigate("register") },
                                userViewModel = userViewModel
                            )
                        }
                        composable("home") { HomeScreen(navController, products, onFavoriteClick, userViewModel) } // CORREGIDO
                        composable("favorites") { FavoritosScreen(navController = navController, products = products, onFavoriteClick = onFavoriteClick) }
                        composable("settings") { SettingsScreen(navController = navController) }
                        // 3. PASAR LA MISMA INSTANCIA A ACCOUNTSCREEN
                        composable("account") { AccountScreen(navController = navController, userViewModel = userViewModel) }
                        composable("edit_profile") { EditProfileScreen(navController = navController, userViewModel = userViewModel) }
                        composable("account_options") { AccountOptionsScreen(navController = navController) }
                        composable("change_email") { ChangeEmailScreen(navController = navController) }
                        composable("change_password") { ChangePasswordScreen(navController = navController) }
                        composable("my_cards") { MyCardsScreen(navController = navController) }

                        navigation(startDestination = "addresses", route = "address_flow") {
                            composable("addresses") { AddressesScreen(navController = navController) }
                            composable("add_edit_address") { backStackEntry ->
                                val addressViewModel = backStackEntry.sharedViewModel<AddressViewModel>(navController)
                                AddEditAddressScreen(navController, addressViewModel)
                            }
                            composable("view_address") { backStackEntry ->
                                val addressViewModel = backStackEntry.sharedViewModel<AddressViewModel>(navController)
                                ViewAddressScreen(navController, addressViewModel)
                            }
                            composable("delete_address") { backStackEntry ->
                                val addressViewModel = backStackEntry.sharedViewModel<AddressViewModel>(navController)
                                DeleteAddressScreen(navController, addressViewModel)
                            }
                        }

                        composable("past_orders") { PastOrdersScreen(navController = navController) }
                        composable("physical_stores") { PhysicalStoresScreen(navController = navController) }
                        composable("notifications") { NotificationsScreen(navController = navController) }
                        composable("faq") { FaqScreen(navController = navController) }
                        composable("terms_and_conditions") { TermsAndConditionsScreen(navController = navController) }
                        composable("about_nextbyte") { AboutNextByteScreen(navController = navController) }
                        composable("logout") {
                            userViewModel.logout()
                            navController.navigate("welcome") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                        composable("product"){ Text(text = "Pantalla de productos de NextByte") }
                        composable(
                            route = "product_detail_route/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getInt("productId")
                            if (productId != null) {
                                ProductDetailScreen(productId = productId)
                            } else {
                                Text(text = "Error: Producto no encontrado.")
                            }
                        }
                    }
                }
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