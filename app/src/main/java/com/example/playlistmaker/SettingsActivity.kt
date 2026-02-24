package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            val settingsIntent = Intent(this, MainActivity::class.java)
            startActivity(settingsIntent)
        }

        val shareFrame = findViewById<FrameLayout>(R.id.shareFrame)

        shareFrame.setOnClickListener {
            shareApp()
        }

        val supportFrame = findViewById<FrameLayout>(R.id.supportFrame)

        supportFrame.setOnClickListener {
            val messageSupport = getString(R.string.message_support)
            val messageTitle = getString(R.string.message_title)
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(EmailConstants.SUPPORT_ADDRESS))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, messageTitle)
            supportIntent.putExtra(Intent.EXTRA_TEXT, messageSupport)
            startActivity(supportIntent)
        }

        val agreementFrame = findViewById<FrameLayout>(R.id.agreementFrame)

        agreementFrame.setOnClickListener {
            val url = Uri.parse(getString(R.string.offer_url))
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
        }

        val chooser = Intent.createChooser(shareIntent, getString(R.string.sett_share))
        startActivity(chooser)
    }
}