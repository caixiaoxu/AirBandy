package com.lsy.airhockey.render

import android.content.res.Resources
import android.opengl.Matrix
import com.lsy.airhockey.shape.Shape
import com.lsy.airhockey.shape.Table3
import com.lsy.airhockey.shape.Table4
import com.lsy.airhockey.utils.MatrixUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 矩阵Renderer
 * @author Xuwl
 * @date 2021/10/20
 *
 */
class MatrixRenderer(res: Resources) : BaseRenderer(res) {
    private lateinit var mShaper: Shape

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        mShaper = Table3(res)
    }

    override fun initMatrix(width: Int, height: Int) {
        val aspectRatio = width.toFloat() / height.toFloat()
        MatrixUtil.perspectiveM(mProjectionMatrix, 45f, aspectRatio, 1f, 10f)

        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f)
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)

        val temp = FloatArray(16)
        Matrix.multiplyMM(temp, 0, mProjectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, mProjectionMatrix, 0, temp.size)
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        mShaper.drawSelf(mProjectionMatrix)
    }
}