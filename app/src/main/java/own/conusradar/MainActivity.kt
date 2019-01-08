package own.conusradar

import android.Manifest
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.RelativeLayout


class MainActivity : Activity() {

    // Our OpenGL Surfaceview
    private var glSurfaceView: GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        checkPermissions()

        // We create our Surfaceview for our OpenGL here.
        glSurfaceView = GLSurf(this)

        // Set our view.
        setContentView(R.layout.main)

        // Retrieve our Relative layout from our main layout we just set to our view.
        val layout = findViewById<View>(R.id.gamelayout) as RelativeLayout

        // Attach our surfaceview to our relative layout from our main layout.
        val glParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        layout.addView(glSurfaceView, glParams)
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView!!.onPause()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView!!.onResume()
    }


    private val MULTIPLE_PERMISSION_REQUEST_CODE = 4


    private fun checkPermissions() {


        val internetPermissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.INTERNET
        )

        val writeExternalStoragePermissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val coarseLocationPermissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val fineLocationPermissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )



        if (internetPermissionCheck == PackageManager.PERMISSION_GRANTED &&
            writeExternalStoragePermissionCheck == PackageManager.PERMISSION_GRANTED &&
            coarseLocationPermissionCheck == PackageManager.PERMISSION_GRANTED &&
            fineLocationPermissionCheck == PackageManager.PERMISSION_GRANTED) {


        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                MULTIPLE_PERMISSION_REQUEST_CODE
            )
        }
    }

}
