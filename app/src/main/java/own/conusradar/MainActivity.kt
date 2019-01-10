package own.conusradar

import android.Manifest
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import com.intentfilter.androidpermissions.PermissionManager
import kotlinx.coroutines.*
import okio.Okio
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.*


class MainActivity : Activity() {

    // Our OpenGL Surfaceview
    private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main

    private var glSurfaceView: GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //checkPermissions()


        //storage permission for downloading conus.gif to sdcard//
        val storagepermissionManager = PermissionManager.getInstance(this)
        storagepermissionManager.checkPermissions(Collections.singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), object : PermissionManager.PermissionRequestListener {
            override fun onPermissionGranted() {
                getimage()
            }

            override fun onPermissionDenied() {
                Log.d("conus", "Storage Permissions Denied")
            }
        })


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

    /*
    override fun onPause() {
        super.onPause()
        glSurfaceView!!.onPause()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView!!.onResume()
    }
*/


    /*
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
            Log.d("conus", "have perms!")
            getimage()

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
    */




    fun getimage() = GlobalScope.launch(uiDispatcher) {
        withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            Log.d("conus", "getimage ran")
            try {
                val request = Request.Builder()
                    .url("https://radar.weather.gov/ridge/Conus/RadarImg/latest_radaronly.gif")
                    .build()

                val response = client.newCall(request).execute()

                val Directory = File(Constants.FilesPath)
                if (!Directory.exists()) {
                    Directory.mkdirs()
                }

                val sink = Okio.buffer(Okio.sink(File(Directory, "conus.gif")))
                sink.writeAll(response.body()!!.source())
                sink.close()
                response.body()!!.close()
            } catch (e: Exception) {
                Log.d("conus", "exception: " + e.printStackTrace())
                e.printStackTrace()
            }
        }

        }



    }



