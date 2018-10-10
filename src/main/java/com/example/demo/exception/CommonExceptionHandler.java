package com.example.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class CommonExceptionHandler {
    /**
     * 拦截Exception类的异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String, Object> exceptionHandler(Exception e) {
        log.info("Exception: {}({})", e.getMessage(), e.hashCode());
        Map<String, Object> result = new HashMap<>();
        result.put("respCode", e.hashCode());
        result.put("respMsg", e.getMessage());
        return result;
    }
}
