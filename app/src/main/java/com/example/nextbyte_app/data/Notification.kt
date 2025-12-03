package com.example.nextbyte_app.data

import com.google.firebase.Timestamp

/**
 * Modelo de datos para una notificación.
 *
 * @property id El ID único de la notificación.
 * @property title El título del mensaje.
 * @property message El cuerpo del mensaje.
 * @property createdAt La fecha y hora en que se creó la notificación.
 */
data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val createdAt: Timestamp = Timestamp.now()
) {
    // Constructor sin argumentos requerido por Firebase para la deserialización.
    constructor() : this("", "", "", Timestamp.now())
}
