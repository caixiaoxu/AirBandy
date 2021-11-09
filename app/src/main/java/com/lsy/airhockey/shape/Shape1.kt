package com.lsy.airhockey.shape

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * @author Xuwl
 * @date 2021/10/20
 *
 */
abstract class Shape1 {

    /**
     * 创建顶点数据
     */
    protected abstract fun createVerticesArr(): FloatArray

    /**
     * 顶点的缓存数据
     */
    protected val vertexData: FloatBuffer = createVerticesArr().let { arr ->
        ByteBuffer.allocateDirect(arr.size * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(arr)
            }
        }
    }

    /**
     * 设置顶点参数
     * @param dataOffsset 起始位置
     * @param attributeLocation 着色器变量
     * @param componentCount 一条数据有几个数据
     * @param stride 长度
     */
    protected fun setVertexAttribPointer(
        dataOffsset: Int, attributeLocation: Int, componentCount: Int, stride: Int
    ) {
        vertexData.position(dataOffsset)
        GLES20.glVertexAttribPointer(
            attributeLocation, componentCount, GLES20.GL_FLOAT, false, stride, vertexData
        )
        GLES20.glEnableVertexAttribArray(attributeLocation)
        vertexData.position(0)
    }

    abstract fun draw()
}