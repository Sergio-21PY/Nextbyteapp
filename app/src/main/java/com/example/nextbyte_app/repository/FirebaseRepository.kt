package com.example.nextbyte_app.repository

import com.example.nextbyte_app.data.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await
import android.util.Log

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
            Log.e("FirebaseRepository", "Error obteniendo productos: ${e.message}")
            emptyList()
        }
    }

    // Registrar nuevo usuario
    suspend fun registerUser(email: String, password: String, name: String): Boolean {
        return try {
            Log.d("FirebaseRepository", "Intentando registrar: $email")

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID es nulo")

            Log.d("FirebaseRepository", "Usuario creado en Auth, UID: $uid")

            // Crear objeto de usuario para Firestore
            val user = hashMapOf(
                "uid" to uid,
                "email" to email,
                "name" to name,
                "createdAt" to FieldValue.serverTimestamp(),
                "phone" to "",
                "address" to ""
            )

            // Guardar en Firestore
            db.collection("users").document(uid).set(user).await()
            Log.d("FirebaseRepository", "Usuario guardado en Firestore")

            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error en registro: ${e.message}", e)
            false
        }
    }

    // Iniciar sesión
    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            Log.d("FirebaseRepository", "Intentando login: $email")
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            Log.d("FirebaseRepository", "Login exitoso: ${user?.email}")
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error en login: ${e.message}")
            false
        }
    }

    // Obtener usuario actual
    fun getCurrentUser() = auth.currentUser

    // Cerrar sesión
    fun logout() {
        auth.signOut()
        Log.d("FirebaseRepository", "Usuario cerró sesión")
    }

    // Verificar si hay usuario logueado
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Obtener productos por categoría
    suspend fun getProductsByCategory(category: String): List<Product> {
        return try {
            db.collection("products")
                .whereEqualTo("category", category)
                .get()
                .await()
                .toObjects(Product::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error obteniendo productos por categoría: ${e.message}")
            emptyList()
        }
    }

    // Obtener producto por ID
    suspend fun getProductById(productId: String): Product? {
        return try {
            db.collection("products")
                .document(productId)
                .get()
                .await()
                .toObject(Product::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error obteniendo producto por ID: ${e.message}")
            null
        }
    }
}