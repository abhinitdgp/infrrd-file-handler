package infrrd.ai.filehandler.service;

import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;

public interface StorageService {
	void init() throws IOException;

	Path load(String filename);

	void delete(String filename);

	Resource loadAsResource(String filename, HttpServletResponse response);

	String copyFile(String filename);

}
