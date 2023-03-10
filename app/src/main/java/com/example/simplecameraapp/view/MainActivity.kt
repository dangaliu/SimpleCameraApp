package com.example.simplecameraapp.view

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.simplecameraapp.R
import com.example.simplecameraapp.databinding.ActivityMainBinding
import com.example.simplecameraapp.viewmodel.MainViewModel
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val TAG = "MainActivity"

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: Executor
    private var recording: Recording? = null
    private lateinit var videoCapture: VideoCapture<Recorder>


    private var requestAppPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            viewModel.allPermissionsGranted = true
            for ((permission, isGranted) in it) {
                if (!isGranted) viewModel.allPermissionsGranted = false
            }
            if (!viewModel.allPermissionsGranted) {
                Toast.makeText(
                    this,
                    "???????????????????? ???? ??????????????????????????, ???????????????????? ???? ?????????? ????????????????",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                startApp()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startApp()
    }

    private fun startApp() {
        if (viewModel.checkAppPermissions()) {
            cameraProviderFuture = ProcessCameraProvider.getInstance(this)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            }, ContextCompat.getMainExecutor(this))
        } else {
            requestAppPermissionsLauncher.launch(viewModel.appRequestList)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        imageCapture = ImageCapture.Builder().build()


        setListeners()
    }

    private fun setListeners() {
        binding.cvTakePhoto.setOnClickListener {
            takePhoto()
        }

        binding.btnGoToGallery.setOnClickListener {
            startActivity(Intent(this, GalleryActivity::class.java))
        }

        binding.cvCaptureVideo.setOnClickListener {
            captureVideo()
        }
    }

    private fun takePhoto() {
        val contextWrapper = ContextWrapper(this)
        val imagesDir: File = contextWrapper.getDir("imagesDir", Context.MODE_PRIVATE)
        val fileName = "IMAGE_${System.currentTimeMillis()}"
        val file = File(imagesDir, fileName)
        val outputFileOption = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputFileOption,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d(TAG, "onImageSaved: Saved uri ${file.toUri()}")
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.d(TAG, "onError: ${exception.message}")
                }
            }
        )
    }

    private fun captureVideo() {
        val contextWrapper = ContextWrapper(this)
        val videosDir: File = contextWrapper.getDir("videosDir", Context.MODE_PRIVATE)
        val fileName = "VIDEO_${System.currentTimeMillis()}"
        val file = File(videosDir, fileName)

        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            return
        }

        val fileOutputOptions = FileOutputOptions.Builder(file).build()
        recording = videoCapture.output
            .prepareRecording(this, fileOutputOptions)
            .apply {
                if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.ivCaptureVideo.setImageResource(R.drawable.stop)
                    }
                    is VideoRecordEvent.Finalize -> {
                        binding.ivCaptureVideo.setImageResource(R.drawable.play)
                        if (!recordEvent.hasError()) {
                            Toast.makeText(this, "?????????? ?????????????????? ?? ${recordEvent.outputResults.outputUri}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "???????????? ${recordEvent.error}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
        cameraProvider.unbindAll()

        val recorder = Recorder.Builder().build()
        videoCapture = VideoCapture.withOutput(recorder)

        cameraProvider.unbindAll()

        val camera = cameraProvider.bindToLifecycle(
            this,
            cameraSelector,
            preview,
            imageCapture,
            videoCapture
        )
    }
}