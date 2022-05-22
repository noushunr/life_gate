package com.lifegate.app.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Build.MANUFACTURER
import android.os.Build.MODEL
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.lifegate.app.MainApplication
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import okhttp3.RequestBody
import java.io.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.MediaType
import okio.BufferedSink


/*
 *Created by Adithya T Raj on 24-06-2021
*/

object CoRoutines {

    fun main(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            work()
        }

    fun io(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.IO).launch {
            work()
        }

}

@Throws(JsonSyntaxException::class)
inline fun <reified T> fromJson(json: String): T {
    return Gson().fromJson(json, object: TypeToken<T>(){}.type)
}

fun hasNetwork(): Boolean {
    val context = MainApplication.appContext
    var isConnected = false // Initial Value
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        connectivityManager?.let {
            it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                isConnected = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
        }
    } else {
        val activeNetwork: NetworkInfo? = connectivityManager?.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
    }
    return isConnected
}

//@SuppressLint("DefaultLocale")
//fun getDeviceName(): String =
//    (if (MODEL.startsWith(MANUFACTURER, ignoreCase = true)) {
//        "$MODEL (API $SDK_INT)"
//    } else {
//        "$MANUFACTURER $MODEL (API $SDK_INT)"
//    }).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

fun applyImageUrl(builder: NotificationCompat.Builder, imageUrl: String) = runBlocking {

    withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val input = url.openStream()
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            null
        }
    }?.let { bitmap ->
        builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
    }
}

fun getImageBitMap(encodedString: String) : Bitmap? {
    try {
        val byteArray = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    } catch (e: Exception) {
        return null
    }
}

fun setYearMonthDateTimeStamp(date: String): Date? {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    var newDate :Date? = null
    try {
        newDate = dateFormat.parse(date)
    } catch (e: Exception) {
        ////println(e.message)
    }
    return newDate
}

fun setDateMonth(date: String): String {
    var formattedString = date
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var newDate :Date? = null
    try {
        newDate = dateFormat.parse(date)
        formattedString = SimpleDateFormat("dd MMMM", Locale.getDefault()).format(newDate!!)
    } catch (e: Exception) {
        ////println(e.message)
    }
    return formattedString
}

fun setMonthDateYear(date: String): String {
    var formattedString = date
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var newDate :Date? = null
    try {
        newDate = dateFormat.parse(date)
        formattedString = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(newDate!!)
    } catch (e: Exception) {
        ////println(e.message)
    }
    return formattedString
}

fun getTime(date: Date): String =
    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)

fun getFormattedDate(date: Date): String =
    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)

fun getRelativeTop(myView: View): Int {
    return if (myView.parent === myView.rootView) {
        myView.top
    } else {
        myView.top + getRelativeTop(myView.parent as View)
    }
}

enum class ScaleTypes(value: String) {
    FIT("fit"), CENTER_CROP("centerCrop"), CENTER_INSIDE("centerInside")
}

fun setDateMonthYear(date: String): String {
    var formattedString = date
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    var newDate :Date? = null
    try {
        newDate = dateFormat.parse(date)
        formattedString = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(newDate!!)
    } catch (e: Exception) {
        ////println(e.message)
    }
    return formattedString
}

fun String?.clearHtmlTag(): Spanned? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}

fun setDayMonthDate(date: String): String {
    var formattedString = date
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var newDate :Date? = null
    try {
        newDate = dateFormat.parse(date)
        formattedString = SimpleDateFormat("E, MMMM d", Locale.getDefault()).format(newDate!!)
    } catch (e: Exception) {
        ////println(e.message)
    }
    return formattedString
}

fun setMonthDate(date: String): String {
    var formattedString = date
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var newDate :Date? = null
    try {
        newDate = dateFormat.parse(date)
        formattedString = SimpleDateFormat("MMM d,yyyy", Locale.getDefault()).format(newDate!!)
    } catch (e: Exception) {
        ////println(e.message)
    }
    return formattedString
}



