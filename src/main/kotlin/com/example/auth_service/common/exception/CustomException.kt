package com.example.auth_service.common.exception

open class CustomException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)