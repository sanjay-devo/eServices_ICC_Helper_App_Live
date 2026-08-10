package com.icc.eserviceshelper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.webkit.WebViewAssetLoader
import com.icc.eserviceshelper.databinding.ActivityPdfViewerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.security.MessageDigest

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewerBinding
    private var isViewerLoaded = false
    private var pendingPdfUrl: String? = null
    private lateinit var assetLoader: WebViewAssetLoader
    private lateinit var pdfCacheDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pdfUrl = intent.getStringExtra("PDF_URL")
        val title = intent.getStringExtra("TITLE")

        binding.toolbar.title = title ?: "PDF Viewer"
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Handle back button using OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.pdfWebView.canGoBack()) {
                    binding.pdfWebView.goBack()
                } else {
                    finish()
                }
            }
        })

        // Use filesDir instead of cacheDir as InternalStoragePathHandler is more strict on some devices
        pdfCacheDir = File(filesDir, "pdf_cache")
        if (!pdfCacheDir.exists()) {
            pdfCacheDir.mkdirs()
        }

        try {
            assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
                .addPathHandler("/pdf_cache/", WebViewAssetLoader.InternalStoragePathHandler(this, pdfCacheDir))
                .build()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to initialize PDF viewer: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupWebView()

        if (pdfUrl != null) {
            downloadAndLoadPdf(pdfUrl)
        } else {
            Toast.makeText(this, "Invalid PDF URL", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupWebView() {
        val webView = binding.pdfWebView

        webView.settings.apply {
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

        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun onLinkClick(url: String) {
                runOnUiThread {
                    handleUri(Uri.parse(url))
                }
            }

            @JavascriptInterface
            fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@PdfViewerActivity, "PDF Error: $error", Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }, "Android")

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request!!.url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                isViewerLoaded = true
                binding.progressBar.visibility = View.GONE
                pendingPdfUrl?.let {
                    loadPdfInWebView(it)
                    pendingPdfUrl = null
                }
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                if (request?.isForMainFrame == true) {
                    Toast.makeText(this@PdfViewerActivity, "Failed to load PDF viewer", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        // Use the domain-based URL for AssetLoader
        webView.loadUrl("https://appassets.androidplatform.net/assets/pdf_viewer.html")
    }

    private fun downloadAndLoadPdf(url: String) {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val fileName = urlToFileName(url)
                val localFile = File(pdfCacheDir, fileName)

                if (!localFile.exists()) {
                    downloadFile(url, localFile)
                }

                // The path for AssetLoader - matches the "/pdf_cache/" handler
                val internalUrl = "https://appassets.androidplatform.net/pdf_cache/$fileName"
                
                withContext(Dispatchers.Main) {
                    if (isViewerLoaded) {
                        loadPdfInWebView(internalUrl)
                    } else {
                        pendingPdfUrl = internalUrl
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@PdfViewerActivity, "Failed to load PDF: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun downloadFile(url: String, destination: File) {
        val inputStream = URL(url).openStream()
        val outputStream = FileOutputStream(destination)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun urlToFileName(url: String): String {
        return try {
            val bytes = MessageDigest.getInstance("MD5").digest(url.toByteArray())
            bytes.joinToString("") { "%02x".format(it) } + ".pdf"
        } catch (e: Exception) {
            url.filter { it.isLetterOrDigit() }.takeLast(20) + ".pdf"
        }
    }

    private fun loadPdfInWebView(internalUrl: String) {
        binding.pdfWebView.evaluateJavascript("loadPdf('$internalUrl')", null)
    }

    private fun handleUri(uri: Uri): Boolean {
        val urlString = uri.toString()
        if (urlString.isBlank()) return false

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        return try {
            startActivity(intent)
            true
        } catch (e: Exception) {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
                startActivity(browserIntent)
                true
            } catch (ex: Exception) {
                Toast.makeText(this, "No application found for this link", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    override fun onDestroy() {
        binding.pdfWebView.destroy()
        super.onDestroy()
    }
}
