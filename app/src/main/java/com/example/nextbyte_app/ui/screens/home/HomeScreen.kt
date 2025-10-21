import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.navigation.NavController
import com.example.nextbyte_app.ui.screens.home.content.HomeCarousel
import com.example.nextbyte_app.ui.screens.Productos.ProductosScreen
import com.example.nextbyte_app.ui.screens.carrito.CarritoScreen



@Composable
fun HomeScreen(navController: NavController) {

    //NavController DEDICADO para la navegación de la barra inferior
    val bottomNavController = rememberNavController()

    Scaffold(
        //barra en el esqueleto
        bottomBar = {
            // Ecommerce -> Clase AppNavData
            ECommerceBottomBar(navController = bottomNavController)
        }
    ) { paddingValues ->

        // 3. NavHost El contenido que cambia
        NavHost(
            navController = bottomNavController,
            startDestination = AppDestinations.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            //Definición de las 5 Pantallas Composables
            composable(AppDestinations.Home.route) {

                // LazyColumn es un reciclaje de vista para que la app sea fluida
                LazyColumn(modifier = Modifier
                    .fillMaxSize()) {

                    //Título de Ofertas
                    item {
                        Text(
                            text = "🔥 OFERTAS IMPERDIBLES",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }

                    //Carrusel
                    item {
                        HomeCarousel(navController = navController) // Llama al carrusel
                    }

                   //Podemos agregar mas productos por aqui a futuro
                }
                // <<< CIERRE DEL LAZYCOLUMN >>>
            }



            composable(AppDestinations.Productos.route) {
                ProductosScreen(navController = navController)
            }

            composable(AppDestinations.Cart.route) {
                CarritoScreen(navController = navController)
            }
            composable(AppDestinations.Account.route) {
                Text(text = "Contenido de Cuenta")
            }
        }
    }
}