package com.lsy.airhockey.render

import android.content.res.Resources
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.lsy.airhockey.utils.MatrixUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.min

/**
 * @author Xuwl
 * @date 2021/10/20
 *
 */
abstract class BaseRenderer(val res: Resources) : GLSurfaceView.Renderer {
    //归一化矩阵
    protected val mProjectionMatrix = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //底色
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //窗口大小
        GLES20.glViewport(0, 0, width, height)
        //初始化矩阵
        initMatrix(width, height)
    }

    /**
     * 初始化基础矩阵
     * @param width 窗口的宽
     * @param height 窗口的高
     */
    protected open fun initMatrix(width: Int, height: Int) {
        MatrixUtil.createBaseOrthoM(mProjectionMatrix, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }
}