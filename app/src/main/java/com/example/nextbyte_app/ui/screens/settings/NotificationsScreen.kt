package com.example.nextbyte_app.ui.screens.settings

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.nextbyte_app.R

private const val CHANNEL_ID = "nextbyte_channel"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    val context = LocalContext.current
    var hasNotificationPermission by remember { mutableStateOf(false) }

    // Launcher para solicitar el permiso de notificaciones.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            if (!isGranted) {
                Toast.makeText(context, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Comprueba el permiso al entrar y crea el canal de notificación.
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasNotificationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
        createNotificationChannel(context)
    }

    // Estado para la notificación personalizada.
    var customTitle by remember { mutableStateOf("") }
    var customText by remember { mutableStateOf("") }

    // Función reutilizable para enviar notificaciones.
    val sendNotification: (String, String, Int) -> Unit = { title, text, id ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (hasNotificationPermission) {
                showNotification(context, title, text, id)
            } else {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            showNotification(context, title, text, id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Notificaciones") },
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
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Sección de Notificaciones Predefinidas ---
            item {
                Text("Notificaciones Predefinidas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Button(
                    onClick = { sendNotification("¡20% de Descuento!", "Usa el código PROMO20 esta semana.", 1) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enviar oferta del 20%")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Button(
                    onClick = { sendNotification("¡Descuentos Imperdibles!", "No olvides que esta semana hay ofertas en teclados y monitores.", 2) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enviar recordatorio de descuentos")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- Sección de Notificación Personalizada ---
            item {
                Text("Notificación Personalizada", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                OutlinedTextField(
                    value = customTitle,
                    onValueChange = { customTitle = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                OutlinedTextField(
                    value = customText,
                    onValueChange = { customText = it },
                    label = { Text("Mensaje de la notificación") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Button(
                    onClick = {
                        if (customTitle.isNotBlank() && customText.isNotBlank()) {
                            sendNotification(customTitle, customText, 3)
                            customTitle = ""
                            customText = ""
                        } else {
                            Toast.makeText(context, "Por favor, completa título y mensaje", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enviar Notificación Personalizada")
                }
            }
        }
    }
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Next-Byte Notificaciones"
        val descriptionText = "Canal para notificaciones de ofertas y estado de pedidos."
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

private fun showNotification(context: Context, title: String, text: String, notificationId: Int) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.promo1) // Asegúrate de tener un icono válido aquí
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setStyle(NotificationCompat.BigTextStyle().bigText(text)) // Permite texto más largo
        .setAutoCancel(true)
        .build()

    notificationManager.notify(notificationId, notification)
}
