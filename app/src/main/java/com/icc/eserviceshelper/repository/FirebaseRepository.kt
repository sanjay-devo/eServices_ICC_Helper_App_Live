package com.icc.eserviceshelper.repository

import com.google.firebase.database.*
import com.icc.eserviceshelper.models.Category
import com.icc.eserviceshelper.models.ServiceItem
import com.icc.eserviceshelper.models.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import com.google.firebase.database.ServerValue

class FirebaseRepository {
    private val database = FirebaseDatabase.getInstance("https://eservices-icc-helper-app-default-rtdb.firebaseio.com/").reference
    private val categoriesRef = database.child("categories")
    private val ordersRef = database.child("orders")

    fun getCategories(): Flow<Result<List<Category>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Move mapping to background thread
                launch(Dispatchers.Default) {
                    try {
                        val categoriesList = mutableListOf<Category>()
                        
                        // Iterating through children directly ensures the order defined by numeric keys in Firebase
                        for (categorySnapshot in snapshot.children) {
                            val title = categorySnapshot.child("title").getValue(String::class.java) ?: ""
                            val iconUrl = categorySnapshot.child("icon_url").getValue(String::class.java) ?: ""
                            
                            // Reconstructing items using LinkedHashMap to preserve numeric order (1, 2, 3...)
                            val itemsMap = LinkedHashMap<String, ServiceItem>()
                            categorySnapshot.child("items").children.forEach { itemSnapshot ->
                                val item = itemSnapshot.getValue(ServiceItem::class.java)?.copy(id = itemSnapshot.key ?: "")
                                if (item != null) {
                                    itemsMap[itemSnapshot.key ?: ""] = item
                                }
                            }

                            categoriesList.add(
                                Category(
                                    id = categorySnapshot.key ?: "",
                                    title = title,
                                    icon_url = iconUrl,
                                    items = itemsMap
                                )
                            )
                        }
                        trySend(Result.success(categoriesList))
                    } catch (e: Exception) {
                        trySend(Result.failure(e))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        categoriesRef.addValueEventListener(listener)
        awaitClose { categoriesRef.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)

    fun getOrders(): Flow<Result<List<Order>>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                launch(Dispatchers.Default) {
                    try {
                        val ordersList = mutableListOf<Order>()
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(Order::class.java)
                            if (order != null) {
                                ordersList.add(order)
                            }
                        }
                        // Sort by timestamp descending (newest first)
                        ordersList.sortByDescending { it.timestamp }
                        trySend(Result.success(ordersList))
                    } catch (e: Exception) {
                        trySend(Result.failure(e))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }
        ordersRef.addValueEventListener(listener)
        awaitClose { ordersRef.removeEventListener(listener) }
    }.flowOn(Dispatchers.IO)

    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            suspendCancellableCoroutine { continuation ->
                ordersRef.child(orderId).child("status").setValue(newStatus)
                    .addOnSuccessListener {
                        continuation.resume(Result.success(Unit))
                    }
                    .addOnFailureListener {
                        continuation.resume(Result.failure(it))
                    }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun applyForService(
        serviceTitle: String,
        subserviceTitle: String,
        userName: String,
        mobileNumber: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val orderId = ordersRef.push().key ?: return@withContext Result.failure(Exception("Failed to generate order ID"))
            val order = mapOf(
                "id" to orderId,
                "service" to serviceTitle,
                "subservice" to subserviceTitle,
                "userName" to userName,
                "mobileNumber" to mobileNumber,
                "timestamp" to ServerValue.TIMESTAMP,
                "status" to Order.STATUS_PENDING
            )
            
            suspendCancellableCoroutine { continuation ->
                ordersRef.child(orderId).setValue(order)
                    .addOnSuccessListener {
                        continuation.resume(Result.success(Unit))
                    }
                    .addOnFailureListener {
                        continuation.resume(Result.failure(it))
                    }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
