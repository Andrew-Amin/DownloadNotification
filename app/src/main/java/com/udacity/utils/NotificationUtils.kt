package com.udacity.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun NotificationManager.sendNotificationWithAction(
    messageBody: String,
    id: Long? ,
    applicationContext: Context
) {
    // Create the content intent for the notification, which launches
    // DetailActivity
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra("msg" , messageBody)
    contentIntent.putExtra("id" , id)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val detailsActionIntent = Intent(applicationContext, DetailActivity::class.java)
    val detailsPendingIntent: PendingIntent = PendingIntent.getActivity(
        applicationContext,
        REQUEST_CODE,
        detailsActionIntent,
        FLAGS
    )

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(
            applicationContext
                .getString(R.string.notification_title)
        )
        .setContentText(messageBody)

        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)

        .addAction(
            R.drawable.ic_info,
            applicationContext.getString(R.string.details),
            detailsPendingIntent
        )

        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.sendNotification(
    messageBody: String,
    status: String = "",
    applicationContext: Context
) {
    // Create the content intent for the notification, which launches
    // DetailActivity
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra("msg" , messageBody)
    contentIntent.putExtra("status" , status)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )


    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id_low)
    )
        .setSmallIcon(R.drawable.ic_cloud_download)
        .setContentTitle(
            applicationContext
                .getString(R.string.notification_title)
        )
        .setContentText(messageBody)

        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)

        .setPriority(NotificationCompat.PRIORITY_LOW)
    notify(NOTIFICATION_ID, builder.build())
}


/**
 * Cancels all notifications.
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}