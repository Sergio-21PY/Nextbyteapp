package com.example.nextbyte_app.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class User(
    @PropertyName("uid") val uid: String = "",
    @PropertyName("email") val email: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("phone") val phone: String = "",
    @PropertyName("address") val address: String = "",
    @PropertyName("role") val role: UserRole = UserRole.CUSTOMER,
    @PropertyName("createdAt") val createdAt: Timestamp? = null,
    @PropertyName("profileImage") val profileImage: String = "",
    @PropertyName("isEmailVerified") val isEmailVerified: Boolean = false
) {
    // Constructor sin parámetros para Firebase
    constructor() : this("", "", "", "", "", UserRole.CUSTOMER, null, "", false)
}

enum class UserRole {
    ADMIN,      // Puede agregar/editar/eliminar productos
    MANAGER,    // Puede agregar/editar productos
    CUSTOMER,   // Solo puede comprar y ver productos
    GUEST       // Usuario no registrado
}

// Extension functions para verificar permisos
fun User.canAddProducts(): Boolean {
    return this.role == UserRole.ADMIN || this.role == UserRole.MANAGER
}

fun User.canEditProducts(): Boolean {
    return this.role == UserRole.ADMIN || this.role == UserRole.MANAGER
}

fun User.canDeleteProducts(): Boolean {
    return this.role == UserRole.ADMIN
}

fun User.canViewAdminPanel(): Boolean {
    return this.role == UserRole.ADMIN || this.role == UserRole.MANAGER
}

// Función para obtener el rol como string para mostrar
fun User.getRoleDisplayName(): String {
    return when (this.role) {
        UserRole.ADMIN -> "Administrador"
        UserRole.MANAGER -> "Gestor"
        UserRole.CUSTOMER -> "Cliente"
        UserRole.GUEST -> "Invitado"
    }
}