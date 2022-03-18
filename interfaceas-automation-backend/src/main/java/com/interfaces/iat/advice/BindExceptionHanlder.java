package com.interfaces.iat.advice;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interfaces.iat.dto.common.ResponseData;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
public class BindExceptionHanlder {

    /**
     * 拦截BindException
     * @param ex
     * @param response
     * @throws IOException
     */
    @ExceptionHandler(BindException.class)
    public void handleBindException(BindException ex, HttpServletResponse response) throws IOException {
        List<FieldError> fieldErrors = ex.getFieldErrors();
        List<String> errors = new ArrayList<>();
        fieldErrors.stream().forEach(s->{
//            errors.add(s.getField()+s.getDefaultMessage());
            errors.add(s.getDefaultMessage());
        });

        ResponseData responseData = new ResponseData();
        responseData.setCode(996);
        responseData.setMessage(errors.stream().collect(Collectors.joining(",")));
        String json = new ObjectMapper().writeValueAsString(responseData);
        response.setStatus(996);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
    }
}
