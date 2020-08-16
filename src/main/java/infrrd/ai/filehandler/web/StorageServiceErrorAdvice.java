package infrrd.ai.filehandler.web;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import infrrd.ai.filehandler.entities.ErrorDetails;
import infrrd.ai.filehandler.exception.FileNotFoundException;

@ControllerAdvice
@RestController
public class StorageServiceErrorAdvice extends ResponseEntityExceptionHandler {
	@ExceptionHandler(FileNotFoundException.class)
	public final ResponseEntity<ErrorDetails> handleFileNotFoundException(FileNotFoundException ex,
			WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}
}
