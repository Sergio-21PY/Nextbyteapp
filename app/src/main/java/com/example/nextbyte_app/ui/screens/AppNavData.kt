import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination

/*Creamos una sealed class (clase sellada) para poder guardar la informacion sobre nuestra barra menu,
* --------------------------------------------------------------*/
sealed class AppDestinations(val route: String, val icon: ImageVector, val label: String) {

    /*Utulizamos el data object para que cada instancia sea unica.*/
    data object Home : AppDestinations("home_route", Icons.Default.Home, "Home")
    data object Search : AppDestinations("search_route", Icons.Default.Search, "Search")
    data object Categories : AppDestinations("categories_route", Icons.Default.Category, "Categories")
    data object Cart : AppDestinations("cart_route", Icons.Default.ShoppingCart, "Cart")
    data object Account : AppDestinations("account_route", Icons.Default.AccountCircle, "Account")
}


// El Composable de la Barra
@Composable //Estructura para la ventana.

/*Parametros de navagacion mediante NavController*/
fun ECommerceBottomBar(navController: NavController) {
    /*La lista de items la tomamos de nuestra clase sellada donde se encuentran los data objects,
    * Home,Searh,Categories,Cart y Account */
    val items = listOf(AppDestinations.Home, AppDestinations.Search, AppDestinations.Categories, AppDestinations.Cart, AppDestinations.Account)
    /*mediante el compose el nacBackStackEntry le dice que pantalla esta visualizando cada vez que se
    * cambia de pantalla con el navController.*/
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    /*CurrentRoute contiene el el identificador unico de cada pestaña, ejemplo "home_route"*/
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedColor = Color(0xFF8A2BE2) // Color Azul

    NavigationBar(containerColor = selectedColor) {
        items.forEach { destination ->
            val isSelected = currentRoute == destination.route

            NavigationBarItem(
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label, color = if (isSelected) Color.White else Color(0xFFBBDEFB)) },
                selected = isSelected,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
            )
        }
    }
}