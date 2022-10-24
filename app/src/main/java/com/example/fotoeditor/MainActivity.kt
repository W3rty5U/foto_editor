package com.example.fotoeditor

import android.graphics.*
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
    private lateinit var bitmap: Bitmap

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    fun updateImageFilter() {
        imageView.imageAlpha = opacitySeekbar.progress
        imageView.colorFilter = PorterDuffColorFilter(Color.rgb(
            redSeekbar.progress,
            greenSeekbar.progress,
            blueSeekbar.progress
        ), PorterDuff.Mode.MULTIPLY)
    }

    private var contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            val imgFile = File(cacheDir, PHOTO_FILENAME)
            if (imgFile.exists()) {
                val ei = ExifInterface(imgFile.absolutePath)
                val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
                bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                bitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                    else -> { bitmap }
                }
                imageView.setImageBitmap(bitmap)
                updateImageFilter()
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

        rotateLeftBtn.setOnClickListener {
            imageView.rotation -= 90
        }

        rotateRightBtn.setOnClickListener {
            imageView.rotation += 90
        }

        var commonListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                updateImageFilter()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        }

        opacitySeekbar.setOnSeekBarChangeListener(commonListener)
        redSeekbar.setOnSeekBarChangeListener(commonListener)
        greenSeekbar.setOnSeekBarChangeListener(commonListener)
        blueSeekbar.setOnSeekBarChangeListener(commonListener)
    }

}