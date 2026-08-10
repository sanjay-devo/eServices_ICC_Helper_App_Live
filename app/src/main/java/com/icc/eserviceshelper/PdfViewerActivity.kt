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
    private var currentPdfUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentPdfUrl = intent.getStringExtra("PDF_URL")
        val title = intent.getStringExtra("TITLE")
        val categoryTitle = intent.getStringExtra("CATEGORY_TITLE")

        binding.toolbar.title = title ?: "PDF Viewer"
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.layoutFabApply.fabApply.setOnClickListener {
            val applyIntent = Intent(this, ApplyActivity::class.java)
            applyIntent.putExtra("SERVICE_TITLE", categoryTitle)
            applyIntent.putExtra("SUBSERVICE_TITLE", title)
            startActivity(applyIntent)
        }

        binding.btnRetry.setOnClickListener {
            currentPdfUrl?.let { downloadAndLoadPdf(it) }
        }

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
            showError("Failed to initialize PDF viewer: ${e.message}")
            return
        }

        setupWebView()

        if (currentPdfUrl != null) {
            downloadAndLoadPdf(currentPdfUrl!!)
        } else {
            showError("Invalid PDF URL")
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
                    showError("PDF Error: $error")
                }
            }

            @JavascriptInterface
            fun onDocumentReady() {
                runOnUiThread {
                    hideLoading()
                }
            }
        }, "Android")

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request!!.url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                isViewerLoaded = true
                pendingPdfUrl?.let {
                    loadPdfInWebView(it)
                    pendingPdfUrl = null
                }
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                if (request?.isForMainFrame == true) {
                    showError("Failed to load PDF viewer system")
                }
            }
        }

        // Use the domain-based URL for AssetLoader
        webView.loadUrl("https://appassets.androidplatform.net/assets/pdf_viewer.html")
    }

    private fun downloadAndLoadPdf(url: String) {
        showLoading()
        binding.tvLoadingStatus.text = getString(R.string.status_preparing)
        
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val fileName = urlToFileName(url)
                val localFile = File(pdfCacheDir, fileName)

                if (!localFile.exists()) {
                    downloadFile(url, localFile)
                }

                withContext(Dispatchers.Main) {
                    binding.tvLoadingStatus.text = getString(R.string.status_loading_pages)
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
                    showError("Failed to load PDF: ${e.message}")
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

    private fun showLoading() {
        binding.layoutLoading.visibility = View.VISIBLE
        binding.layoutError.visibility = View.GONE
        binding.pdfWebView.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.layoutLoading.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                binding.layoutLoading.visibility = View.GONE
                binding.layoutLoading.alpha = 1f
                binding.pdfWebView.visibility = View.VISIBLE
                binding.pdfWebView.alpha = 0f
                binding.pdfWebView.animate().alpha(1f).setDuration(300).start()
            }.start()
    }

    private fun showError(message: String) {
        binding.layoutLoading.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.pdfWebView.visibility = View.GONE
        binding.tvErrorMsg.text = message
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
