package com.swisscom.userregister.controller.handler;

import com.swisscom.userregister.domain.exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String METHOD_ARGUMENT_ERROR_CODE = "001";
    private static final String BUSINESS_ERROR_CODE = "002";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ExceptionObject> handleMethodArgumentException(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(i -> new ExceptionObject(METHOD_ARGUMENT_ERROR_CODE, i.getDefaultMessage()))
                .toList();
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ExceptionObject handleBusinessException(BusinessException ex) {
        return new ExceptionObject(BUSINESS_ERROR_CODE, ex.getMessage());
    }


}
