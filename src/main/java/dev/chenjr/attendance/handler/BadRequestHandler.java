package dev.chenjr.attendance.handler;

import com.fasterxml.jackson.core.JsonParseException;
import dev.chenjr.attendance.exception.HttpStatusException;
import dev.chenjr.attendance.exception.SuperException;
import dev.chenjr.attendance.service.dto.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestHandler {
    
    @Value("${spring.servlet.multipart.max-file-size:1MB}")
    String maxFileSize;
    
    private static final String BAD_ARGUMENT_MESSAGE = "参数有误！";
    
    /**
     * TODO extends  ResponseEntityExceptionHandler
     * see: org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldError> fieldErrors = ex.getFieldErrors();
        TreeMap<String, String> errorMap = new TreeMap<>();
        fieldErrors.forEach(fieldError -> errorMap.put(fieldError.getField(), fieldError.getDefaultMessage()));
        
        return RestResponse.errorWithData(HttpStatus.BAD_REQUEST, request, BAD_ARGUMENT_MESSAGE, errorMap);
    }
    
    /**
     * 针对http状态码异常的通用方法，通过ResponseEntity来处理
     *
     * @param ex      异常
     * @param request 请求
     * @return 通过返回ResponseEntity来指定返回的http 状态码
     */
    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<RestResponse<?>> handleHttpStatusException(HttpStatusException ex, HttpServletRequest request) {
        RestResponse<?> error = RestResponse.error(ex.getStatus(), request, ex.getMessage());
        return new ResponseEntity<>(error, ex.getStatus());
        
    }
    
    @Operation(hidden = true)
    @ExceptionHandler({
            JsonParseException.class,
            SuperException.class,
            MethodArgumentTypeMismatchException.class,
            AuthenticationException.class,
            MaxUploadSizeExceededException.class,
            HttpMessageNotReadableException.class
    })
    public RestResponse<?> handleManyException(Exception ex, HttpServletRequest request) {
        
        log.error("handleManyException:{},{}", request.toString(), ex.getMessage());
        RestResponse<?> error = RestResponse.error(HttpStatus.BAD_REQUEST, request, ex.getMessage());
        if (ex instanceof JsonParseException) {
            error.message = "Json 格式化错误";
        } else if (ex instanceof MaxUploadSizeExceededException) {
            error.message = "文件太大了，不可以超过" + maxFileSize + "哦";
        }
        return error;
    }
    
    
}
