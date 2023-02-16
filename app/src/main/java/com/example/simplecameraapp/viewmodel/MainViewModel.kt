package com.example.simplecameraapp.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

class MainViewModel(private val app: Application) : AndroidViewModel(app) {

    val appRequestList = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO
    )

    var allPermissionsGranted = false

    fun checkAppPermissions(): Boolean {
        appRequestList.forEach {
            if (app.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }
}