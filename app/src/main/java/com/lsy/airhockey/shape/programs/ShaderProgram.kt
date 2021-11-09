package com.lsy.airhockey.shape.programs

import android.content.res.Resources
import android.opengl.GLES20
import com.lsy.airhockey.utils.ShaderUtil

/**
 * 着色器程序
 * @param res 资源
 * @param vertexShaderResourceName 顶点着色器的资源名
 * @param fragmentShaderResourceName 片元着色器的资源名
 */
open class ShaderProgram(
    res: Resources, vertexShaderResourceName: String, fragmentShaderResourceName: String,
) {
    //Uniform 常量
    protected val U_MATRIX = "u_Matrix"
    protected val U_TEXTURE_UNIT = "u_TextureUnit"
    protected val U_COLOR = "u_Color"
    protected val U_TIME = "u_Time"
    protected val U_VECTOR_TO_LIGHT = "u_VectorToLight"
    protected val U_MV_MATRIX = "u_MVMatrix"
    protected val U_IT_MV_MATRIX = "u_IT_MVMatrix"
    protected val U_MVP_MATRIX = "u_MVPMatrix"
    protected val U_POINT_LIGHT_POSITIONS = "u_PointLightPositions"
    protected val U_POINT_LIGHT_COLORS = "u_PointLightColors"

    //Attribute 常量
    protected val A_POSITION = "a_Position"
    protected val A_COLOR = "a_Color"
    protected val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
    protected val A_DIRECTION_VECTOR = "a_DirectionVector"
    protected val A_PARTICLE_START_TIME = "a_ParticleStartTime"
    protected val A_NORMAL = "a_Normal"

    protected val mProgram: Int

    init {
        //加载着色器
        val vertexShaper = ShaderUtil.readTextFileFromResource(res, vertexShaderResourceName)
        val fragmentShaper = ShaderUtil.readTextFileFromResource(res, fragmentShaderResourceName)
        mProgram = ShaderUtil.buildProgram(vertexShaper, fragmentShaper)
    }

    /**
     * 使用程序
     */
    fun useProgram() {
        GLES20.glUseProgram(mProgram)
    }
}