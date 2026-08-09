package com.icc.eserviceshelper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.icc.eserviceshelper.adapters.CategoryAdapter
import com.icc.eserviceshelper.databinding.ActivityMainBinding
import com.icc.eserviceshelper.utils.UiState
import com.icc.eserviceshelper.viewmodels.MainViewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: CategoryAdapter
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDrawer()
        setupRecyclerView()
        setupSearchView()
        observeViewModel()
        setupBackPress()
    }

    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    if (backPressedTime + 2000 > System.currentTimeMillis()) {
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Press back again to exit", Toast.LENGTH_SHORT).show()
                        backPressedTime = System.currentTimeMillis()
                    }
                }
            }
        })
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupDrawer() {
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
        
        // Material 3 style: remove icon tint if you want original colors, 
        // but here we want to match the theme.
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(emptyList()) { category ->
            val intent = Intent(this, TopicsActivity::class.java)
            intent.putExtra("CATEGORY", category)
            startActivity(intent)
        }
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCategories.adapter = adapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearchQuery(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewModel.filteredCategories.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvEmptyState.visibility = View.GONE
                    binding.recyclerViewCategories.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val categories = state.data
                    if (categories.isEmpty()) {
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.recyclerViewCategories.visibility = View.GONE
                        binding.tvEmptyState.text = "No results found"
                    } else {
                        binding.tvEmptyState.visibility = View.GONE
                        binding.recyclerViewCategories.visibility = View.VISIBLE
                        adapter.updateList(categories)
                    }
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewCategories.visibility = View.GONE
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.tvEmptyState.text = "Error: ${state.message}"
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // Already on Home
            }
            R.id.nav_about -> openInfo("ABOUT")
            R.id.nav_privacy -> openInfo("PRIVACY")
            R.id.nav_terms -> openInfo("TERMS")
            R.id.nav_disclaimer -> openInfo("DISCLAIMER")
            R.id.nav_sources -> openInfo("SOURCES")
            R.id.nav_contact -> openInfo("CONTACT")
            R.id.nav_report -> reportIssue()
            R.id.nav_share -> shareApp()
            R.id.nav_rate -> rateApp()
            R.id.nav_update -> checkForUpdates()
            R.id.nav_version -> openInfo("VERSION")
            R.id.nav_developer -> openInfo("DEVELOPER")
            R.id.nav_organization -> openInfo("ORGANIZATION")
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openInfo(type: String) {
        val intent = Intent(this, InfoActivity::class.java)
        intent.putExtra("INFO_TYPE", type)
        startActivity(intent)
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "eServices: India Cyber Cafe")
        val shareMessage = "Simplify your access to government services with eServices: India Cyber Cafe! Get step-by-step guides for Aadhaar, PAN, Voter ID, Passport, and more. Download the app today: https://play.google.com/store/apps/details?id=${packageName}"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun rateApp() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${packageName}"))
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${packageName}")))
        }
    }

    private fun reportIssue() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("icc@indiacybercafe.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Report Issue - eServices: India Cyber Cafe")
        }
        startActivity(Intent.createChooser(intent, "Send Email"))
    }

    private fun checkForUpdates() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${packageName}"))
        startActivity(intent)
    }


}