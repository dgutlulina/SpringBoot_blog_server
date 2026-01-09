package lln.spring.exception;

import lln.spring.tools.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    public Result handleMultipartException(MultipartException e) {
        return Result.error("文件上传错误: " + e.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public Result handleIOException(IOException e) {
        return Result.error("文件操作错误: " + e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result handleIllegalArgumentException(IllegalArgumentException e) {
        return Result.error("参数错误: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace();
        return Result.error("系统错误: " + e.getMessage());
    }
}