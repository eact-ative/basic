package com.eactative.ua.entity

data class BaseResponse<T> (
    var code: ResponseCode,
    var data: T,
)