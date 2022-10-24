package com.example.fotoeditor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        val PHOTO_FILENAME = "pic.jpg"
    }

    private lateinit var imageView: ImageView
    private lateinit var rotateLeftBtn: ImageButton
    private lateinit var rotateRightBtn: ImageButton
    private lateinit var opacitySeekbar: SeekBar
    private lateinit var redSeekbar: SeekBar
    private lateinit var greenSeekbar: SeekBar
    private lateinit var blueSeekbar: SeekBar

    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    private var contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            val imgFile = File(cacheDir, PHOTO_FILENAME)
            if (imgFile.exists()) {
                val ei = ExifInterface(imgFile.absolutePath)
                val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
                var bmp = BitmapFactory.decodeFile(imgFile.absolutePath)
                bmp = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bmp, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bmp, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bmp, 270f)
                    else -> { bmp }
                }
                imageView.setImageBitmap(bmp)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        rotateLeftBtn = findViewById(R.id.rotateLeftButton)
        rotateRightBtn = findViewById(R.id.rotateRightButton)
        opacitySeekbar = findViewById(R.id.seekbarOpacity)
        redSeekbar = findViewById(R.id.seekbarRed)
        greenSeekbar = findViewById(R.id.seekbarGreen)
        blueSeekbar = findViewById(R.id.seekbarBlue)

        imageView.setOnClickListener {
            val uri = FileProvider.getUriForFile(
                this,
                "com.example.fotoeditor",
                File(cacheDir, PHOTO_FILENAME)
            )
            contract.launch(uri)
        }
    }

}