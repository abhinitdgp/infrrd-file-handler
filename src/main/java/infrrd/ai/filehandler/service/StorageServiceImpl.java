package infrrd.ai.filehandler.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import infrrd.ai.filehandler.exception.FileNotFoundException;
import infrrd.ai.filehandler.exception.StorageException;
import infrrd.ai.filehandler.property.StorageProperties;

@Service
public class StorageServiceImpl implements StorageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StorageServiceImpl.class);
	private final Path rootLocation;

	@Autowired
	public StorageServiceImpl(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	@PostConstruct
	public void init() throws IOException {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			LOGGER.error(e.toString());
			throw new StorageException("Could not initialize storage location", e);
		}
	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename, HttpServletResponse response) {
		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		LOGGER.info("Inside " + methodName);
		try {
			Path path = load(filename);
			File file = new File(path.toString());

//			if (file.exists()) {
//				String mimeType = URLConnection.guessContentTypeFromName(file.getName());
//				if (mimeType == null) {
//					mimeType = "application/octet-stream";
//				}
//				response.setContentType(mimeType);
//				response.setHeader("Content-Disposition",
//						String.format("attachment; filename=\"" + file.getName() + "\""));
//				response.setContentLength((int) file.length());
//				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
//				FileCopyUtils.copy(inputStream, response.getOutputStream());
//			}

			Resource resource = new UrlResource(path.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new FileNotFoundException("Could not read file: " + filename);
			}
		} catch (Exception e) {
			LOGGER.error(e.toString());
			throw new FileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void delete(String filename) {
		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		LOGGER.info("Inside " + methodName);
		try {
			Path file = load(filename);
			boolean deleted = Files.deleteIfExists(file);
			if (!deleted) {
				throw new FileNotFoundException("Could not find file: " + filename);
			}
		} catch (Exception e) {
			LOGGER.error(e.toString());
			throw new FileNotFoundException("Could not find file: " + filename, e);

		}
	}

	@Override
	public String copyFile(String filename) {
		String methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		LOGGER.info("Inside " + methodName);

		String newFilename = "";
		try {
			String ext = filename.substring(filename.indexOf("."));
			filename = filename.substring(0, filename.indexOf("."));

			newFilename = "";
			Path path = load(filename + ext);
			File oldFile = new File(path.toString());
			if (oldFile.exists()) {
				File newFile = oldFile;

				int version = 1;
				while (newFile.exists()) {
					newFilename = filename + version;
					newFile = new File(load(newFilename + ext).toString());
					version++;
				}

				FileChannel sourceChannel = null;
				FileChannel destChannel = null;
				try {
					sourceChannel = new FileInputStream(oldFile).getChannel();
					destChannel = new FileOutputStream(newFile).getChannel();
					destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
				} catch (java.io.FileNotFoundException e) {
					LOGGER.error("Error" + e);
				} catch (IOException e) {
					LOGGER.error("Error" + e);
				} finally {
					try {
						sourceChannel.close();
						destChannel.close();
					} catch (IOException e) {
						LOGGER.error("Error" + e);
					}

				}
			} else {
				throw new FileNotFoundException("Could not find file: " + filename + ext);
			}
		} catch (Exception e) {
			LOGGER.error(e.toString());
			throw new FileNotFoundException("Could not find file: " + filename, e);
		}

		return newFilename;
	}

}
