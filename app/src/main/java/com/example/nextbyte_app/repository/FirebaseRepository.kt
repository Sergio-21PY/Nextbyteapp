package com.example.nextbyte_app.repository

import com.example.nextbyte_app.data.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Obtener todos los productos
    suspend fun getAllProducts(): List<Product> {
        return try {
            db.collection("products")
                .get()
                .await()
                .toObjects(Product::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Registrar nuevo usuario
    suspend fun registerUser(email: String, password: String, name: String): Boolean {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: ""

            // Guardar datos adicionales del usuario
            val user = mapOf(
                "uid" to uid,
                "email" to email,
                "name" to name
            )
            db.collection("users").document(uid).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Iniciar sesión
    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}