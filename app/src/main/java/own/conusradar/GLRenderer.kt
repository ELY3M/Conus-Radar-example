package own.conusradar

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.graphics.RectF
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix


class GLRenderer: Renderer {


    internal var ortInt = 400

    private val mtrxProjection = FloatArray(16)
    private val mtrxView = FloatArray(16)
    private val mtrxProjectionAndView = FloatArray(16)



/*
    fun onPause() {
        /* Do stuff to pause the renderer */
    }

    fun onResume() {
        /* Do stuff to resume the renderer */
        //mLastTime = System.currentTimeMillis();
    }

*/

    override fun onDrawFrame(unused: GL10) {

        var vertexBuffer: FloatBuffer
        var drawListBuffer: ShortBuffer
        var uvBuffer: FloatBuffer


        //use conus shader
        GLES20.glUseProgram(riGraphicTools.sp_Image)

        // clear Screen and Depth Buffer, we have set the clear color as black.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)


        //triangle
        val base = RectF(-500f, 500f, 500f, -500f)
        val scale = 1f

        val left = base.left * scale
        val right = base.right * scale
        val bottom = base.bottom * scale
        val top = base.top * scale

        val vertices = floatArrayOf(
            left, top, 0.0f,
            left, bottom, 0.0f,
            right, bottom, 0.0f,
            right, top, 0.0f)



        val indices = shortArrayOf(0, 1, 2, 0, 2, 3) // The order of vertexrendering.

        // The vertex buffer.
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        // initialize byte buffer for the draw list
        val dlb = ByteBuffer.allocateDirect(indices.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(indices)
        drawListBuffer.position(0)


        //texture
        val uvs = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f)

        // The texture buffer
        val tbb = ByteBuffer.allocateDirect(uvs.size * 4)
        tbb.order(ByteOrder.nativeOrder())
        uvBuffer = tbb.asFloatBuffer()
        uvBuffer.put(uvs)
        uvBuffer.position(0)
        LoadImage(Constants.FilesPath + "conus.gif")


        val mPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "vPosition")
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        val mTexCoordLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_texCoords")
        GLES20.glEnableVertexAttribArray(mTexCoordLoc)
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, uvBuffer)
        val mtrxhandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, mtrxProjectionAndView, 0)
        val conusTexture = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "u_texture")
        GLES20.glUniform1i(conusTexture, 0)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, drawListBuffer)

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mTexCoordLoc)


    }



    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {

        val mSurfaceRatio = width.toFloat() / height.toFloat()
        // Redo the Viewport, making it fullscreen.
        GLES20.glViewport(0, 0, width, height)

        // Clear our matrices
        for (i in 0..15) {
            mtrxProjection[i] = 0.0f
            mtrxView[i] = 0.0f
            mtrxProjectionAndView[i] = 0.0f
        }


        Matrix.orthoM(
            mtrxProjection,
            0,
            (-1 * ortInt).toFloat(),
            ortInt.toFloat(),
            -1f * ortInt.toFloat() * (1 / mSurfaceRatio),
            ortInt * (1 / mSurfaceRatio),
            1f,
            -1f
        )

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0)

        //Matrix.translateM(mtrxProjectionAndView, 0, 0f, 0f, 0f); //x and y
        //Matrix.scaleM(mtrxProjectionAndView, 0, 0f, 0f, 1f); //zoom

    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {



        // Set the clear color to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1f)
        // Create the shaders, images
        val vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Image)
        val fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Image)
        riGraphicTools.sp_Image = GLES20.glCreateProgram()             // create empty OpenGL ES Program
        GLES20.glAttachShader(riGraphicTools.sp_Image, vertexShader)   // add the vertex shader to program
        GLES20.glAttachShader(riGraphicTools.sp_Image, fragmentShader) // add the fragment shader to program
        GLES20.glLinkProgram(riGraphicTools.sp_Image)                  // creates OpenGL ES program executables
        // Set our shader programm
        //GLES20.glUseProgram(riGraphicTools.sp_Image)
    }


    fun LoadImage(imagefile: String) {
        // Generate Textures, if more needed, alter these numbers.
        val texturenames = IntArray(1)
        GLES20.glGenTextures(1, texturenames, 0)
        // Temporary create a bitmap
        val options = BitmapFactory.Options()
        options.inScaled = false
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bmp = BitmapFactory.decodeFile(imagefile, options)
        // Bind texture to texturename
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0])
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat())

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0)

        // We are done using the bitmap so we should recycle it.
        bmp.recycle()

    }




}
