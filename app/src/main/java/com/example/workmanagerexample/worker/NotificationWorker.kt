package com.example.workmanagerexample.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.workmanagerexample.R

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context,
    workerParams
) {
    private val CHANNEL_ID="MainActivity"
    private val notificationTitle=inputData.getString("notificationTitle")
    val notification=NotificationCompat.Builder(applicationContext,CHANNEL_ID)
        .setSmallIcon(R.drawable.pic)
        .setContentTitle(notificationTitle)
        .setContentText("This is the text Notification you were implementing")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
       try{
           createNotificationChannel()
           with(NotificationManagerCompat.from(applicationContext)){
               notify(1,notification)
           }
           return Result.success()
       }catch (e:Exception){
           return Result.failure()
       }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(){
        val channel=NotificationChannel(CHANNEL_ID,"Test Notification",NotificationManager.IMPORTANCE_HIGH).apply {
            description="This is MainActivity Test Channel"
        }
        val notificationManager=applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}