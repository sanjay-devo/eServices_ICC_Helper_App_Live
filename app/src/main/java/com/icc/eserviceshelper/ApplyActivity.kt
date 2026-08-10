package com.icc.eserviceshelper

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.icc.eserviceshelper.databinding.ActivityApplyBinding
import com.icc.eserviceshelper.models.Category
import com.icc.eserviceshelper.models.ServiceItem
import com.icc.eserviceshelper.repository.FirebaseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ApplyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplyBinding
    private val repository = FirebaseRepository()
    
    private var allCategories: List<Category> = emptyList()
    private var selectedCategory: Category? = null
    private var selectedServiceItem: ServiceItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadDataAndSetupSpinners()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadDataAndSetupSpinners() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val result = repository.getCategories().first()
                allCategories = result.getOrNull() ?: emptyList()
                
                setupServiceSpinner()
                handleIntents()
                
            } catch (e: Exception) {
                Toast.makeText(this@ApplyActivity, "Failed to load services", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun setupServiceSpinner() {
        val serviceTitles = allCategories.map { it.title }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, serviceTitles)
        binding.actvService.setAdapter(adapter)

        binding.actvService.setOnItemClickListener { _, _, position, _ ->
            val category = allCategories[position]
            onServiceSelected(category)
        }
    }

    private fun onServiceSelected(category: Category, preselectSubserviceTitle: String? = null) {
        selectedCategory = category
        selectedServiceItem = null
        binding.actvSubservice.setText("")
        
        val subservices = category.items?.values?.toList() ?: emptyList()
        val subserviceTitles = subservices.map { it.title }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subserviceTitles)
        binding.actvSubservice.setAdapter(adapter)

        if (preselectSubserviceTitle != null) {
            val index = subservices.indexOfFirst { it.title == preselectSubserviceTitle }
            if (index != -1) {
                selectedServiceItem = subservices[index]
                binding.actvSubservice.setText(preselectSubserviceTitle, false)
            }
        }

        binding.actvSubservice.setOnItemClickListener { _, _, position, _ ->
            selectedServiceItem = subservices[position]
        }
    }

    private fun handleIntents() {
        val intentServiceTitle = intent.getStringExtra("SERVICE_TITLE")
        val intentSubserviceTitle = intent.getStringExtra("SUBSERVICE_TITLE")

        if (intentServiceTitle != null) {
            val category = allCategories.find { it.title == intentServiceTitle }
            if (category != null) {
                binding.actvService.setText(intentServiceTitle, false)
                onServiceSelected(category, intentSubserviceTitle)
            }
        }
    }

    private fun setupListeners() {
        binding.btnSubmitApply.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun validateAndSubmit() {
        val name = binding.etName.text.toString().trim()
        val mobile = binding.etMobile.text.toString().trim()
        val service = binding.actvService.text.toString()
        val subservice = binding.actvSubservice.text.toString()

        var isValid = true

        if (service.isEmpty()) {
            binding.tilService.error = getString(R.string.error_invalid_service)
            isValid = false
        } else {
            binding.tilService.error = null
        }

        if (subservice.isEmpty()) {
            binding.tilSubservice.error = getString(R.string.error_invalid_subservice)
            isValid = false
        } else {
            binding.tilSubservice.error = null
        }

        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.error_invalid_name)
            isValid = false
        } else {
            binding.tilName.error = null
        }

        if (mobile.length != 10) {
            binding.tilMobile.error = getString(R.string.error_invalid_mobile)
            isValid = false
        } else {
            binding.tilMobile.error = null
        }

        if (isValid) {
            submitOrder(service, subservice, name, mobile)
        }
    }

    private fun submitOrder(service: String, subservice: String, name: String, mobile: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSubmitApply.isEnabled = false

        lifecycleScope.launch {
            val result = repository.applyForService(service, subservice, name, mobile)
            binding.progressBar.visibility = View.GONE
            binding.btnSubmitApply.isEnabled = true

            if (result.isSuccess) {
                Toast.makeText(this@ApplyActivity, R.string.msg_apply_success, Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this@ApplyActivity, "Error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
