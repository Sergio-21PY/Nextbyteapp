package com.example.nextbyte_app

import HomeScreen
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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nextbyte_app.ui.screens.ProductDetailScreen
import com.example.nextbyte_app.ui.screens.login.LoginScreen
import com.example.nextbyte_app.ui.screens.register.RegisterScreen
import com.example.nextbyte_app.ui.screens.WelcomeScreen
import com.example.nextbyte_app.ui.theme.NextbyteappTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NextbyteappTheme {
                /*El navController es el encargado de controlar la navegacion entre las diferentes pantallas,
                * se llama mediante una variable
                * La funcion principal es exponer la funcion: navigate() para entregarle el parametro hacia
                * donde tiene que ir, ejemplo: navController.navigate("ruta")*/

                val navController = rememberNavController() /*<----- Variable que controla los escenarios.*/
                /*-------------------------------------------------------------------------------------*/
                Surface(modifier = Modifier.fillMaxSize()) {

                    /*NavHost vendria siendo el escenario, es donde estaran ubicadas las diferentes
                    * pantallas de la aplicacion*/
                    NavHost(
                        /*Mediante esta variable le decimos quien es el director de las pantallas,
                        * en este caso es navController que definimos en la primera variable.*/
                        navController = navController,
                        /*------------------------------------------------------------------*/

                        /*Mediante la variable startDestination, le decimos cual es el escenario o
                        * la primera pantalla que debe mostrar al principio de la aplicacion.*/
                        startDestination = "welcome"
                    ) {

                        /*Composable esta encargado de mapear la ruta de los escenarios*/
                        composable("welcome") {
                            WelcomeScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login")
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

                        composable("login"){
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(navController) // <-- Le pasas el navController a tu pantalla principal
                        }

                        composable ("product"){
                            Text(text = "Pantalla de productos de NextByte")
                        }

                        // Ruta hacia los productos "Para ver los detalles." ---
                        composable(
                            route = "product_detail_route/{productId}",
                            arguments = listOf(
                                navArgument("productId") {
                                    type = NavType.IntType
                                }
                            )
                        ) { backStackEntry ->
                            // Extrae el ID del producto de los argumentos de navegación
                            val productId = backStackEntry.arguments?.getInt("productId")
                            if (productId != null) {
                                // Llama a la pantalla de detalle, pasándole el ID
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