object FilesObj {

    private fun tempImageDirectory(context: Context): File {
        val privateTempDir = File(context.cacheDir, "EasyImage")
        if (!privateTempDir.exists()) privateTempDir.mkdirs()
        return privateTempDir
    }

    private fun generateFileName(): String {
        return "ei_${System.currentTimeMillis()}"
    }

    private fun writeToFile(inputStream: InputStream, file: File) {
        try {
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var length: Int = inputStream.read(buffer)
            while (length > 0) {
                outputStream.write(buffer, 0, length)
                length = inputStream.read(buffer)
            }
            outputStream.close()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun copyFile(src: File, dst: File) {
        val inputStream = FileInputStream(src)
        writeToFile(inputStream, dst)
    }

    internal fun copyFilesInSeparateThread(context: Context, folderName: String, filesToCopy: List<File>) {
        Thread(Runnable {
            val copiedFiles = ArrayList<File>()
            var i = 1
            for (fileToCopy in filesToCopy) {
                val dstDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folderName)
                if (!dstDir.exists()) dstDir.mkdirs()

                val filenameSplit = fileToCopy.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val extension = "." + filenameSplit[filenameSplit.size - 1]
                val datePart = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Calendar.getInstance().time)
                val filename = "IMG_${datePart}_$i.$extension%d.%s"
                val dstFile = File(dstDir, filename)
                try {
                    dstFile.createNewFile()
                    copyFile(fileToCopy, dstFile)
                    copiedFiles.add(dstFile)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                i++
            }
            scanCopiedImages(context, copiedFiles)
        }).run()
    }

    private fun scanCopiedImages(context: Context, copiedImages: List<File>) {
        val paths = arrayOfNulls<String>(copiedImages.size)
        for (i in copiedImages.indices) {
            paths[i] = copiedImages[i].toString()
        }

        MediaScannerConnection.scanFile(context,
            paths, null,
            object : MediaScannerConnection.OnScanCompletedListener {
                override fun onScanCompleted(path: String, uri: Uri) {
                    Log.d(javaClass.simpleName, "Scanned $path:")
                    Log.d(javaClass.simpleName, "-> uri=$uri")
                }
            })
    }

    @Throws(IOException::class)
    internal fun pickedExistingPicture(context: Context, photoUri: Uri): File {
        val pictureInputStream = context.contentResolver.openInputStream(photoUri)
        val directory = tempImageDirectory(context)
        val photoFile = File(directory, generateFileName() + "." + getMimeType(context, photoUri))
        photoFile.createNewFile()
        writeToFile(pictureInputStream!!, photoFile)
        return photoFile
    }

    /**
     * To find out the extension of required object in given uri
     * Solution by http://stackoverflow.com/a/36514823/1171484
     */
    private fun getMimeType(context: Context, uri: Uri): String? {
        val extension: String?

        //Check uri format to avoid null
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            extension = mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }

        return extension
    }

    private fun getUriToFile(context: Context, file: File): Uri {
        val packageName = context.applicationContext.packageName
        val authority = "$packageName.easyphotopicker.fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }
}

class UploadRequestBody(
    private val file: File,
    private val contentType: String,
    private val callback: UploadCallback? = null
) : RequestBody() {

    override fun contentType() = MediaType.parse("$contentType/*")

    override fun contentLength() = file.length()

    override fun writeTo(sink: BufferedSink) {
        val length = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(file)
        var uploaded = 0L
        fileInputStream.use { inputStream ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (inputStream.read(buffer).also { read = it } != -1) {
                handler.post(ProgressUpdater(uploaded, length))
                uploaded += read
                sink.write(buffer, 0, read)
            }
        }
    }

    interface UploadCallback {
        fun onProgressUpdate(percentage: Int)
    }

    inner class ProgressUpdater(
        private val uploaded: Long,
        private val total: Long
    ) : Runnable {
        override fun run() {
            callback?.onProgressUpdate((100 * uploaded / total).toInt())
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }
}