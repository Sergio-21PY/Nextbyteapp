package com.example.nextbyte_app.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutNextByteScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acerca de Next Byte") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Bienvenido a Next Byte",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Tu tienda online de confianza para encontrar lo último y lo mejor en tecnología. Nos dedicamos a ofrecerte una cuidada selección de productos, desde smartphones y laptops de última generación hasta accesorios innovadores y gadgets que mejoran tu día a día.",
                fontSize = 16.sp, 
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Text(
                text = "En Next Byte, creemos que la tecnología debe ser accesible, funcional y emocionante. Nuestro compromiso es con la calidad, el buen servicio y, por supuesto, con los mejores precios del mercado. ¡Gracias por elegirnos!",
                fontSize = 16.sp, 
                lineHeight = 24.sp
            )
        }
    }
}