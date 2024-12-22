package com.machado001.lilol

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.machado001.lilol.rotation.view.activity.RotationActivity


class MyNotification(private val ctx: Context) {
    companion object {
        const val ROTATION_CHANNEL_ID = "ROTATION_CHANNEL"
        const val ROTATION_NOTIFICATION_ID = 12
    }

    private val intent = Intent(ctx, RotationActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    private val pendingIntent: PendingIntent =
        PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    private var builder = NotificationCompat.Builder(ctx, ROTATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.baseline_360_24)
        .setContentTitle(ctx.resources.getString(R.string.new_rotation_notification_title))
        .setContentText(ctx.resources.getString(R.string.new_rotation_notification_desc))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)

    fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = ctx.resources.getString(R.string.error_request)
            val descriptionText = ctx.resources.getString(R.string.error_request)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(ROTATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun showNotification() = with(NotificationManagerCompat.from(ctx)) {
        notify(ROTATION_NOTIFICATION_ID, builder.build())
    }
}