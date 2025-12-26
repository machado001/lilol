package com.machado001.lilol

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.machado001.lilol.rotation.model.repository.RotationRepository
import com.machado001.lilol.rotation.view.activity.RotationActivity
import kotlinx.coroutines.runBlocking

class RiotMessagingService : FirebaseMessagingService() {

    private val appContainer by lazy { (application as Application).container }
    private val rotationRepository: RotationRepository by lazy { appContainer.rotationRepository }
    private val functions: FirebaseFunctions by lazy { FirebaseFunctions.getInstance() }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val type = data["type"]

        if (type != ROTATION_TYPE) {
            return
        }

        val incomingSignature = data["signature"]
        if (incomingSignature.isNullOrBlank()) {
            Log.w(TAG, "Rotation update message missing signature")
            return
        }

        val localSignature = runBlocking { rotationRepository.getLocalSignature() }
        val isOutdated = localSignature == null || localSignature != incomingSignature

        if (!isOutdated) return

        fetchRotationAndNotify()
    }

    private fun fetchRotationAndNotify() {
        functions
            .getHttpsCallable("championRotation")
            .call()
            .addOnSuccessListener { result ->
                val payload = result.data as? Map<*, *>
                if (payload == null) {
                    Log.w(TAG, "championRotation returned null/non-map payload")
                    return@addOnSuccessListener
                }
                runBlocking {
                    rotationRepository.updateRotationFromPayload(payload)
                }
                showRotationNotification()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to fetch rotation after FCM update", e)
            }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showRotationNotification() {
        val channelId = "riot_rotation_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Champion rotation updates",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, RotationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.lilol_icon)
            .setContentTitle(getString(R.string.new_rotation_notification_title))
            .setContentText(getString(R.string.new_rotation_notification_desc))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(ROTATION_NOTIFICATION_ID, notification)
    }

    companion object {
        private const val ROTATION_TYPE = "riot_rotation_update"
        private const val ROTATION_NOTIFICATION_ID = 1002
        private const val TAG = "RiotMessagingService"
    }
}
