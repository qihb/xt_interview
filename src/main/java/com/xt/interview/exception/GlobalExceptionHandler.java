package com.xt.interview.exception;

import com.xt.interview.dto.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.validation.method.ParameterValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleBodyValidation(MethodArgumentNotValidException exception) {
        Map<String, String> details = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            details.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return response(HttpStatus.BAD_REQUEST, "请求参数校验失败", details);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodValidation(HandlerMethodValidationException exception) {
        Map<String, String> details = new LinkedHashMap<>();
        for (ParameterValidationResult result : exception.getParameterValidationResults()) {
            String field = result.getMethodParameter().getParameterName();
            String message = result.getResolvableErrors().stream()
                    .map(error -> error.getDefaultMessage() == null ? "参数值不合法" : error.getDefaultMessage())
                    .findFirst()
                    .orElse("参数值不合法");
            details.put(field == null ? "request" : field, message);
        }
        return response(HttpStatus.BAD_REQUEST, "请求参数校验失败", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        Map<String, String> details = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath().toString();
            String field = path.substring(path.lastIndexOf('.') + 1);
            details.putIfAbsent(field, violation.getMessage());
        });
        return response(HttpStatus.BAD_REQUEST, "请求参数校验失败", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableBody() {
        return response(HttpStatus.BAD_REQUEST, "请求体格式不正确", Map.of("body", "请提交合法的 JSON 数据"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException exception) {
        String message = exception.getMostSpecificCause().getMessage();
        if (message != null && (message.contains("uk_username") || message.contains("username"))) {
            return response(HttpStatus.CONFLICT, "用户名已存在", Map.of("username", "用户名已存在"));
        }
        if (message != null && (message.contains("uk_code") || message.contains("code"))) {
            return response(HttpStatus.CONFLICT, "部门编码已存在", Map.of("code", "部门编码已存在"));
        }
        return response(HttpStatus.BAD_REQUEST, "数据保存失败", Map.of("request", "请检查字段值和关联数据"));
    }

    @ExceptionHandler(DepartmentInUseException.class)
    public ResponseEntity<ApiErrorResponse> handleDepartmentInUse(DepartmentInUseException exception) {
        return response(HttpStatus.CONFLICT, exception.getMessage(), Map.of("id", exception.getMessage()));
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handlePermissionDenied(PermissionDeniedException exception) {
        return response(HttpStatus.FORBIDDEN, exception.getMessage(),
                Map.of("permission", exception.getRequiredPermission()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        String message = exception.getMessage();
        String field = message != null && message.startsWith("ID")
                ? "id"
                : message != null && message.startsWith("所属部门")
                ? "departmentId"
                : message != null && message.startsWith("页码")
                ? "page"
                : message != null && message.startsWith("每页")
                ? "size"
                : "request";
        return response(HttpStatus.BAD_REQUEST, message, Map.of(field, message));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessRuntimeException(RuntimeException exception) {
        String message = exception.getMessage();
        if ("用户名已存在".equals(message)) {
            return response(HttpStatus.CONFLICT, message, Map.of("username", message));
        }
        if ("部门编码已存在".equals(message)) {
            return response(HttpStatus.CONFLICT, message, Map.of("code", message));
        }
        if ("账号不存在".equals(message) || "部门不存在".equals(message)) {
            return response(HttpStatus.NOT_FOUND, message, Map.of("id", message));
        }
        log.error("Unhandled API exception", exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "服务器处理失败", Map.of("request", "请稍后重试或联系管理员"));
    }

    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, String message, Map<String, String> details) {
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(status.value(), status.getReasonPhrase(), message, details));
    }
}
