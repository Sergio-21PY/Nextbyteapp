package com.example.nextbyte_app.data

/**
 * Un objeto singleton para almacenar credenciales falsas para la aplicación.
 * En una aplicación real, esto sería reemplazado por una base de datos, una API, etc.
 */
object FakeCredentials {
    // Credenciales de la cuenta registrada por el usuario
    var registeredEmail: String = ""
    var registeredPassword: String = ""
    var registeredNumber: String = ""

    // Credenciales por defecto para pruebas rápidas
    const val DEFAULT_EMAIL = "test@nextbyte.cl"
    const val DEFAULT_PASSWORD = "123"

    /**
     * Limpia las credenciales del usuario registrado.
     */
    fun clearRegisteredUser() {
        registeredEmail = ""
        registeredPassword = ""
        registeredNumber = ""
    }
}
