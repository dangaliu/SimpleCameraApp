package com.example.simplecameraapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.simplecameraapp.databinding.ActivityMainBinding
import com.example.simplecameraapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val TAG = "MainActivity"

    private var requestAppPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            viewModel.allPermissionsGranted = true
            for ((permission, isGranted) in map) {
                if (!isGranted) viewModel.allPermissionsGranted = false
            }
            if (!viewModel.allPermissionsGranted) {
                Toast.makeText(
                    this,
                    "Разрешения не предоставлены, приложение не будет работать",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (viewModel.checkAppPermissions()) {

        } else {
            requestAppPermissionsLauncher.launch(viewModel.appRequestList)
        }
    }
}