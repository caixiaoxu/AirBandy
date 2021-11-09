package com.lsy.airhockey.utils

import android.content.res.Resources
import android.opengl.GLES20
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

/**
 * OpenGL ES 初始化工具类(加载着色器，创建程序，检测)
 * @author Xuwl
 * @date 2021/10/20
 *
 */
object ShaderUtil {

    /**
     * 从资源文件中读取着色器
     * @param res 资源类
     * @param rId 文件名
     */
    fun readTextFileFromResource(res: Resources, fname: String): String {
        val body = StringBuilder()
        try {
            val bReader = BufferedReader(InputStreamReader(res.assets.open(fname)))
            var nextLine: String?
            while (null != bReader.readLine().also { nextLine = it }) {
                body.append(nextLine).append('\n')
            }
        } catch (e: IOException) {
            throw RuntimeException("Could not open resource : $fname", e)
        } catch (nfe: FileNotFoundException) {
            throw RuntimeException("Could not found : $fname", nfe)
        }
        return body.toString()
    }

    /**
     * 编译顶点着色器
     * @param sourceCode 着色器源码
     */
    fun compileVertexShaper(sourceCode: String): Int =
        compileShaper(GLES20.GL_VERTEX_SHADER, sourceCode)

    /**
     * 编译片元着色器
     * @param sourceCode 着色器源码
     */
    fun compileFragmentShaper(sourceCode: String): Int =
        compileShaper(GLES20.GL_FRAGMENT_SHADER, sourceCode)

    /**
     * 编译着色器
     * @param type 着色器类型
     * @param sourceCode 着色器源码
     */
    fun compileShaper(type: Int, sourceCode: String): Int {
        //创建着色器对象
        val shaderId = GLES20.glCreateShader(type)
        //创建失败
        if (0 == shaderId) {
            LogUtil.logE("Could not create new shaper.")
            return 0
        }

        //上传源代码
        GLES20.glShaderSource(shaderId, sourceCode)
        //编译
        GLES20.glCompileShader(shaderId)

        //检查编译状态
        val status = IntArray(1).also { arr -> checkShaderCompile(shaderId, arr) }
        //编译失败
        if (0 == status[0]) {
            LogUtil.logE("Compilation of shader failed.")
            printShaderInfo(shaderId, status[0])
            GLES20.glDeleteShader(shaderId)
            return 0
        }
        return shaderId
    }

    /**
     * 检测着色器的编译状态
     * @param shaderId 着色器ID
     * @param status 返回状态
     */
    fun checkShaderCompile(shaderId: Int, status: IntArray) {
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, status, 0)
    }

    /**
     * 打印着色器日志
     */
    fun printShaderInfo(shaderId: Int, status: Int) {
        LogUtil.logE("Results of compiling source: $status \n-->${GLES20.glGetShaderInfoLog(shaderId)}")
    }

    /**
     * 链接OpenGL ES程序
     */
    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        if (0 == vertexShaderId || 0 == fragmentShaderId) {
            return 0
        }
        val programId = GLES20.glCreateProgram()
        if (0 == programId) {
            LogUtil.logE("Could not create new program")
            return 0
        }
        //附上着色器
        GLES20.glAttachShader(programId, vertexShaderId)
        GLES20.glAttachShader(programId, fragmentShaderId)
        GLES20.glLinkProgram(programId)

        //检查编译状态
        val status = IntArray(1).also { arr -> checkProgramLink(programId, arr) }
        //编译失败
        if (0 == status[0]) {
            LogUtil.logE("Linking of program failed.")
            printProgramLinkInfo(programId, status[0])
            GLES20.glDeleteShader(programId)
            return 0
        }
        return programId
    }

    /**
     * 检测着色器的编译状态
     * @param programId 程序ID
     * @param status 返回状态
     */
    fun checkProgramLink(programId: Int, status: IntArray) {
        GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, status, 0)
    }

    /**
     * 打印创建程序日志
     * @param programId 程序ID
     * @param status 状态码
     */
    fun printProgramLinkInfo(programId: Int, status: Int) {
        LogUtil.logE(
            "Results of Linking program: $status \n-->${
                GLES20.glGetProgramInfoLog(programId)
            }"
        )
    }

    /**
     * 程序是否有效
     * @param programId 程序ID
     */
    fun validateProgram(programId: Int): Boolean {
        GLES20.glValidateProgram(programId)
        val status = IntArray(1).also { arr ->
            GLES20.glGetProgramiv(programId, GLES20.GL_VALIDATE_STATUS, arr, 0)
        }
        printProgramLinkInfo(programId, status[0])
        return status[0] != 0
    }

    /**
     * 创建程序
     */
    fun buildProgram(vertexShaderSource: String, fragmentShaderSource: String): Int {
        //编译着色器
        val vertexId = compileVertexShaper(vertexShaderSource)
        val fragmentId = compileFragmentShaper(fragmentShaderSource)
        //链接程序
        return linkProgram(vertexId, fragmentId).apply {
            validateProgram(this)
        }
    }
}