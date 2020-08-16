package infrrd.ai.filehandler.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import infrrd.ai.filehandler.service.StorageService;

@RestController
public class FileController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

	@Autowired
	private StorageService storageService;

	@GetMapping("/file/{filename:.+}")
	public ResponseEntity<Resource> downloadFile(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("filename") String filename) {
		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		LOGGER.info("Inside " + methodName);
		LOGGER.info("Requested file: " + filename);
		Resource resource = storageService.loadAsResource(filename, response);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);

	}

	@DeleteMapping("/file/{filename:.+}")
	public void deletFile(@PathVariable("filename") String filename) {
		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		LOGGER.info("Inside " + methodName);
		LOGGER.info("File to be deleted: " + filename);
		storageService.delete(filename);
	}

	@PostMapping("/file/{filename:.+}")
	public void copyFile(@PathVariable("filename") String filename) {
		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		LOGGER.info("Inside " + methodName);
		LOGGER.info("File to be deleted: " + filename);
		storageService.copyFile(filename);
	}
}
