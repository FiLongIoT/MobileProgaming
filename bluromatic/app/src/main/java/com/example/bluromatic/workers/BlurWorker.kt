package com.example.bluromatic.workers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.bluromatic.DELAY_TIME_MILLIS
import com.example.bluromatic.KEY_BLUR_LEVEL
import com.example.bluromatic.KEY_IMAGE_URI
import com.example.bluromatic.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "BlurWorker"

class BlurWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        val blurLevel = inputData.getInt(KEY_BLUR_LEVEL, 1)

        makeStatusNotification(
            applicationContext.resources.getString(R.string.blurring_image),
            applicationContext
        )

        return withContext(Dispatchers.IO) {

            // Giả lập tác vụ chạy chậm để test WorkManager
            delay(DELAY_TIME_MILLIS)

            return@withContext try {
                // Kiểm tra URI hợp lệ
                require(!resourceUri.isNullOrBlank()) {
                    val errorMessage =
                        applicationContext.resources.getString(R.string.invalid_input_uri)
                    Log.e(TAG, errorMessage)
                    errorMessage
                }

                // Kiểm tra quyền truy cập bộ nhớ (nếu cần)
                if (ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.e(TAG, "Permission READ_EXTERNAL_STORAGE not granted.")
                    return@withContext Result.failure()
                }

                val resolver = applicationContext.contentResolver

                // Thử mở InputStream, có thể bị từ chối quyền
                val picture = try {
                    val inputStream = resolver.openInputStream(Uri.parse(resourceUri))
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: SecurityException) {
                    Log.e(TAG, "Không có quyền truy cập ảnh: $resourceUri", e)
                    return@withContext Result.failure()
                } catch (e: Exception) {
                    Log.e(TAG, "Lỗi khi đọc ảnh từ URI: $resourceUri", e)
                    return@withContext Result.failure()
                }

                // Làm mờ ảnh theo cấp độ người dùng chọn
                val output = blurBitmap(picture, blurLevel)

                // Ghi bitmap ra file tạm
                val outputUri = writeBitmapToFile(applicationContext, output)

                // Trả dữ liệu kết quả
                val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

                Result.success(outputData)
            } catch (throwable: Throwable) {
                Log.e(
                    TAG,
                    applicationContext.resources.getString(R.string.error_applying_blur),
                    throwable
                )
                Result.failure()
            }
        }
    }
}
