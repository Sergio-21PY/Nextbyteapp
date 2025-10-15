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
                Surface(modifier = Modifier.fillMaxSize()) { // ← Agregar fillMaxSize()

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
                                onNavigateToLogin = { // ← Cambiar a onNavigateToLogin
                                    navController.navigate("login") // ← Navegar a login
                                }
                            )
                        }

                        /*¿Que tipo de eventos puedo crear?
                        1) onNavigateToLogin: Navega al tocar el boton de login
                        * Ejemplo: Button(onClick = onNavigateToLogin)
                        * --------------------------------------------------
                        2) onSkip: Salta una pantalla al tocar el boton
                         Ejemplo: Button(onClick = onSkip)
                         ---------------------------------------------------
                         ---------------------------------------------------
                         ¿Que tipos de nombre puedo colocar?
                         1) on + [Verbo]: Para acciones del usuario que no involucran navegacion
                         Ejemplo: onButtonClick, onTextChange, onDimiss.
                         ---------------------------------------------------
                         2) onNavigateTo + [Pantalla]: Para acciones del usuario de ir a otra pantalla.
                         Ejemplo: onNavigateToLogin
                         ---------------------------------------------------
                         3) onAction + [Resultado]: Para indicar resultados de una accion
                         Ejemplo: onLoginSuccess, onLogoutConfirmed.*/

                        composable("register") {
                            RegisterScreen(
                                onBack = {
                                    //volver a la pantalla inmediatamente anterior
                                    navController.popBackStack()
                                }
                            )
                        }

                        //Parametros para la pantalla de login
                        composable("login"){
                            LoginScreen(
                                //Navega a home y limpia el historial mediante el popUpTo("login").
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        // Limpia el back stack para que no pueda volver al login
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                // 2. Conexión de REGISTRO: Navega a la pantalla de registro.
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        // 4. Pantalla HOME (Definida para que el Login funcione)
                        composable("home") {

                            HomeScreen()
                        }

                        composable ("product"){
                            Text(text = "Pantalla de productos de NextByte")
                        }
                    }
                }
            }
        }
    }
}