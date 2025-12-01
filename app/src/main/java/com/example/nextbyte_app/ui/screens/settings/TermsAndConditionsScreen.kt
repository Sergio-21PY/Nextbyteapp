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
fun TermsAndConditionsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Términos y Condiciones") },
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
                text = "1. Aceptación de los Términos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Al acceder y utilizar la aplicación Next Byte (la 'App'), usted acepta y se compromete a cumplir los presentes términos y condiciones de uso. Si no está de acuerdo con estos términos, por favor no utilice la App.",
                fontSize = 16.sp, 
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Text(
                text = "2. Política de Privacidad",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Nos comprometemos a proteger su privacidad. La información personal que nos proporcione será utilizada únicamente para procesar sus pedidos, mejorar su experiencia de compra y comunicarnos con usted. No venderemos, alquilaremos ni compartiremos su información personal con terceros sin su consentimiento, excepto cuando sea requerido por la ley.",
                fontSize = 16.sp, 
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Text(
                text = "3. Política de Devoluciones y Garantías",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Ofrecemos una política de devolución de 30 días en la mayoría de los productos. Los productos deben ser devueltos en su estado y embalaje original, sin usar. Todos los productos nuevos tienen una garantía de un año del fabricante. Los productos reacondicionados o de segunda mano tienen una garantía limitada de 90 días. Por favor, consulte la página de cada producto para detalles específicos de la garantía.",
                fontSize = 16.sp, 
                lineHeight = 24.sp
            )
        }
    }
}