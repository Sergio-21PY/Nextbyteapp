package com.example.nextbyte_app.ui.screens

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.material.icons.filled.PhotoCamera

/*Creamos una sealed class (clase sellada) para poder guardar la informacion sobre nuestra barra menu,
* --------------------------------------------------------------*/
sealed class AppDestinations(val route: String, val icon: ImageVector, val label: String) {

    /*Utulizamos el data object para que cada instancia sea unica.*/
    data object Home : AppDestinations("home_route", Icons.Default.Home, "Home")
    data object Productos : AppDestinations("productos_route", Icons.Default.Category, "Productos")
    data object Camera : AppDestinations("camera_route", Icons.Default.PhotoCamera, "Cámara")
    data object Cart : AppDestinations("cart_route", Icons.Default.ShoppingCart, "Cart")
    data object Account : AppDestinations("account_route", Icons.Default.AccountCircle, "Account")
}


// El Composable de la Barra
@Composable //Estructura para la ventana.

        /*Parametros de navagacion mediante NavController*/
fun ECommerceBottomBar(navController: NavController) {
    /*La lista de items la tomamos de nuestra clase sellada donde se encuentran los data objects,
    * Home,Searh,Productos,Cart y Account */
    val items = listOf(AppDestinations.Home, AppDestinations.Productos, AppDestinations.Camera,AppDestinations.Cart, AppDestinations.Account)
    /*mediante el compose el nacBackStackEntry le dice que pantalla esta visualizando cada vez que se
    * cambia de pantalla con el navController.*/
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    /*CurrentRoute contiene el el identificador unico de cada pestaña, ejemplo "home_route"*/
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarColor = Color(0xFF8A2BE2) // Púrpura/Azul original para la barra
    val cameraFABColor = Color(0xFFFF4081) // Color "FAB" para el botón central (Magenta)
    val unselectedIconColor = Color(0xFFBBDEFB)

    // *** LÓGICA DE LANZAMIENTO DE CÁMARA ***

    //Launcher para abrir la cámara (usando TakePicturePreview para simplicidad)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        // Manejar el resultado de la foto aquí (bitmap contendrá la previsualización)
        if (bitmap != null) {
            println("Cámara: ¡Foto tomada! Bitmap size: ${bitmap.width}x${bitmap.height}")
        }
    }

    // 2. Launcher para solicitar el permiso de CÁMARA
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Si el permiso es otorgado, lanzamos la cámara
            cameraLauncher.launch(null)
        } else {
            // Manejar caso de permiso denegado
            println("Permiso de cámara denegado.")
        }
    }

    // Acción que se ejecuta al presionar el botón de la cámara: pide permisos y lanza la app.
    val launchCameraAction: () -> Unit = {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }
    /*-------------------------------------------------------------------------------*/

    NavigationBar(containerColor = bottomBarColor) {
        items.forEach { destination ->
            val isSelected = currentRoute == destination.route
            val isCamera = destination == AppDestinations.Camera

            val iconTint = if (isCamera) Color.White else if (isSelected) Color.White else unselectedIconColor

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (isCamera) {
                        // Si es el botón de la cámara, llamamos a la acción de la cámara
                        launchCameraAction()
                    } else {
                        // Si es cualquier otro botón, navegamos
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },

                // *** DISEÑO CAMARA ***
                icon = {
                    if (isCamera) {
                        Box(
                            // Modificaciones del icono camara.
                            modifier = Modifier
                                .size(56.dp) // Tamaño grande
                                .clip(CircleShape) // Forma redonda
                                .background(cameraFABColor), //
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                                modifier = Modifier.size(30.dp),
                                tint = iconTint
                            )
                        }
                    } else {
                        // Íconos normales
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = destination.label,
                            tint = iconTint
                        )
                    }
                },
                /*----------------------------------------------------------------*/

                //Ocultamos la etiqueta si es la cámara
                label = {
                    if (!isCamera) {
                        Text(destination.label, color = if (isSelected) Color.White else unselectedIconColor)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                   //Cuando pinchamos una opcion se ve transparante
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = unselectedIconColor
                )
            )
        }
    }
}