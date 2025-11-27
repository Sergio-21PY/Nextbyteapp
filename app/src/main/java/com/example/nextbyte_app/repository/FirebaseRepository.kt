package com.example.nextbyte_app.repository

import com.example.nextbyte_app.data.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.Timestamp
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

    // Agregar nuevo producto
    suspend fun addProduct(product: Product): String {
        return try {
            val productData = hashMapOf(
                "name" to product.name,
                "description" to product.description,
                "price" to product.price,
                "category" to product.category,
                "imageUrl" to product.imageUrl,
                "rating" to product.rating,
                "stock" to product.stock,
                "timestamp" to Timestamp.now()
            )

            val docRef = db.collection("products").document()
            docRef.set(productData).await()
            Log.d("FirebaseRepository", "Producto agregado exitosamente: ${product.name}")
            docRef.id // Retornar el ID del documento creado
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error agregando producto: ${e.message}", e)
            throw e
        }
    }

    // Obtener categorías
    suspend fun getCategories(): List<String> {
        return try {
            val snapshot = db.collection("categories").get().await()
            val categories = snapshot.documents.map { it.id }
            Log.d("FirebaseRepository", "Categorías obtenidas: ${categories.size}")
            categories
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error obteniendo categorías: ${e.message}")
            // Si no hay categorías, retornar categorías por defecto
            listOf(
                "Smartphones",
                "Laptops",
                "Tablets",
                "Accessories",
                "Gaming",
                "Audio",
                "Cameras",
                "Wearables",
                "Home Appliances"
            )
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
            auth.signInWithEmailAndPassword(email, password).await()
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

    // Actualizar producto existente
    suspend fun updateProduct(productId: String, product: Product): Boolean {
        return try {
            val productData = hashMapOf(
                "name" to product.name,
                "description" to product.description,
                "price" to product.price,
                "category" to product.category,
                "imageUrl" to product.imageUrl,
                "rating" to product.rating,
                "stock" to product.stock,
                "updatedAt" to Timestamp.now()
            )

            db.collection("products")
                .document(productId)
                .update(productData as Map<String, Any>)
                .await()

            Log.d("FirebaseRepository", "Producto actualizado exitosamente: $productId")
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error actualizando producto: ${e.message}", e)
            false
        }
    }

    // Eliminar producto
    suspend fun deleteProduct(productId: String): Boolean {
        return try {
            db.collection("products")
                .document(productId)
                .delete()
                .await()

            Log.d("FirebaseRepository", "Producto eliminado exitosamente: $productId")
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error eliminando producto: ${e.message}", e)
            false
        }
    }

    // Buscar productos por nombre
    suspend fun searchProducts(query: String): List<Product> {
        return try {
            db.collection("products")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .await()
                .toObjects(Product::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error buscando productos: ${e.message}")
            emptyList()
        }
    }

    // Obtener productos destacados (con rating alto)
    suspend fun getFeaturedProducts(): List<Product> {
        return try {
            db.collection("products")
                .whereGreaterThanOrEqualTo("rating", 4.5)
                .get()
                .await()
                .toObjects(Product::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error obteniendo productos destacados: ${e.message}")
            emptyList()
        }
    }

    // Obtener productos con poco stock
    suspend fun getLowStockProducts(): List<Product> {
        return try {
            db.collection("products")
                .whereLessThanOrEqualTo("stock", 5)
                .get()
                .await()
                .toObjects(Product::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error obteniendo productos con poco stock: ${e.message}")
            emptyList()
        }
    }

    // Agregar múltiples productos (batch)
    suspend fun addMultipleProducts(products: List<Product>): Boolean {
        return try {
            val batch = db.batch()

            products.forEach { product ->
                val docRef = db.collection("products").document()
                val productData = hashMapOf(
                    "name" to product.name,
                    "description" to product.description,
                    "price" to product.price,
                    "category" to product.category,
                    "imageUrl" to product.imageUrl,
                    "rating" to product.rating,
                    "stock" to product.stock,
                    "timestamp" to Timestamp.now()
                )
                batch.set(docRef, productData)
            }

            batch.commit().await()
            Log.d("FirebaseRepository", "${products.size} productos agregados exitosamente")
            true
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error agregando múltiples productos: ${e.message}", e)
            false
        }
    }
}