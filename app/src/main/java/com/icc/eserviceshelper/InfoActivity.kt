package com.icc.eserviceshelper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.icc.eserviceshelper.databinding.ActivityInfoBinding
import com.icc.eserviceshelper.utils.ContentProvider

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("INFO_TYPE") ?: "ABOUT"
        val content = ContentProvider.getContent(type)
        val title = ContentProvider.getTitle(type)

        binding.toolbar.title = title
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.tvContent.text = content
    }
}