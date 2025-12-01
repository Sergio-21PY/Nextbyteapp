package com.example.nextbyte_app.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class FaqItem(val question: String, val answer: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    val context = LocalContext.current
    val faqs = listOf(
        FaqItem("¿Cómo puedo realizar un seguimiento de mi pedido?", "Una vez que tu pedido haya sido enviado, recibirás un correo electrónico con un número de seguimiento. Puedes usar este número en el sitio web del transportista para ver el estado de tu entrega."),
        FaqItem("¿Cuál es la política de devoluciones?", "Ofrecemos una política de devolución de 30 días para la mayoría de los artículos. Los productos deben estar en su embalaje original y sin usar. Consulta nuestra página de política de devoluciones para obtener más detalles."),
        FaqItem("¿Ofrecen garantía en sus productos?", "Sí, todos nuestros productos nuevos vienen con una garantía del fabricante de un año. Los productos reacondicionados tienen una garantía de 90 días.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayuda y Soporte") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Preguntas Frecuentes", style = MaterialTheme.typography.titleLarge)
            }
            items(faqs.size) { index ->
                FaqCard(faq = faqs[index])
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("¿No encuentras lo que buscas?", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://wa.me/56964837910")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Whatsapp, contentDescription = "WhatsApp")
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text("Contactar por WhatsApp")
                }
            }
        }
    }
}

@Composable
fun FaqCard(faq: FaqItem) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(faq.question, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(faq.answer)
        }
    }
}
