package com.lsy.airhockey.shape

import android.opengl.GLES20
import com.lsy.airhockey.shape.programs.SkyboxShaderProgram
import java.nio.ByteBuffer

/**
 * @author Xuwl
 * @date 2021/10/27
 *
 */
class SkyBox {
    private val POSITION_COMPONENT_COUNT = 3
    private val vertexArray: VertexArray
    private val indexArray: ByteBuffer

    init {
        vertexArray = VertexArray(floatArrayOf(
            -1f, 1f, 1f,//(0)前左上
            1f, 1f, 1f,//(1)前左上
            -1f, -1f, 1f,//(2)前左下
            1f, -1f, 1f,//(3)前右下
            -1f, 1f, -1f,//(4)后左上
            1f, 1f, -1f,//(5)后右上
            -1f, -1f, -1f,//(6)后左下
            1f, -1f, -1f//(7)后右下
        ))

        indexArray = ByteBuffer.allocateDirect(6 * 6).apply {
            put(byteArrayOf(
                //前
                1, 3, 0,
                0, 3, 2,
                //后
                4, 6, 5,
                5, 6, 7,
                //左
                0, 2, 4,
                4, 2, 6,
                //右
                5, 7, 1,
                1, 7, 3,
                //上
                5, 1, 4,
                4, 1, 0,
                //下
                6, 2, 7,
                7, 2, 3
            ))
            position(0)
        }
    }

    /**
     * 绑定数据
     * @param skyboxShaderProgram 天空盒着色程序
     */
    fun bindData(skyboxShaderProgram: SkyboxShaderProgram) {
        vertexArray.setVertexAttribPointer(0,
            skyboxShaderProgram.aPositionLocation, POSITION_COMPONENT_COUNT, 0)
    }

    fun draw() {
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_BYTE, indexArray)
    }
}