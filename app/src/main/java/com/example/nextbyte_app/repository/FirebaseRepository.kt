package com.example.nextbyte_app.repository

import android.net.Uri
import com.example.nextbyte_app.data.Notification
import com.example.nextbyte_app.data.Order
import com.example.nextbyte_app.data.OrderItem
import com.example.nextbyte_app.data.OrderStatus
import com.example.nextbyte_app.data.Product
import com.example.nextbyte_app.data.Review
import com.example.nextbyte_app.data.User
import com.example.nextbyte_app.data.UserRole
import com.google.firebase.Timestamp
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

    private fun parseOrder(doc: com.google.firebase.firestore.DocumentSnapshot): Order? {
        return try {
            val itemsList = doc.get("items") as? List<HashMap<String, Any>> ?: emptyList()
            val orderItems = itemsList.map {
                OrderItem(
                    productId = it["productId"] as? String ?: "",
                    name = it["name"] as? String ?: "",
                    quantity = (it["quantity"] as? Long)?.toInt() ?: 0,
                    price = it["price"] as? Double ?: 0.0
                )
            }
            Order(
                orderId = doc.getString("orderId") ?: doc.id,
                userId = doc.getString("userId") ?: "",
                items = orderItems,
                totalPrice = doc.getDouble("totalPrice") ?: 0.0,
                createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now(),
                status = OrderStatus.valueOf(doc.getString("status") ?: "PROCESANDO")
            )
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error parseando orden ${doc.id}: ${e.message}")
            null
        }
    }

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
            docRef.id
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateProduct(product: Product): Boolean {
        return try {
            db.collection("products").document(product.id).set(product).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun updateProductAverageRating(productId: String) {
        try {
            val reviews = getReviewsForProduct(productId)
            val averageRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0
            db.collection("products").document(productId).update("rating", averageRating).await()
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error actualizando el rating promedio: ${e.message}")
        }
    }

    suspend fun deleteProduct(productId: String): Boolean {
        return try {
            db.collection("products").document(productId).delete().await()
            true
        } catch (e: Exception) {
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
            ""
        }
    }

    // ========== FUNCIONES DE RESEÑAS ==========
    suspend fun addReview(productId: String, review: Review): Boolean {
        return try {
            db.collection("products").document(productId).collection("reviews").add(review).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getReviewsForProduct(productId: String): List<Review> {
        return try {
            db.collection("products").document(productId)
                .collection("reviews")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get().await().toObjects(Review::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ========== FUNCIONES DE ÓRDENES ==========
    suspend fun getAllOrders(): List<Order> {
        return try {
            val snapshot = db.collection("orders").orderBy("createdAt", Query.Direction.DESCENDING).get().await()
            snapshot.documents.mapNotNull(::parseOrder)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getOrdersForUser(userId: String): List<Order> {
        return try {
            val snapshot = db.collection("orders").whereEqualTo("userId", userId).orderBy("createdAt", Query.Direction.DESCENDING).get().await()
            snapshot.documents.mapNotNull(::parseOrder)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveOrder(order: Order): Boolean {
        return try {
            db.collection("orders").document(order.orderId).set(order).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Boolean {
        return try {
            db.collection("orders").document(orderId).update("status", newStatus).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error actualizando estado del pedido: ${e.message}")
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
            false
        }
    }

    fun listenForNotifications(): Flow<List<Notification>> = callbackFlow {
        val listener = db.collection("notifications")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects(Notification::class.java)).isSuccess
                }
            }
        awaitClose { listener.remove() }
    }

    // ========== FUNCIONES DE AUTENTICACIÓN ==========

    suspend fun sendPasswordResetEmail(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun registerUser(email: String, password: String, name: String): Boolean {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID es nulo")
            val user = User(uid = uid, email = email, name = name, role = UserRole.CUSTOMER)
            createOrUpdateUser(user)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
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
            false
        }
    }

    suspend fun changeUserPassword(newPassword: String): Boolean {
        return try {
            auth.currentUser!!.updatePassword(newPassword).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // ========== FUNCIONES DE USUARIOS ==========

    suspend fun getUserById(userId: String): User? {
        return try {
            db.collection("users").document(userId).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createOrUpdateUser(user: User): Boolean {
        return try {
            db.collection("users").document(user.uid).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateUserProfile(userId: String, userData: Map<String, Any>): Boolean {
        return try {
            db.collection("users").document(userId).update(userData).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun addFavorite(userId: String, productId: String): Boolean {
        return try {
            db.collection("users").document(userId).update("favoriteProductIds", FieldValue.arrayUnion(productId)).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFavorite(userId: String, productId: String): Boolean {
        return try {
            db.collection("users").document(userId).update("favoriteProductIds", FieldValue.arrayRemove(productId)).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAllUsers(): List<User> {
        return try {
            db.collection("users").get().await().toObjects(User::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateUserRole(userId: String, newRole: UserRole): Boolean {
        return try {
            db.collection("users").document(userId).update("role", newRole.name).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserAddresses(userId: String): List<String> {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            (doc.get("addresses") as? List<String>) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addUserAddress(userId: String, address: String): Boolean {
        return try {
            db.collection("users").document(userId).update("addresses", FieldValue.arrayUnion(address)).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteUserAddress(userId: String, address: String): Boolean {
        return try {
            db.collection("users").document(userId).update("addresses", FieldValue.arrayRemove(address)).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}