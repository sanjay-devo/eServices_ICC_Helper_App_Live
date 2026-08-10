package com.icc.eserviceshelper

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.icc.eserviceshelper.adapters.TopicAdapter
import com.icc.eserviceshelper.databinding.ActivityTopicsBinding
import com.icc.eserviceshelper.models.Category

class TopicsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopicsBinding
    private lateinit var adapter: TopicAdapter
    private var currentCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentCategory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("CATEGORY", Category::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Category>("CATEGORY")
        }
        
        binding.toolbar.title = currentCategory?.title ?: "Topics"
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupApplyFab()

        val topics = currentCategory?.items?.values?.toList() ?: emptyList()

        adapter = TopicAdapter(topics) { topic ->
            val intent = Intent(this, PdfViewerActivity::class.java)
            intent.putExtra("PDF_URL", topic.pdf_url)
            intent.putExtra("TITLE", topic.title)
            intent.putExtra("CATEGORY_TITLE", currentCategory?.title)
            startActivity(intent)
        }
        binding.recyclerViewTopics.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTopics.adapter = adapter
    }

    private fun setupApplyFab() {
        binding.layoutFabApply.fabApply.setOnClickListener {
            val intent = Intent(this, ApplyActivity::class.java)
            intent.putExtra("SERVICE_TITLE", currentCategory?.title)
            startActivity(intent)
        }
    }
}
