package com.example.auth_service.common.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(
        ex: CustomException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val code = ex.errorCode
        return buildErrorResponse(code.httpStatus, code.message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.fieldErrors.joinToString(", ") {
            "${it.field}: ${it.defaultMessage}"
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val message = "파라미터 '${ex.name}'의 타입이 올바르지 않습니다. (입력값: ${ex.value})"
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val message = "지원하지 않는 HTTP 메서드입니다: ${ex.method}"
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, message)
    }

    @ExceptionHandler(Exception::class)
    fun handleAll(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        ex.printStackTrace()
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 오류")
    }

    private fun buildErrorResponse(
        status: HttpStatus,
        message: String,
    ): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            error = status.reasonPhrase,
            message = message,
        )
        return ResponseEntity.status(status).body(body)
    }
}
