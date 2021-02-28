package com.ostah_app.data.remote.objects

import java.io.Serializable

class BaseResponseModel<T> : Serializable {
    var success: Boolean =false
    var message: Any? = null
    var data: T? = null
}