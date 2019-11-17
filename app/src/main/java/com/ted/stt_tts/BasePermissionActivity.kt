package com.ted.stt_tts

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*

abstract class BasePermissionActivity : AppCompatActivity() {

    private val REQUEST_ASK_MULTIPLE_PERMISSIONS = 124

    //SetUp views after permission granted
    abstract fun getLayoutId(): Int
    abstract fun initLayout()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())

        // Marshmallow next version
        if (Build.VERSION.SDK_INT >= 23) {
            fuckMarshmallow()
        }
        // Marshmallow old version
        else {
            initLayout()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun fuckMarshmallow() {
        val permissionsList = ArrayList<String>()
        if (!isPermissionGranted(permissionsList, Manifest.permission.RECORD_AUDIO)) {
            if (permissionsList.size > 0) {
                ActivityCompat.requestPermissions(this, permissionsList.toTypedArray(), REQUEST_ASK_MULTIPLE_PERMISSIONS)
                return
            }
        }
        initLayout()
    }


    @TargetApi(Build.VERSION_CODES.M)
    private fun isPermissionGranted(permissionsList: MutableList<String>, permission: String): Boolean {

        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission)

            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ASK_MULTIPLE_PERMISSIONS -> {
                val perms = HashMap<String, Int>()
                perms[Manifest.permission.RECORD_AUDIO] = PackageManager.PERMISSION_GRANTED

                for (i in permissions.indices) {
                    perms[permissions[i]] = grantResults[i]
                }

                // All Permissions Granted
                if (perms[Manifest.permission.RECORD_AUDIO] == PackageManager.PERMISSION_GRANTED) {
                    initLayout()
                }
                // Permission Denied
                else {
                    Toast.makeText(this, "Some Permissions are Denied Exiting App", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

}