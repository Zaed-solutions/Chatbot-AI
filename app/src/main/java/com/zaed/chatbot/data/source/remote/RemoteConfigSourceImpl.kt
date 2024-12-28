package com.zaed.chatbot.data.source.remote

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.zaed.chatbot.ui.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class RemoteConfigSourceImpl(
    val remoteConfig: FirebaseFirestore
) : RemoteConfigSource {
    override fun getUserFreeTrialCount(androidId: String): Flow<Int> = callbackFlow {
        try {
            remoteConfig.collection("users").document(androidId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        close(e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val freeTrialCount = snapshot.getLong("freeTrialCount")?.toInt() ?: 0
                        trySend(freeTrialCount)
                    } else {
                        remoteConfig.collection("users").document(androidId)
                            .set(mapOf("freeTrialCount" to 5)).addOnSuccessListener {
                                trySend(5)
                            }.addOnFailureListener {
                                close(it)
                            }
                    }
                }
        } catch (e: Exception) {
            close(e)
            e.printStackTrace()
        }
        awaitClose { }
    }


    override suspend fun incrementUserFreeTrialCount(androidId: String) {
        try {
            remoteConfig.collection("users").document(androidId)
                .update("freeTrialCount", FieldValue.increment(-1)).addOnSuccessListener {
                    Log.d("RemoteConfigSourceImpl", "incrementUserFreeTrialCount: Success")
                }.addOnFailureListener {
                    Log.d("RemoteConfigSourceImpl", "incrementUserFreeTrialCount: Failed $it")
                    it.printStackTrace()
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("RemoteConfigSourceImpl", "incrementUserFreeTrialCount: Failed $e")
        }
    }
    override suspend fun decrementUserImageFreeTrialCount(androidId: String) {
        try {
            remoteConfig.collection("users").document(androidId)
                .update("imageSubscribtionCount", FieldValue.increment(-1)).addOnSuccessListener {
                    Log.d("RemoteConfigSourceImpl", "incrementUserFreeTrialCount: Success")
                }.addOnFailureListener {
                    Log.d("RemoteConfigSourceImpl", "incrementUserFreeTrialCount: Failed $it")
                    it.printStackTrace()
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("RemoteConfigSourceImpl", "incrementUserFreeTrialCount: Failed $e")
        }
    }
    override fun getUserImageFreeTrialCount(androidId: String, productId: String): Flow<Int> =
        callbackFlow {
            try {
                remoteConfig.collection("users").document(androidId)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            close(e)
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            val imageFreeTrialCount =
                                snapshot.getLong("imageSubscribtionCount")?.toInt() ?: 0
                            trySend(imageFreeTrialCount)
                        } else {
                            Log.d("tenoo2", "remoteConfig: "+productId)
                            val limit = if (productId == Constants.YEARLY_SUBSCRIPTION_ID) {
                                Constants.YEARLY_SUBSCRIPTION_IMAGE_GENERATION_LIMIT
                            } else {
                                Constants.MONTHLY_SUBSCRIPTION_IMAGE_GENERATION_LIMIT
                            }
                            remoteConfig.collection("users").document(androidId)
                                .set(mapOf("imageSubscribtionCount" to limit))
                                .addOnSuccessListener {
                                    trySend(limit)
                                }.addOnFailureListener {
                                    close(it)
                                }
                        }
                    }
            } catch (e: Exception) {
                close(e)
                e.printStackTrace()
            }
            awaitClose { }
        }
}