package com.lsy.airhockey.shape

import android.content.res.Resources
import android.opengl.GLES20
import com.lsy.airhockey.utils.ShaderUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * @author Xuwl
 * @date 2021/10/20
 *
 */
abstract class Shape(res: Resources) {
    protected val mProgram: Int

    init {
        //加载着色器
        val vertexShaper = ShaderUtil.readTextFileFromResource(res, loadVertexShader())
        val fragmentShaper = ShaderUtil.readTextFileFromResource(res, loadFragmentShader())
        //编译着色器
        val vertexId = ShaderUtil.compileVertexShaper(vertexShaper)
        val fragmentId = ShaderUtil.compileFragmentShaper(fragmentShaper)
        //链接程序
        mProgram = ShaderUtil.linkProgram(vertexId, fragmentId)
        if (ShaderUtil.validateProgram(mProgram)) {
            GLES20.glUseProgram(mProgram)
        }
    }

    /**
     * 着色器资源路径
     */
    protected abstract fun loadVertexShader(): String
    protected abstract fun loadFragmentShader(): String

    /**
     * 绘制自己
     * @param matrix 矩阵
     */
    abstract fun drawSelf(matrix: FloatArray)

}