package com.example.nextbyte_app.repository

import android.net.Uri
import com.example.nextbyte_app.data.Notification
import com.example.nextbyte_app.data.Order
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.data.User
import com.example.nextbyte_app.data.UserRole
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import android.util.Log
import java.util.UUID

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // ========== FUNCIONES DE PRODUCTOS ==========

    suspend fun getAllProducts(): List<Product> {
        return try {
            val snapshot = db.collection("products").get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    Product(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        code = doc.getString("code") ?: "",
                        price = doc.getDouble("price") ?: (doc.getLong("price")?.toDouble() ?: 0.0),
                        cost = doc.getDouble("cost") ?: (doc.getLong("cost")?.toDouble() ?: 0.0),
                        imageUrl = doc.getString("imageUrl") ?: "",
                        category = doc.getString("category") ?: "",
                        stock = doc.getLong("stock")?.toInt() ?: 0,
                        rating = doc.getDouble("rating") ?: 0.0,
                        isFavorited = doc.getBoolean("isFavorited") ?: false
                    )
                } catch (e: Exception) {
                    Log.e("FirebaseRepository", "Error parseando producto ${doc.id}: ${e.message}")
                    null
                }
            }
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

    suspend fun updateProduct(product: Product): Boolean {
        return try {
            db.collection("products").document(product.id).set(product).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error actualizando producto: ${e.message}")
            false
        }
    }

    suspend fun deleteProduct(productId: String): Boolean {
        return try {
            db.collection("products").document(productId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error eliminando producto: ${e.message}")
            false
        }
    }
    
    suspend fun uploadImage(imageUri: Uri): String {
        return try {
            val storageRef = storage.reference
            val imageRef = storageRef.child("product_images/${UUID.randomUUID()}")
            imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error subiendo imagen: ${e.message}")
            ""
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

    // ========== FUNCIONES DE ÓRDENES ==========
    suspend fun getAllOrders(): List<Order> {
        return try {
            db.collection("orders").get().await().toObjects(Order::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error obteniendo órdenes: ${e.message}")
            emptyList()
        }
    }
    
    suspend fun saveOrder(order: Order): Boolean {
        return try {
            db.collection("orders").document(order.orderId).set(order).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error guardando la orden: ${e.message}")
            false
        }
    }

    // ========== FUNCIONES DE NOTIFICACIONES ==========
    suspend fun sendNotification(notification: Notification): Boolean {
        return try {
            val docRef = db.collection("notifications").document()
            val notificationWithId = notification.copy(id = docRef.id)
            docRef.set(notificationWithId).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error enviando notificación: ${e.message}")
            false
        }
    }

    fun listenForNotifications(): Flow<List<Notification>> = callbackFlow {
        val listener = db.collection("notifications")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("FirebaseRepository", "Listen error", e)
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val notifications = snapshot.toObjects(Notification::class.java)
                    trySend(notifications).isSuccess
                }
            }
        awaitClose { listener.remove() }
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

    fun logout() {
        auth.signOut()
    }

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
    
    suspend fun addFavorite(userId: String, productId: String): Boolean {
        return try {
            db.collection("users").document(userId).update("favoriteProductIds", FieldValue.arrayUnion(productId)).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error añadiendo a favoritos: ${e.message}")
            false
        }
    }

    suspend fun removeFavorite(userId: String, productId: String): Boolean {
        return try {
            db.collection("users").document(userId).update("favoriteProductIds", FieldValue.arrayRemove(productId)).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error eliminando de favoritos: ${e.message}")
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