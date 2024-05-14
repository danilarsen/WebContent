package com.example.webcontent.ui.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.webcontent.R
import com.example.webcontent.data.datasource.local.LocalDataSource
import com.example.webcontent.data.datasource.remote.RetrofitHelper
import com.example.webcontent.data.repository.WebContentRepositoryImpl
import com.example.webcontent.databinding.ActivityMainBinding
import com.example.webcontent.ui.viewmodel.WebContentViewModel
import com.example.webcontent.utils.Constants.PREFS_NAME
import com.example.webcontent.utils.URLs.BASE_URL_API

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WebContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
        setupListeners()
    }

    private fun setupUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViewModel() {
        val sharedPreferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val localDataSource = LocalDataSource(sharedPreferences)
        val apiService = RetrofitHelper.getRetrofit(BASE_URL_API)
        val repository = WebContentRepositoryImpl(apiService, localDataSource)
        val viewModelFactory = WebContentViewModel.Factory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory)[WebContentViewModel::class.java]

        viewModel.everyTenthCharacter.observe(this, Observer { result ->
            binding.tenthCharacterText.text = result
            // Adjust the visibility of the scroll hint based on whether the result is empty or not
            binding.scrollHintText.visibility = if (result.isEmpty()) View.GONE else View.VISIBLE
        })

        viewModel.wordCount.observe(this, Observer { result ->
            val resultString = result.toString()
            binding.wordCountText.text = resultString
            // Adjust the visibility of the scroll hint based on whether the result is empty or not
            binding.scrollHintText.visibility =
                if (resultString.isEmpty()) View.GONE else View.VISIBLE
        })
    }


    private fun setupListeners() {
        binding.fetchButton.setOnClickListener {
            viewModel.fetchData()
        }
    }
}