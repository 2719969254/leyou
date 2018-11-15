package com.leyou.common.advice;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2018/11/15
 */
@ControllerAdvice
public class CommonExceptionHandler {
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ExceptionResult> handleException(LyException e){
		ExceptionEnum exceptionEnum = e.getExceptionEnum();
		return ResponseEntity.status(exceptionEnum.getCode()).body(new ExceptionResult(e.getExceptionEnum()));
	}
}
