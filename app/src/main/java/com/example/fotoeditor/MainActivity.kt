package com.example.fotoeditor

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        val PHOTO_FILENAME = "pic.jpg"
    }

    private lateinit var imageView: ImageView
    private lateinit var takePictureButton: Button

    private var contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            val imgFile = File(cacheDir, PHOTO_FILENAME)
            if (imgFile.exists()) {
                val bmp = BitmapFactory.decodeFile(imgFile.absolutePath)
                imageView.setImageBitmap(bmp)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.wtf)
        takePictureButton = findViewById(R.id.take_pic_btn)

        takePictureButton.setOnClickListener {
            val uri = FileProvider.getUriForFile(
                this,
                "com.example.fotoeditor",
                File(cacheDir, PHOTO_FILENAME)
            )
            contract.launch(uri)
        }
    }

}