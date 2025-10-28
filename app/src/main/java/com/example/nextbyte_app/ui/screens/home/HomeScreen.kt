import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nextbyte_app.ui.screens.Productos.ProductosScreen
import com.example.nextbyte_app.ui.screens.account.AccountScreen
import com.example.nextbyte_app.ui.screens.carrito.CarritoScreen
import com.example.nextbyte_app.ui.screens.home.content.HomeCarousel
import com.example.nextbyte_app.ui.shared.MainTopBar

@Composable
fun HomeScreen(navController: NavController) {

    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when (currentRoute) {
        AppDestinations.Home.route -> "Inicio"
        AppDestinations.Productos.route -> "Productos"
        AppDestinations.Cart.route -> "Carrito"
        AppDestinations.Account.route -> "Mi Cuenta"
        else -> "Next-Byte"
    }

    Scaffold(
        topBar = {
            MainTopBar(
                title = title,
                onSettingsClick = { navController.navigate("settings") } // Navega a la nueva pantalla
            )
        },
        bottomBar = {
            ECommerceBottomBar(navController = bottomNavController)
        }
    ) { paddingValues ->

        NavHost(
            navController = bottomNavController,
            startDestination = AppDestinations.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(AppDestinations.Home.route) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Text(
                            text = "🔥 OFERTAS IMPERDIBLES",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }
                    item {
                        HomeCarousel(navController = navController)
                    }
                }
            }

            composable(AppDestinations.Productos.route) {
                ProductosScreen(navController = navController)
            }

            composable(AppDestinations.Cart.route) {
                CarritoScreen(navController = navController)
            }

            composable(AppDestinations.Account.route) {
                AccountScreen(navController = bottomNavController)
            }
        }
    }
}