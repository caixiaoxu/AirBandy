package com.lsy.airhockey.utils

import android.util.Log

/**
 * @author Xuwl
 * @date 2021/10/20
 *
 */
object LogUtil {
    private val ON = true
    private val TAG = "OpenGL ES"

    fun logE(msg: String) {
        if (ON) Log.e(TAG, msg)
    }
}