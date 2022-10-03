package com.ochs.app.backgroundtasks

interface AcuantTokenServiceListener {
    fun onSuccess(token: String)
    fun onFail(responseCode: Int)
}