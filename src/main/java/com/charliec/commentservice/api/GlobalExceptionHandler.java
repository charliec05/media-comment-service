package com.charliec.commentservice.api;

import com.charliec.commentservice.api.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(err("BAD_REQUEST", ex.getMessage(), req));
    }

    // @RequestParam / @PathVariable 校验（如果你后面加了 @Min/@Max）
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(err("BAD_REQUEST", ex.getMessage(), req));
    }

    // @Valid @RequestBody 校验失败（CreateCommentRequest）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgNotValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Validation failed"
                : ex.getBindingResult().getFieldErrors().get(0).getField() + ": " +
                  ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(err("VALIDATION_ERROR", msg, req));
    }

    // 表单/参数绑定错误（少见，但补上）
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBind(BindException ex, HttpServletRequest req) {
        String msg = ex.getFieldErrors().isEmpty()
                ? "Bind failed"
                : ex.getFieldErrors().get(0).getField() + ": " + ex.getFieldErrors().get(0).getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(err("BAD_REQUEST", msg, req));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception ex, HttpServletRequest req) {
        // 生产环境别把 ex.getMessage() 直接返回；这里先保持简洁
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(err("INTERNAL_ERROR", "Unexpected error", req));
    }

    private ErrorResponse err(String code, String message, HttpServletRequest req) {
        return new ErrorResponse(
                code,
                message,
                req.getRequestURI(),
                req.getHeader("X-Request-Id"),
                Instant.now()
        );
    }
}
