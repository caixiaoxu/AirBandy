package com.lsy.airhockey.services

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import android.widget.Toast
import com.lsy.airhockey.render.ParticlesRender
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author Xuwl
 * @date 2021/10/29
 */
internal class GLWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return GLEngine()
    }

    inner class GLEngine : Engine() {
        private lateinit var mGlSurfaceView: WallpaperGLSurfaceView
        private var rendererSet = false

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            mGlSurfaceView = WallpaperGLSurfaceView(this@GLWallpaperService.baseContext)

            //是否支持OpenGl Es 2.0
            if ((getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).deviceConfigurationInfo.reqGlEsVersion >= 0x2000) {
                val particlesRenderer = ParticlesRender(this@GLWallpaperService.resources)
                mGlSurfaceView.setEGLContextClientVersion(2)
                mGlSurfaceView.setRenderer(particlesRenderer)
                rendererSet = true
            } else {
                Toast.makeText(this@GLWallpaperService,
                    "This device does not support OpenGL ES 2.0",
                    Toast.LENGTH_LONG).show()
                return
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (rendererSet) {
                if (visible) {
                    mGlSurfaceView.onResume()
                } else {
                    mGlSurfaceView.onPause()
                }
            }
        }

        override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int
        ) {
            mGlSurfaceView.queueEvent {}
        }

        override fun onDestroy() {
            super.onDestroy()
            mGlSurfaceView.onWallpaperDestroy()
        }

        inner class WallpaperGLSurfaceView(context: Context?) : GLSurfaceView(context) {

            override fun getHolder(): SurfaceHolder = surfaceHolder

            fun onWallpaperDestroy() {
                super.onDetachedFromWindow()
            }
        }
    }
}