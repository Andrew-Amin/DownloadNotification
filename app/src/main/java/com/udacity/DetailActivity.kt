package com.udacity

import android.app.DownloadManager
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    var downloadID: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val intentResults = intent.extras
        val msg = intentResults?.getString("msg") ?: "No Data"
        downloadID = intentResults?.getLong("id")

        file_name_tv.text = msg
        getDownloadData()

    }

    private fun getDownloadData() {
        downloadID?.let {
            val query = DownloadManager.Query()

            query.setFilterById(downloadID!!)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val cursor = downloadManager.query(query)
            val uri = downloadManager.getUriForDownloadedFile(downloadID!!)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val url = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI))
                val localUrl =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                val total =
                    cursor.getFloat(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        .div(1000)
                val downloaded =
                    cursor.getFloat(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        .div(1000)
                val type =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))
                val name =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                val timestamp =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP))
                        .toLong()
                val date = getDate(timestamp)
                val reason = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                download_file_name_tv.text = name
                ("Download URL: $url\n\n" +
                        "Local path: $localUrl\n\n" +
                        "File type: $type\n\n" +
                        "Date: $date\n\n" +
                        "Error reason: $reason\n\n" +
                        "Query: ${uri.query}\n\n" +
                        "Total file size: $total KB\n\n" +
                        "Total downloaded bytes: $downloaded KB\n\n" +
                        "Host: ${uri.host}").also { details_tv.text = it }

                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        download_status_tv.text = getString(R.string.succeed)
                        download_status_tv.setTextColor(getColor(R.color.colorPrimary))

                    }
                    DownloadManager.STATUS_FAILED -> {
                        download_status_tv.text = getString(R.string.failed)
                        download_status_tv.setTextColor(Color.RED)


                    }
                    else -> {
                        download_status_tv.text = getString(R.string.downloading)
                    }

                }
            }
        }
    }

    fun navigateBackToMain(view: View) {
        if (view.id == R.id.details_ok_btn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

//    fun openDownloadedFile(view: View) {
//        if (view.id == R.id.details_ok_btn) {
//            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//            val parcelFileDescriptor = downloadManager.openDownloadedFile(downloadID!!)
//        }
//    }

    fun getDate(timestamp: Long): String {
        val df: DateFormat = SimpleDateFormat("YYYY-MM-DD hh:mm a", Locale.US)
        return df.format(timestamp)
    }

}
