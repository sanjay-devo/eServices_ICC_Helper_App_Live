package com.icc.eserviceshelper

import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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
    private var pdfRenderer: PdfRenderer? = null
    private var parcelFileDescriptor: ParcelFileDescriptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pdfUrl = intent.getStringExtra("PDF_URL")
        val title = intent.getStringExtra("TITLE")

        binding.toolbar.title = title ?: "PDF Viewer"
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.pdfRecyclerView.layoutManager = LinearLayoutManager(this)

        if (pdfUrl != null) {
            loadPdf(pdfUrl)
        } else {
            Toast.makeText(this, "Invalid PDF URL", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadPdf(url: String) {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val fileName = urlToFileName(url)
                val localFile = File(cacheDir, fileName)

                if (!localFile.exists()) {
                    downloadFile(url, localFile)
                }

                withContext(Dispatchers.Main) {
                    displayPdfFromFile(localFile)
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

    private fun displayPdfFromFile(file: File) {
        try {
            parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(parcelFileDescriptor!!)
            
            val adapter = PdfPageAdapter(pdfRenderer!!)
            binding.pdfRecyclerView.adapter = adapter
            
            binding.progressBar.visibility = View.GONE
        } catch (e: Exception) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Error displaying PDF: ${e.message}", Toast.LENGTH_LONG).show()
            if (file.exists()) file.delete()
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

    override fun onDestroy() {
        super.onDestroy()
        pdfRenderer?.close()
        parcelFileDescriptor?.close()
    }
}
