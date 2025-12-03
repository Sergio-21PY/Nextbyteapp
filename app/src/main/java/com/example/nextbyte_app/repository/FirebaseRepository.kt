package com.example.nextbyte_app.repository

import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.data.User
import com.example.nextbyte_app.data.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import android.util.Log

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // ========== FUNCIONES DE PRODUCTOS ==========

    suspend fun getAllProducts(): List<Product> {
        return try {
            db.collection("products").get().await().toObjects(Product::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error obteniendo productos: ${e.message}")
            emptyList()
        }
    }

    suspend fun addProduct(product: Product): String {
        return try {
            val docRef = db.collection("products").document()
            val productWithId = product.copy(id = docRef.id)
            docRef.set(productWithId).await()
            Log.d("FirebaseRepository", "Producto agregado exitosamente: ${product.name}")
            docRef.id
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error agregando producto: ${e.message}", e)
            throw e
        }
    }

    suspend fun getCategories(): List<String> {
        return try {
            val snapshot = db.collection("categories").get().await()
            snapshot.documents.map { it.id }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error obteniendo categorías: ${e.message}")
            emptyList()
        }
    }

    // ========== FUNCIONES DE AUTENTICACIÓN ==========

    suspend fun registerUser(email: String, password: String, name: String): Boolean {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID es nulo")
            val user = User(uid = uid, email = email, name = name, role = UserRole.CUSTOMER)
            createOrUpdateUser(user)
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error en registro: ${e.message}")
            false
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error en login: ${e.message}")
            false
        }
    }

    fun getCurrentUser() = auth.currentUser

    fun logout() {
        auth.signOut()
        Log.d("FirebaseRepository", "Usuario cerró sesión")
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    suspend fun reauthenticateUser(password: String): Boolean {
        return try {
            val user = auth.currentUser!!
            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            user.reauthenticate(credential).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error de reautenticación: ${e.message}")
            false
        }
    }

    suspend fun changeUserEmail(newEmail: String): Boolean {
        return try {
            val user = auth.currentUser!!
            val userId = user.uid

            // 1. Actualizar en Firebase Authentication
            user.updateEmail(newEmail).await()

            // 2. Actualizar en Firestore
            db.collection("users").document(userId).update("email", newEmail).await()
            
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error cambiando email: ${e.message}")
            false
        }
    }

    suspend fun changeUserPassword(newPassword: String): Boolean {
        return try {
            auth.currentUser!!.updatePassword(newPassword).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error cambiando contraseña: ${e.message}")
            false
        }
    }

    // ========== FUNCIONES DE USUARIOS ==========

    suspend fun getUserById(userId: String): User? {
        return try {
            db.collection("users").document(userId).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error obteniendo usuario: ${e.message}")
            null
        }
    }

    suspend fun createOrUpdateUser(user: User): Boolean {
        return try {
            db.collection("users").document(user.uid).set(user).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error creando/actualizando usuario: ${e.message}")
            false
        }
    }

    suspend fun updateUserProfile(userId: String, userData: Map<String, Any>): Boolean {
        return try {
            db.collection("users").document(userId).update(userData).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error actualizando perfil: ${e.message}")
            false
        }
    }

    suspend fun getAllUsers(): List<User> {
        return try {
            db.collection("users").get().await().toObjects(User::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error obteniendo todos los usuarios: ${e.message}")
            emptyList()
        }
    }

    suspend fun updateUserRole(userId: String, newRole: UserRole): Boolean {
        return try {
            db.collection("users").document(userId).update("role", newRole.name).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error actualizando rol de usuario: ${e.message}")
            false
        }
    }

    suspend fun getUserAddresses(userId: String): List<String> {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            (doc.get("addresses") as? List<String>) ?: emptyList()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error obteniendo direcciones: ${e.message}")
            emptyList()
        }
    }

    suspend fun addUserAddress(userId: String, address: String): Boolean {
        return try {
            db.collection("users").document(userId).update("addresses", FieldValue.arrayUnion(address)).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error agregando dirección: ${e.message}")
            false
        }
    }

    suspend fun deleteUserAddress(userId: String, address: String): Boolean {
        return try {
            db.collection("users").document(userId).update("addresses", FieldValue.arrayRemove(address)).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error eliminando dirección: ${e.message}")
            false
        }
    }
}