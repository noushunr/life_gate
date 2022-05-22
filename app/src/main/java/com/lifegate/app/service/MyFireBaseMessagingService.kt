package com.lifegate.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lifegate.app.MainApplication
import com.lifegate.app.R
import com.lifegate.app.data.preferences.PreferenceProvider
import com.lifegate.app.ui.activity.MainActivity
import com.lifegate.app.utils.applyImageUrl

/*
 *Created by Adithya T Raj on 25-07-2021
*/

class MyFireBaseMessagingService : FirebaseMessagingService() {

    private val prefs by lazy {
        PreferenceProvider(MainApplication.appContext)
    }
    private val channelId = "test_channel_id"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {
            sendDataNotification(remoteMessage)
            println("Message data payload: " + remoteMessage.data)
        } else if (remoteMessage.notification != null) {
            sendNotification(remoteMessage)
            println("Message Notification Body: ${remoteMessage.notification?.body}")
        }
    }

    override fun onNewToken(token: String) {
        Log.d("Firebase_",token)
//        prefs.saveFireBaseDeviceToken(token)
    }

    private fun sendDataNotification(messageBody: RemoteMessage) {
        val backIntent = Intent(this, MainActivity::class.java)
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, backIntent,
            PendingIntent.FLAG_ONE_SHOT
        )
         val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(
            applicationContext
        )
        createNotificationChannel(notificationManager)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext, channelId
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(applicationContext.resources.getColor(R.color.lifegate_blue_txt))
            .setContentTitle(messageBody.data["title"].toString())
            .setContentText(messageBody.data["body"].toString())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(messageBody.data["body"].toString()))
            // Add the action button
//                .setDefaults(Notification.DEFAULT_ALL)

        // notificationId is a unique int for each notification that you must define
        val notificationBuilderImage = if (!messageBody.data["img"].isNullOrEmpty()) {
            applyImageUrl(builder, messageBody.data["img"].toString())
        } else {
            null
        }

        if (notificationBuilderImage != null) {
            notificationManager.notify(System.currentTimeMillis().toInt() , notificationBuilderImage.build())
        } else {
            notificationManager.notify(System.currentTimeMillis().toInt() , builder.build())
        }


//        val channelId = "test_channel_id"
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.ic_notification)
//            .setColor(ContextCompat.getColor(applicationContext, R.color.lifegate_blue_txt))
//            .setContentTitle(messageBody.data["title"].toString())
//            .setContentText(messageBody.data["body"].toString())
//            .setPriority(NotificationCompat.PRIORITY_MAX)
//            .setAutoCancel(true)
//            .setDefaults(Notification.DEFAULT_SOUND)
//            .setStyle(NotificationCompat.BigTextStyle()
//                .bigText(messageBody.data["body"].toString()))
//
//        val notificationBuilderImage = if (!messageBody.data["img"].isNullOrEmpty()) {
//            applyImageUrl(notificationBuilder, messageBody.data["img"].toString())
//        } else {
//            null
//        }
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            val channel = NotificationChannel(channelId,
//                "Life & Gate",
//                NotificationManager.IMPORTANCE_HIGH)
//            val attributes = AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                .build()
//            // Configure the notification channel.
//            channel.enableLights(true)
//            channel.enableVibration(true)
//            channel.setSound(defaultSoundUri, attributes) // This is IMPORTANT
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        if (notificationBuilderImage != null) {
//            notificationManager.notify(System.currentTimeMillis().toInt() , notificationBuilderImage.build())
//        } else {
//            notificationManager.notify(System.currentTimeMillis().toInt() , notificationBuilder.build())
//        }
    }

    private fun sendNotification(messageBody: RemoteMessage) {
        val backIntent = Intent(this, MainActivity::class.java)
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, backIntent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(
            applicationContext
        )
        createNotificationChannel(notificationManager)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext, channelId
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(applicationContext.resources.getColor(R.color.lifegate_blue_txt))
            .setContentTitle(messageBody.notification?.title)
            .setContentText(messageBody.notification?.body)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(messageBody.notification?.body))
            // Add the action button
//                .setDefaults(Notification.DEFAULT_ALL)

        // notificationId is a unique int for each notification that you must define
        val img = messageBody.notification?.imageUrl

        val notificationBuilderImage = if (img != null) {
            applyImageUrl(builder, img.toString())
        } else {
            null
        }

        if (notificationBuilderImage != null) {
            notificationManager.notify(System.currentTimeMillis().toInt() , notificationBuilderImage.build())
        } else {
            notificationManager.notify(System.currentTimeMillis().toInt() , builder.build())
        }
//        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())


//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.ic_notification)
//            .setColor(ContextCompat.getColor(applicationContext, R.color.lifegate_blue_txt))
//            .setContentTitle(messageBody.notification?.title)
//            .setContentText(messageBody.notification?.body)
//            .setPriority(NotificationCompat.PRIORITY_MAX)
//            .setAutoCancel(true)
//            .setDefaults(Notification.DEFAULT_SOUND)
//            .setStyle(NotificationCompat.BigTextStyle()
//                .bigText(messageBody.data["body"].toString()))
//
//
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            val channel = NotificationChannel(channelId,
//                "Life & Gate",
//                NotificationManager.IMPORTANCE_HIGH)
//            val attributes = AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                .build()
//            // Configure the notification channel.
//            channel.enableLights(true)
//            channel.enableVibration(true)
//            channel.setSound(defaultSoundUri, attributes) // This is IMPORTANT
//            notificationManager.createNotificationChannel(channel)
//        }


    }

    private fun createNotificationChannel(notificationManager: NotificationManagerCompat) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        val channelName = "Channel Name"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, importance)
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            // Configure the notification channel.
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            mChannel.setSound(defaultSoundUri, attributes) // This is IMPORTANT
            mChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            assert(notificationManager != null)

//            assert(notificationManager != null)
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}