package com.example.nextbyte_app.ui.screens.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Última actualización: 24 de mayo de 2024",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Bienvenido a Next-Byte. Al utilizar nuestra aplicación, aceptas los siguientes términos y condiciones. Por favor, léelos con atención.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item { SectionTitle(title = "1. Uso de la Cuenta") }
            item {
                SectionParagraph(
                    text = "Para realizar compras y acceder a ciertas funciones, es posible que debas crear una cuenta. Eres responsable de mantener la confidencialidad de tu contraseña y de toda la actividad que ocurra en tu cuenta. Debes ser mayor de 18 años para crear una cuenta y realizar compras."
                )
            }

            item { SectionTitle(title = "2. Propiedad Intelectual") }
            item {
                SectionParagraph(
                    text = "Todo el contenido presente en esta aplicación, incluyendo textos, gráficos, logos, iconos y software, es propiedad de Next-Byte o de sus proveedores de contenido, y está protegido por las leyes de propiedad intelectual. No se permite la reproducción, modificación o distribución del contenido sin nuestro consentimiento expreso por escrito."
                )
            }

            item { SectionTitle(title = "3. Compras y Pagos") }
            item {
                SectionParagraph(
                    text = "Next-Byte se esfuerza por mostrar los precios y la disponibilidad de los productos de la manera más precisa posible. Sin embargo, nos reservamos el derecho de corregir cualquier error en los precios o en la información del producto en cualquier momento. Todas las ventas son finales, sujetas a nuestras políticas de devolución y garantía."
                )
            }

            item { SectionTitle(title = "4. Limitación de Responsabilidad") }
            item {
                SectionParagraph(
                    text = "El uso de esta aplicación es bajo tu propio riesgo. Next-Byte no garantiza que el servicio sea ininterrumpido o libre de errores. En la máxima medida permitida por la ley, Next-Byte no será responsable de ningún daño directo, indirecto, incidental o consecuente que resulte del uso o la incapacidad de usar nuestra aplicación."
                )
            }

            item { SectionTitle(title = "5. Modificaciones de los Términos") }
            item {
                SectionParagraph(
                    text = "Nos reservamos el derecho de modificar estos términos y condiciones en cualquier momento. Cualquier cambio será efectivo inmediatamente después de su publicación en la aplicación. Te recomendamos revisar esta página periódicamente para estar al tanto de las actualizaciones."
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SectionParagraph(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}
