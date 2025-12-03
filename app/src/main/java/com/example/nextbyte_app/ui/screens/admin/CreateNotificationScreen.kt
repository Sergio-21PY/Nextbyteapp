package com.example.nextbyte_app.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nextbyte_app.viewmodels.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNotificationScreen(
    navController: NavController, 
    notificationViewModel: NotificationViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val sendState by notificationViewModel.sendState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(sendState) {
        when (sendState) {
            is NotificationViewModel.SendState.Sent -> {
                Toast.makeText(context, "✅ Notificación guardada en la base de datos", Toast.LENGTH_SHORT).show()
                notificationViewModel.resetSendState()
                navController.popBackStack()
            }
            is NotificationViewModel.SendState.Error -> {
                val error = (sendState as NotificationViewModel.SendState.Error).message
                Toast.makeText(context, "❌ $error", Toast.LENGTH_LONG).show()
                notificationViewModel.resetSendState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Notificación") },
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
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Mensaje") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                maxLines = 10
            )
            Button(
                onClick = { notificationViewModel.sendNotification(title, message) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = title.isNotBlank() && message.isNotBlank() && sendState !is NotificationViewModel.SendState.Sending
            ) {
                if (sendState is NotificationViewModel.SendState.Sending) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar Notificación")
                }
            }
        }
    }
}