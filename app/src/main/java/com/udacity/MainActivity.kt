package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.utils.cancelNotifications
import com.udacity.utils.sendNotification
import com.udacity.utils.sendNotificationWithAction
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.concurrent.thread
import kotlin.math.abs

private const val REPO_URL =
    "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"
private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }
        url_et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (Patterns.WEB_URL.matcher(url_et.text.toString()).matches()) {
//                    Toast.makeText(this, "Pattern Matches", Toast.LENGTH_SHORT).show();
                    URL = url_et.text.toString()
                } else {
                    // otherwise show error of invalid url
                    url_et.error = "Invalid Url";
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        //to show download completion
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        //to indicate the download start
        createChannel(
            getString(R.string.notification_channel_id_low),
            getString(R.string.notification_channel_name_low),
            NotificationManager.IMPORTANCE_LOW
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            id?.let {
                notificationManager = ContextCompat.getSystemService(
                    applicationContext,
                    NotificationManager::class.java
                ) as NotificationManager

                notificationManager.cancelNotifications()
                notificationManager.sendNotificationWithAction(
                    "Your files has been downloaded from '$URL'",
                    id,
                    applicationContext
                )

            }
        }
    }


    private fun download() {
        if (URL.isNotEmpty()) {
            notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.sendNotification(
                "Downloading from '$URL' ...",
                "Downloading",
                applicationContext
            )
            custom_button.animateProgress()
            customCircleProgress.animateProgress()
            val request =
                DownloadManager.Request(Uri.parse(URL))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request) // enqueue puts the download request in the queue.
          // get download percentage
            thread {
                var downloading = true
                while (downloading) {
                    val query = DownloadManager.Query()
                    query.setFilterById(downloadID)

                    val cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        val bytesDownloaded =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        val bytesTotal =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false
                        }

                        val progress = ((bytesDownloaded * 100) / bytesTotal).toFloat()
                        runOnUiThread {
//                            custom_button.setProgress(abs(progress)%100)
                            println("progress: $progress")
                        }
                        cursor.close()
                    }
                }
            }
        } else {
            Toast.makeText(
                this,
                applicationContext.getString(R.string.download_text),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    companion object {
        private var URL = ""
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.glide_radioButton ->
                    if (checked) {
                        URL = GLIDE_URL
                    }
                R.id.loadApp_radioButton ->
                    if (checked) {
                        URL = REPO_URL
                    }
                R.id.retrofit_radioButton ->
                    if (checked) {
                        URL = RETROFIT_URL
                    }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String, importanceLevel: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                importanceLevel
            )
                .apply {
                    setShowBadge(false)
                }
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(notificationChannel)

        }
    }
}
