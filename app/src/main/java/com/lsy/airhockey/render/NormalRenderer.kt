package com.lsy.airhockey.render

import android.content.res.Resources
import com.lsy.airhockey.shape.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 常规的Renderer
 * @author Xuwl
 * @date 2021/10/20
 *
 */
open class NormalRenderer(res: Resources) : BaseRenderer(res) {
    private lateinit var mShaper: Shape

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        mShaper = Table3(res)
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        mShaper.drawSelf(mProjectionMatrix)
    }
}