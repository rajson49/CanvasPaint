package com.example.canvas

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.AsyncTask
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private val GALLERY: Int=2
    private var mImageButtonCurrentPaint:ImageButton?=null
    val CAMERA_PERMISSION_CODE=1
    val CAMERA_AND_ACCESS_LOCATION_CODE=12
    var isReadStrorageAllowed=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView.setSizeForBrush(20.0F)

        mImageButtonCurrentPaint=ll_pain_colors[1] as ImageButton

        mImageButtonCurrentPaint!!.
        setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallet_pressed))

        undo_button.setOnClickListener(View.OnClickListener {
            drawingView.undoPath()
        })

        ib_brush.setOnClickListener(View.OnClickListener {
            showBrushSizeChooserDialog()
        })

        ib_save.setOnClickListener(View.OnClickListener {

            // we need to convert view to bitmap image
            if (isReadStrorageAllowed){
                BitmapAsyncTask(getBitmapFromView(mainFramelayout)).execute()
            }else{
                Toast.makeText(this@MainActivity,"Not working",Toast.LENGTH_SHORT).show()
            }


        })

        camera.setOnClickListener(View.OnClickListener {

            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            {

                val pickPhotoIntent= Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhotoIntent,GALLERY)
                isReadStrorageAllowed=true
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


    // view to convert into bitmap
    private fun getBitmapFromView(view: View):Bitmap{
        val returnedBitmap=Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
        val canvas= Canvas(returnedBitmap)
        val bgDrawable=view.background
        if (bgDrawable!=null){
            bgDrawable.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }

        view.draw(canvas)

        return returnedBitmap
    }

    private inner class BitmapAsyncTask(val mBitMap:Bitmap): AsyncTask<Any, Void, String>(){


        private lateinit var mProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {

            var result=""

            if (mBitMap!=null){

                try {
                    // byteoutput stream
                    val bytes=ByteArrayOutputStream()
                    mBitMap.compress(Bitmap.CompressFormat.PNG,90,bytes)

                    val f= File(externalCacheDir!!.absoluteFile.toString()+File.separator+"Canvas_"
                    + System.currentTimeMillis()/1000+".png")

                    // fileoutput stream
                    val fos=FileOutputStream(f)
                    fos.write(bytes.toByteArray())
                    fos.close()
                    result=f.absolutePath

                }catch (e:Exception){
                    result=""
                    e.printStackTrace()
                }
            }

            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            cancelProgressDialog()
            if(!result!!.isEmpty()){
                Toast.makeText(this@MainActivity,"File Saved",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this@MainActivity,"File Not Saved",Toast.LENGTH_SHORT).show()
            }


        }

        private fun showProgressDialog(){
            mProgressDialog= Dialog(this@MainActivity)
            mProgressDialog.setContentView(R.layout.dialog_custom_progress)
            mProgressDialog.show()
        }


        private fun cancelProgressDialog(){
            mProgressDialog.dismiss()
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