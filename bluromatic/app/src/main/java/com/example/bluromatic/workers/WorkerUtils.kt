package com.example.bluromatic.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.bluromatic.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

private const val TAG = "WorkerUtils"

// ⚙️ Các hằng số thông báo (bạn cần có)
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val VERBOSE_NOTIFICATION_CHANNEL_NAME = "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever work starts"
const val NOTIFICATION_TITLE = "WorkRequest Starting"
const val NOTIFICATION_ID = 1

// ⚙️ Đường dẫn output
const val OUTPUT_PATH = "blur_outputs"

/**
 * Hiển thị thông báo khi Work chạy
 */
@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
fun makeStatusNotification(message: String, context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
        val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description

        // Tạo channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannel(channel)
    }

    // Tạo thông báo
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))

    // Hiển thị thông báo
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
}

/**
 * Làm mờ ảnh đơn giản bằng cách giảm rồi phóng to lại kích thước.
 */
@WorkerThread
fun blurBitmap(bitmap: Bitmap, blurLevel: Int): Bitmap {
    val scale = blurLevel * 5
    val input = Bitmap.createScaledBitmap(
        bitmap,
        bitmap.width / scale,
        bitmap.height / scale,
        true
    )
    return Bitmap.createScaledBitmap(input, bitmap.width, bitmap.height, true)
}

/**
 * Ghi Bitmap ra file tạm và trả về Uri.
 */
@Throws(FileNotFoundException::class)
fun writeBitmapToFile(applicationContext: Context, bitmap: Bitmap): Uri {
    val name = "blur-filter-output-${UUID.randomUUID()}.png"
    val outputDir = File(applicationContext.filesDir, OUTPUT_PATH)
    if (!outputDir.exists()) {
        outputDir.mkdirs()
    }

    val outputFile = File(outputDir, name)
    FileOutputStream(outputFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    return Uri.fromFile(outputFile)
}
