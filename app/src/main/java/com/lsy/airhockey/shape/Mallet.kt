package com.lsy.airhockey.shape

import android.opengl.GLES20
import com.lsy.airhockey.shape.programs.ColorShaderProgram

class Mallet : Shape1() {
    private val POSITION_COMPONENT_COUNT = 2
    private val COLOR_COMPONENT_COUNT = 3
    private val STRIDE: Int = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * Float.SIZE_BYTES

    override fun createVerticesArr(): FloatArray = floatArrayOf(
        0f, -0.4f, 0f, 0f, 1f,
        0f, 0.4f, 1f, 0f, 0f
    )

    /**
     * 绑定数据
     * @param textureProgram 颜色程序
     */
    fun bindData(colorProgram: ColorShaderProgram) {
        setVertexAttribPointer(
            0, colorProgram.aPositionLocation, POSITION_COMPONENT_COUNT, STRIDE
        )
//        setVertexAttribPointer(
//            POSITION_COMPONENT_COUNT,
//            colorProgram.aColorLocation,
//            COLOR_COMPONENT_COUNT,
//            STRIDE
//        )
    }

    /**
     * 绘制
     */
    override fun draw() {
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 2)
    }
}