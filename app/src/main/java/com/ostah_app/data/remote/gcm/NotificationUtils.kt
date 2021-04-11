package com.ostah_app.data.remote.gcm

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.ostah_app.R
import com.ostah_app.utiles.ComplexPreferences
import com.ostah_app.utiles.Q
import java.util.*


object NotificationUtils {

    internal var TAG = "NotificationUTils"

    @RequiresApi(Build.VERSION_CODES.O)
    @TargetApi(Build.VERSION_CODES.O)
    fun createChannel(context: Context, id: String, name: String, description: String) {
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val importance = NotificationManager.IMPORTANCE_HIGH

        val mChannel = NotificationChannel(id, name, importance)

        // Configure the notification channel.
        mChannel.description = description // The user-visible description of the channel.

        mChannel.enableLights(true) // Sets the notification light color for notifications posted to this
        mChannel.lightColor = Color.BLUE // channel, if the device supports this feature.

        mChannel.enableVibration(true)
        mChannel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

        mNotificationManager.createNotificationChannel(mChannel)
    }
    fun sendNotification(context: Context, channelId: String, title: String, messageBody: String, pendingIntent: PendingIntent, largeIcon: Bitmap? = null) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
        notificationBuilder.setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setContentIntent(pendingIntent)
        if(largeIcon != null) notificationBuilder.setLargeIcon(largeIcon)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Calendar.getInstance().timeInMillis.toInt(), notificationBuilder.build())
    }




}