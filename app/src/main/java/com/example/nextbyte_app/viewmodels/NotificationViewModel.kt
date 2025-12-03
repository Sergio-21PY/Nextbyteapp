package com.example.nextbyte_app.viewmodels

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextbyte_app.R
import com.example.nextbyte_app.data.Notification
import com.example.nextbyte_app.repository.FirebaseRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _sendState = MutableStateFlow<SendState>(SendState.Idle)
    val sendState: StateFlow<SendState> = _sendState

    private var listenerStartTime: Timestamp? = null

    sealed class SendState {
        object Idle : SendState()
        object Sending : SendState()
        object Sent : SendState()
        data class Error(val message: String) : SendState()
    }

    fun listenForNotifications(context: Context) {
        if (listenerStartTime != null) return

        listenerStartTime = Timestamp.now()

        viewModelScope.launch {
            repository.listenForNotifications().collectLatest { newNotifications ->
                val latestNotification = newNotifications.firstOrNull()
                if (latestNotification != null && latestNotification.createdAt.seconds > (listenerStartTime?.seconds ?: 0)) {
                    showLocalNotification(context, latestNotification.title, latestNotification.message)
                    listenerStartTime = latestNotification.createdAt
                }
                _notifications.value = newNotifications
            }
        }
    }

    fun sendNotification(title: String, message: String) {
        viewModelScope.launch {
            _sendState.value = SendState.Sending
            val notification = Notification(title = title, message = message)
            val success = repository.sendNotification(notification)
            _sendState.value = if (success) SendState.Sent else SendState.Error("No se pudo enviar la notificación.")
        }
    }

    private fun showLocalNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "new_message_channel_id"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Nuevos Mensajes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para notificaciones de nuevos mensajes en la app"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    fun resetSendState() {
        _sendState.value = SendState.Idle
    }
}