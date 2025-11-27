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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutNextByteScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acerca de Next-Byte") },
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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp)
        ) {
            item {
                Text(
                    text = "Más que una tienda, somos tu próximo destino tecnológico.",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            item { SectionTitle(title = "Nuestra Misión") }
            item {
                SectionParagraph(
                    text = "En Next-Byte, nuestra misión es simple: conectar a los apasionados de la tecnología con los mejores productos del mercado. Creemos que la tecnología no solo debe ser funcional, sino también inspiradora. Por eso, seleccionamos cuidadosamente cada artículo de nuestro catálogo, asegurando que cumpla con los más altos estándares de calidad, innovación y rendimiento."
                )
            }

            item { SectionTitle(title = "Nuestra Historia") }
            item {
                SectionParagraph(
                    text = "Next-Byte nació de la pasión de un grupo de entusiastas del hardware y el gaming que soñaban con crear un espacio donde otros aficionados pudieran encontrar no solo componentes, sino también asesoramiento experto y una comunidad. Lo que comenzó como un pequeño proyecto se ha convertido en una de las tiendas de tecnología de referencia, pero nuestro espíritu sigue siendo el mismo: amor por la tecnología y compromiso con nuestros clientes."
                )
            }

            item { SectionTitle(title = "Nuestro Compromiso") }
            item {
                SectionParagraph(
                    text = "Tu satisfacción es nuestra prioridad. Desde el momento en que navegas por nuestra app hasta que recibes tu producto en casa, nos esforzamos por ofrecerte una experiencia de compra excepcional. Nuestro equipo está siempre disponible para ayudarte a encontrar el producto perfecto, resolver tus dudas y asegurarse de que quedes 100% satisfecho con tu compra."
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Gracias por formar parte de la comunidad Next-Byte. ¡Explora, descubre y lleva tu setup al siguiente nivel!",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
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
