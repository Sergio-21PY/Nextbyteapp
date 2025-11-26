package com.example.nextbyte_app.data

/**
 * Objeto que simula ser una fuente de datos remota o local.
 * En una app real, esto sería reemplazado por una base de datos (Room)
 * o una implementación de una API (Retrofit).
 */
object FakeDataSource {
    // Simulación de una tabla de usuarios
    var registeredEmail: String = ""
    var registeredPassword: String = ""
    var registeredNumber: String = ""

    // Datos de prueba o por defecto
    const val DEFAULT_EMAIL = "test@nextbyte.cl"
    const val DEFAULT_PASSWORD = "123"

    fun deleteAccount() {
        registeredEmail = ""
        registeredPassword = ""
        registeredNumber = ""
    }
}
