package com.phemex.dataFactory.controller.handler;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.phemex.dataFactory.controller.handler.annotation.NoResultHolder;
import com.phemex.dataFactory.request.ResultHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统一处理返回结果集
 */
@RestControllerAdvice(value = {"com.phemex.dataFactory"})
public class ResultResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private static final Logger log = LoggerFactory.getLogger(ResultResponseBodyAdvice.class);

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType) || StringHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        // 处理空值
        if (o == null && StringHttpMessageConverter.class.isAssignableFrom(converterType)) {
            return null;
        }

        if (methodParameter.hasMethodAnnotation(NoResultHolder.class)) {
            return o;
        }

        if (!(o instanceof ResultHolder)) {
            if (o instanceof String) {
                return JSON.toJSONString(ResultHolder.success(o));
            }
            return ResultHolder.success(o);
        }
        return o;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResultHolder> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        log.warn("Missing Request Parameter: ", ex);
        String message = String.format("The '%s' parameter is missing.", ex.getParameterName());
        ResultHolder errorResult = ResultHolder.error(message);
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    // 优化异常处理方法
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResultHolder> handleAllExceptions(Exception ex) {
        log.error("Unhandled exception occurred: ", ex);
        String message = "An unexpected error occurred.";
        ResultHolder errorResult = ResultHolder.error(message);
        return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ResultHolder errorResponse = ResultHolder.error("Missing required argument", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();

        ResultHolder errorResponse = ResultHolder.error("Invalid method argument", error);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        Throwable mostSpecificCause = ex.getMostSpecificCause();

        if (mostSpecificCause instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) mostSpecificCause;
            String field = invalidFormatException.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining("."));
            responseBody.put("field", field);
            responseBody.put("rejectedValue", invalidFormatException.getValue());
            responseBody.put("error", "The parameter value is not within the enum range");
        } else {
            responseBody.put("error", "Bad request");
        }

        ResultHolder errorResponse = ResultHolder.error("Invalid method argument", responseBody);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
