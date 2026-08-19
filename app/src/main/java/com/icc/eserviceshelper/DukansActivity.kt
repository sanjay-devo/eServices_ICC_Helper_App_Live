package com.icc.eserviceshelper

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.icc.eserviceshelper.databinding.ActivityDukansBinding

class DukansActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDukansBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDukansBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWebView()
        setupBackPress()
    }

    private fun setupWebView() {
        binding.webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                builtInZoomControls = true
                displayZoomControls = false
                allowFileAccess = true
                allowContentAccess = true
                setSupportZoom(true)
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                useWideViewPort = true
                loadWithOverviewMode = true
            }
            webViewClient = WebViewClient()
            loadUrl("https://xy.dukans.in")
        }
    }

    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Immediately close this WebView page and return directly to the Home screen
                finish()
            }
        })
    }
}
