package com.yuriytkach.demo.demoftspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

@SpringBootApplication
public class DemoFtSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoFtSpringApplication.class, args);
	}

	@ControllerAdvice
	public static class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {
		@ExceptionHandler(ConstraintViolationException.class)
		public ProblemDetail handle(final ConstraintViolationException ex) {
			final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
			problemDetail.setTitle("Bad Request");
			return problemDetail;
		}
	}
}
