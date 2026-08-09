package com.icc.eserviceshelper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.icc.eserviceshelper.adapters.InfoAdapter
import com.icc.eserviceshelper.databinding.ActivityInfoBinding
import com.icc.eserviceshelper.models.InfoItem
import com.icc.eserviceshelper.utils.ContentProvider

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("INFO_TYPE") ?: "ABOUT"
        val title = ContentProvider.getTitle(type)
        val items = ContentProvider.getInfoItems(type)

        binding.toolbar.title = title
        binding.toolbar.setNavigationOnClickListener { finish() }

        if (type == "ORGANIZATION") {
            binding.btnLogin.visibility = android.view.View.VISIBLE
            binding.btnLogin.setOnClickListener {
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
            }
            binding.btnLogin.setOnLongClickListener {
                startActivity(Intent(this, PDFGeneratorActivity::class.java))
                true
            }
        }

        setupRecyclerView(items)
    }

    private fun setupRecyclerView(items: List<InfoItem>) {
        val adapter = InfoAdapter(items) { item ->
            when (item) {
                is InfoItem.Contact -> handleContactAction(item)
                is InfoItem.Link -> openUrl(item.url)
                else -> {}
            }
        }
        binding.recyclerViewInfo.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewInfo.adapter = adapter
    }

    private fun handleContactAction(contact: InfoItem.Contact) {
        when (contact.actionType) {
            InfoItem.ActionType.EMAIL -> sendEmail(contact.value)
            InfoItem.ActionType.PHONE -> callPhone(contact.value)
            InfoItem.ActionType.WEB -> openUrl(contact.value)
            InfoItem.ActionType.MAP -> openMap(contact.value)
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail(email: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, "Query - eServices: India Cyber Cafe")
            }
            startActivity(Intent.createChooser(intent, "Send Email"))
        } catch (e: Exception) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun callPhone(phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open dialer", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openMap(address: String) {
        try {
            val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(address)}"))
                startActivity(intent)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open map", Toast.LENGTH_SHORT).show()
        }
    }
}