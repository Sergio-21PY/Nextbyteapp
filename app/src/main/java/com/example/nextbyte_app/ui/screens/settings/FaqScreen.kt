package com.example.nextbyte_app.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class FaqItem(val question: String, val answer: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(navController: NavController) {
    val context = LocalContext.current
    val faqItems = listOf(
        FaqItem(
            question = "¿Cómo puedo ver el estado de mi pedido?",
            answer = "Puedes ver el estado de tu pedido en la sección 'Pedidos anteriores' de tu cuenta. Allí encontrarás una línea de tiempo con cada etapa del proceso."
        ),
        FaqItem(
            question = "¿Qué hago si tengo un problema con mi pedido?",
            answer = "Si tienes algún problema, puedes contactarnos a través de WhatsApp para recibir ayuda personalizada. También puedes revisar nuestras políticas de devolución en 'Términos y condiciones'."
        ),
        FaqItem(
            question = "¿Cuáles son los métodos de pago aceptados?",
            answer = "Aceptamos tarjetas de crédito y débito de las principales marcas, así como transferencias bancarias."
        ),
        FaqItem(
            question = "¿Cuánto tiempo tarda en llegar mi pedido?",
            answer = "El tiempo de entrega varía según tu ubicación. Por lo general, los pedidos se entregan en un plazo de 3 a 5 días hábiles."
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preguntas Frecuentes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Remove, contentDescription = "Cerrar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(faqItems) { faq ->
                    FaqListItem(faq = faq)
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                }
            }
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://wa.me/56964837910")
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.SupportAgent, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hablar con un agente")
            }
        }
    }
}

@Composable
fun FaqListItem(faq: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = faq.question,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = faq.answer)
        }
    }
}
