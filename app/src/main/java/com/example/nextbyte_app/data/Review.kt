package com.example.nextbyte_app.data

import com.google.firebase.Timestamp

/**
 * Modelo de datos para una reseña de producto.
 *
 * @property id El ID único de la reseña.
 * @property userId El ID del usuario que escribió la reseña.
 * @property userName El nombre del usuario para mostrar en la UI.
 * @property rating La calificación en estrellas (ej. 4.5).
 * @property comment El comentario de texto del usuario.
 * @property createdAt La fecha en que se publicó la reseña.
 */
data class Review(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val createdAt: Timestamp = Timestamp.now()
) {
    // Constructor sin argumentos requerido por Firebase.
    constructor() : this("", "", "", 0.0, "", Timestamp.now())
}
