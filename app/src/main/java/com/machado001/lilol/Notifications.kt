package com.machado001.lilol

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.ComponentActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.machado001.lilol.rotation.view.activity.RotationActivity


class MyNotification(private val ctx: Context) {
    companion object {
        const val ROTATION_CHANNEL_ID = "ROTATION_CHANNEL"
    }

    private val intent = Intent(ctx, RotationActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    private val pendingIntent: PendingIntent =
        PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    var builder = NotificationCompat.Builder(ctx, ROTATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.baseline_360_24)
        .setContentTitle(ctx.resources.getString(R.string.new_rotation_notification_title))
        .setContentText(ctx.resources.getString(R.string.new_rotation_notification_desc))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)

    private fun createNotificationChannel() {
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

    fun showNotification() = with(NotificationManagerCompat.from(ctx)) {
        createNotificationChannel()
        // notificationId is a unique int for each notification that you must define.
        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notify(12, builder.build())
    }
}