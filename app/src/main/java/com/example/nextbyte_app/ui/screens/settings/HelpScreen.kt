package com.example.nextbyte_app.ui.screens.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Modelo de datos para cada pregunta
data class FaqItem(val question: String, val answer: String)

// Lista de preguntas frecuentes de ejemplo
private val faqList = listOf(
    FaqItem(
        question = "¿Cuáles son los métodos de pago aceptados?",
        answer = "Aceptamos tarjetas de crédito y débito (Visa, MasterCard, American Express), transferencias bancarias y pagos a través de PayPal."
    ),
    FaqItem(
        question = "¿Cuánto tiempo tarda en llegar mi pedido?",
        answer = "El tiempo de entrega estándar es de 3 a 5 días hábiles. También ofrecemos envío express con entrega en 1-2 días hábiles por un costo adicional."
    ),
    FaqItem(
        question = "¿Puedo devolver un producto?",
        answer = "Sí, tienes 30 días a partir de la fecha de recepción para devolver cualquier producto, siempre que se encuentre en su empaque original y sin signos de uso. Contacta a nuestro soporte para iniciar el proceso."
    ),
    FaqItem(
        question = "¿Cómo puedo hacer seguimiento de mi pedido?",
        answer = "Una vez que tu pedido sea despachado, recibirás un correo electrónico con un número de seguimiento y un enlace para que puedas ver el estado de tu entrega en tiempo real."
    ),
    FaqItem(
        question = "¿Los productos tienen garantía?",
        answer = "Todos nuestros productos nuevos cuentan con una garantía de 1 año directamente con el fabricante. Los productos reacondicionados tienen una garantía de 6 meses con Next-Byte."
    )
)

@Composable
fun HelpScreen(navController: NavController) {
    Scaffold(
        topBar = { 
            TopAppBar(title = { Text("Centro de Ayuda") }) 
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Preguntas Frecuentes",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            items(faqList) { faqItem ->
                FaqCard(faqItem = faqItem)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                WhatsappSupportCard()
            }
        }
    }
}

@Composable
fun FaqCard(faqItem: FaqItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = faqItem.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = if (isExpanded) "Cerrar" else "Expandir"
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = faqItem.answer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun WhatsappSupportCard() {
    val context = LocalContext.current
    val phoneNumber = "56964837910"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Necesitas más ayuda?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nuestro equipo de soporte está listo para ayudarte con cualquier otra duda que tengas.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://wa.me/$phoneNumber")
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "No se pudo abrir WhatsApp. ¿Está instalado?", Toast.LENGTH_LONG).show()
                    }
                },
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Filled.Whatsapp, contentDescription = "WhatsApp", modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text("Hablar por WhatsApp")
            }
        }
    }
}
