package com.example.canvas

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*
import java.lang.Exception
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private val GALLERY: Int=2
    private var mImageButtonCurrentPaint:ImageButton?=null
    val CAMERA_PERMISSION_CODE=1
    val CAMERA_AND_ACCESS_LOCATION_CODE=12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView.setSizeForBrush(20.0F)

        mImageButtonCurrentPaint=ll_pain_colors[1] as ImageButton

        mImageButtonCurrentPaint!!.
        setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallet_pressed))


        ib_brush.setOnClickListener(View.OnClickListener {
            showBrushSizeChooserDialog()
        })

        camera.setOnClickListener(View.OnClickListener {

            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            {

                val pickPhotoIntent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhotoIntent,GALLERY)

            }
            else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                CAMERA_AND_ACCESS_LOCATION_CODE)
            }

        })


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==CAMERA_AND_ACCESS_LOCATION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission granted for camera",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Oops you just denied the permision for camera.",Toast.LENGTH_SHORT).show()
            }
        }

    }


    // Custom Dialog Box Example
    private fun showBrushSizeChooserDialog(){
        val brushDialog= Dialog(this)

        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size :")

        val smallBtn=brushDialog.ib_small_brush
        smallBtn.setOnClickListener(View.OnClickListener {
            drawingView.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        })

        val mediumbtn=brushDialog.ib_medium_brush
        mediumbtn.setOnClickListener(View.OnClickListener {
            drawingView.setSizeForBrush(12.toFloat())
            brushDialog.dismiss()
        })

        val largeBrush=brushDialog.ib_large_brush
        largeBrush.setOnClickListener(View.OnClickListener {
            drawingView.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        })

        brushDialog.show()
    }

    fun paintClicked(view:View){

        if(view !== mImageButtonCurrentPaint){

            val imageButton= view as ImageButton

            val colorTag = imageButton.tag.toString()

            drawingView.setColor(colorTag)

            imageButton.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallet_pressed))

            mImageButtonCurrentPaint!!.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallet_normal))

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== Activity.RESULT_OK){
            if(requestCode==GALLERY){

                try {

                    if(data!!.data!=null){
                        import_image.setImageURI(data.data)

                    }else{
                        Toast.makeText(this,"error",Toast.LENGTH_SHORT).show()
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }

            }
        }

    }

}