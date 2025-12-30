package com.frank.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@ControllerAdvice
//@ResponseBody
//全域異常處理

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ArithmeticException.class)
	public String error(ArithmeticException exception) {
		System.out.println("發生了數學運算異常" + exception);

//傳回這些進行錯誤處理；
//ProblemDetail：
//ErrorResponse ：

		return "炸了，哈哈...";
	}
}