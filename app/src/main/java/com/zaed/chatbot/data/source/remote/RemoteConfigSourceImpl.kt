package com.zaed.chatbot.data.source.remote

import android.util.Log
import com.android.billingclient.api.Purchase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.zaed.chatbot.ui.activity.CurrentUserPurchase
import com.zaed.chatbot.ui.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class RemoteConfigSourceImpl(
    private val remoteConfig: FirebaseFirestore
) : RemoteConfigSource {
    override fun getUserFreeTrialAndImageLimit(
        androidId: String,
        product: Purchase?
    ): Flow<Pair<Int, Int>> = callbackFlow {
        try {
            when (product == null) {
                true -> {
                    remoteConfig.collection("users").document(androidId)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                close(e)
                                return@addSnapshotListener
                            }
                            if (snapshot != null && snapshot.exists()) {
                                Log.d(
                                    "RemoteConfigSourceImpl",
                                    "getUserFreeTrialAndImageLimit: ${snapshot.data}"
                                )
                                val currentPurchase =
                                    snapshot.toObject(CurrentUserPurchase::class.java)
                                val freeTrialCount = currentPurchase?.freeTrialCount ?: 5
                                trySend(Pair(freeTrialCount, 0))
                            } else {
                                val updatedPurchase = CurrentUserPurchase(
                                    productId = "",
                                    purchaseToken = "",
                                    purchaseTime = 0L,
                                    title = "",
                                    imageLimit = 0,
                                    freeTrialCount = 5
                                )
                                remoteConfig.collection("users").document(androidId)
                                    .set(updatedPurchase)
                                    .addOnSuccessListener {
                                        trySend(Pair(5, 0))
                                    }.addOnFailureListener {
                                        close(it)
                                    }
                            }
                        }
                }

                false -> {
                    val limit = if (product.products.first() == Constants.YEARLY_SUBSCRIPTION_ID) {
                        Constants.YEARLY_SUBSCRIPTION_IMAGE_GENERATION_LIMIT
                    } else {
                        Constants.MONTHLY_SUBSCRIPTION_IMAGE_GENERATION_LIMIT
                    }

                    remoteConfig.collection("users").document(androidId)
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                close(e)
                                return@addSnapshotListener
                            }
                            if (snapshot != null && snapshot.exists()) {
                                Log.d(
                                    "RemoteConfigSourceImpl",
                                    "getUserFreeTrialAndImageLimit: ${snapshot.data}"
                                )
                                val currentPurchase =
                                    snapshot.toObject(CurrentUserPurchase::class.java)
                                val imageFreeTrialCount = currentPurchase?.imageLimit ?: limit
                                val freeTrialCount = currentPurchase?.freeTrialCount ?: 5
                                val currentToken = currentPurchase?.purchaseToken

                                if (currentToken != product.purchaseToken && product.purchaseToken.isNotEmpty()) {
                                    // Reset the model if the token has changed
                                    val updatedPurchase = CurrentUserPurchase(
                                        productId = product.products.firstOrNull() ?: "",
                                        purchaseToken = product.purchaseToken,
                                        purchaseTime = System.currentTimeMillis(),
                                        title = product.products.firstOrNull() ?: "",
                                        imageLimit = limit,
                                        freeTrialCount = 5
                                    )
                                    remoteConfig.collection("users").document(androidId)
                                        .set(updatedPurchase)
                                        .addOnSuccessListener {
                                            trySend(Pair(5, limit))
                                        }.addOnFailureListener {
                                            close(it)
                                        }
                                } else {
                                    trySend(Pair(freeTrialCount, imageFreeTrialCount))
                                }
                            } else {
                                // Initialize model for new user
                                val newPurchase = CurrentUserPurchase(
                                    productId = product.products.firstOrNull() ?: "",
                                    purchaseToken = product.purchaseToken,
                                    purchaseTime = System.currentTimeMillis(),
                                    title = product.products.firstOrNull() ?: "",
                                    imageLimit = limit,
                                    freeTrialCount = 5
                                )
                                remoteConfig.collection("users").document(androidId)
                                    .set(newPurchase)
                                    .addOnSuccessListener {
                                        trySend(Pair(5, limit))
                                    }.addOnFailureListener {
                                        close(it)
                                    }
                            }
                        }
                }
            }

        } catch (e: Exception) {
            Log.d("RemoteConfigSourceImpl", "getUserFreeTrialAndImageLimit: Exception $e")
            close(e)
            e.printStackTrace()
        }
        awaitClose { }
    }

    override suspend fun decrementUserFreeTrialCount(androidId: String) {
        try {
            remoteConfig.collection("users").document(androidId)
                .update("freeTrialCount", FieldValue.increment(-1)).addOnSuccessListener {
                    Log.d("RemoteConfigSourceImpl", "decrementUserFreeTrialCount: Success")
                }.addOnFailureListener {
                    Log.d("RemoteConfigSourceImpl", "decrementUserFreeTrialCount: Failed $it")
                    it.printStackTrace()
                }
        } catch (e: Exception) {
            Log.d("RemoteConfigSourceImpl", "decrementUserFreeTrialCount: Failed $e")
            e.printStackTrace()
        }
    }


    override suspend fun decrementUserImageFreeTrialCount(androidId: String) {
        Log.d("RemoteConfigSourceImpl", "decrementUserImageFreeTrialCount: $androidId")
        try {
            remoteConfig.collection("users").document(androidId)
                .get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val currentPurchase =
                            documentSnapshot.toObject(CurrentUserPurchase::class.java)
                        if (currentPurchase != null && currentPurchase.imageLimit > 0) {
                            remoteConfig.collection("users").document(androidId)
                                .update("imageLimit", FieldValue.increment(-1))
                                .addOnSuccessListener {
                                    Log.d(
                                        "RemoteConfigSourceImpl",
                                        "decrementUserImageFreeTrialCount: Success"
                                    )
                                }.addOnFailureListener {
                                    Log.d(
                                        "RemoteConfigSourceImpl",
                                        "decrementUserImageFreeTrialCount: Failed $it"
                                    )
                                    it.printStackTrace()
                                }
                        }
                    }
                }.addOnFailureListener {
                    Log.d(
                        "RemoteConfigSourceImpl",
                        "decrementUserImageFreeTrialCount: Failed to fetch document $it"
                    )
                    it.printStackTrace()
                }
        } catch (e: Exception) {
            Log.d("RemoteConfigSourceImpl", "decrementUserImageFreeTrialCount: Exception $e")
            e.printStackTrace()
        }
    }
}