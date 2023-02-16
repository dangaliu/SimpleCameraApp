package com.example.simplecameraapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.simplecameraapp.databinding.ActivityMainBinding
import com.example.simplecameraapp.viewmodel.MainViewModel
import com.google.common.util.concurrent.ListenableFuture

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val TAG = "MainActivity"

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private var requestAppPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            viewModel.allPermissionsGranted = true
            for ((permission, isGranted) in it) {
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
            cameraProviderFuture = ProcessCameraProvider.getInstance(this)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            }, ContextCompat.getMainExecutor(this))
        } else {
            requestAppPermissionsLauncher.launch(viewModel.appRequestList)
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
        cameraProvider.unbindAll()
        val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview)
    }
}