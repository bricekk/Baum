package com.example.baum

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    var storage = Firebase.storage
    var storageRef = storage.reference

    val gsReference = storage.getReferenceFromUrl("gs://baum2x.appspot.com/javascript1.pdf")

    var islandRef = storageRef.child("javascript1.pdf")

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)


        val downloadButton: Button = findViewById(R.id.button)

        downloadButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "onCreate: STORAGE PERMISSION IS ALREADY GRANTED")
                dowloadfile()
            } else {
                Log.d(TAG, "onCreate: STORAGE PERMISSION WAS NOT GRANTED")
                requestStoragePermissionLauncher.launch(WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private val requestStoragePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted:Boolean ->

        if (isGranted){
            Toast.makeText(this, "STORAGE GRANTED", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "onCreate: STORAGE PERMISSION has been GRANTED")
            dowloadfile()

        }
        else{
            Toast.makeText(this,"STORAGE NOT GRANTED", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "onCreate: STORAGE PERMISSION not GRANTED")

        }
    }


    private fun dowloadfile(){
        Log.d(TAG,"DOWNLOADBOOK: DOWNLOADINGBOOK")
        progressDialog.setMessage("Downloading Book")
        progressDialog.show()

        //let download book from firebase
        gsReference.getBytes(104857600)
            .addOnSuccessListener{bytes ->
                Toast.makeText(this, "book downloaded", Toast.LENGTH_SHORT).show()
                saveToDownloadsFolder(bytes)
            }
            .addOnFailureListener{ e->
                Toast.makeText(this, "FAILED TO DOWNLOAD due to ${e.message}", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
    }


    private fun saveToDownloadsFolder(bytes: ByteArray?){
        Log.d(TAG,"SAVING BOOK")

        val nameWithExtention = "javascriptbk.pdf"

        try {
            val downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadFolder.mkdirs() //create folder if not exist

            val filePath = downloadFolder.path +"/"+nameWithExtention

            val out = FileOutputStream(filePath)
            out.write(bytes)
            out.close()
            Toast.makeText(this, "saved to down folder", Toast.LENGTH_SHORT).show()
            Log.d(TAG,"saved to down folder")

            progressDialog.dismiss()

            //Toast.makeText(this,"",Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception){
            Log.d(TAG,"failed to save due to ${e.message}")
            Toast.makeText(this,"failed to Saved due to ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}