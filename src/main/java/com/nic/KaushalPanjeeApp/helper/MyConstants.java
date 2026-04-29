package com.nic.KaushalPanjeeApp.helper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class MyConstants {
	public static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String DIGITS = "0123456789";
	public static final String SPECIAL_CHARACTERS = "!@#$%&*";
	public static final String ALL_CHARACTERS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARACTERS;
	public static final SecureRandom random = new SecureRandom();

	public static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>])(?=.*[0-9]).{10,12}$";

	public static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
	public static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	/* ==================================Live=================================== */
 // /*	
	public static final String UPLOAD_ADHAAR = "/docs/FileFolder/KAUSHAL_PANJEE/aadhar/";
	public static final String UPLOAD_VOTER = "/docs/FileFolder/KAUSHAL_PANJEE/voter/";
	public static final String UPLOAD_DL = "/docs/FileFolder/KAUSHAL_PANJEE/dl/";
	public static final String UPLOAD_CAST = "/docs/FileFolder/KAUSHAL_PANJEE/cast/";
	public static final String UPLOAD_MINORITY = "/docs/FileFolder/KAUSHAL_PANJEE/minority/";
	public static final String UPLOAD_DISABLITY = "/docs/FileFolder/KAUSHAL_PANJEE/disablity/";
	public static final String UPLOAD_NREGA = "/docs/FileFolder/KAUSHAL_PANJEE/nrega/";
	public static final String UPLOAD_RASAN = "/docs/FileFolder/KAUSHAL_PANJEE/ration/";
	public static final String UPLOAD_RSBY = "/docs/FileFolder/KAUSHAL_PANJEE/rsby/";
	public static final String UPLOAD_PIP = "/docs/FileFolder/KAUSHAL_PANJEE/pip/";
	public static final String UPLOAD_PMAYG = "/docs/FileFolder/KAUSHAL_PANJEE/pmayg/";
	public static final String UPLOAD_RESIDENT = "/docs/FileFolder/KAUSHAL_PANJEE/resident/";
	public static final String UPLOAD_SHG = "/docs/FileFolder/KAUSHAL_PANJEE/shg/";
	public static final String UPLOAD_CENTER = "/docs/FileFolder/KAUSHAL_PANJEE/center/";
	public static final String BANNER = "/docs/FileFolder/KAUSHAL_PANJEE/banner/";
	public static final String TRAINING_CENTER_BUILDING = "/docs/FileFolder/INSTITUTE/RSETI_BUILDING/";
	public static final String TRAINING_CENTER_CLASS_ROOM1 = "/docs/FileFolder/INSTITUTE/CLASS_ROOM1/";
	public static final String TRAINING_CENTER_CLASS_ROOM2 = "/docs/FileFolder/INSTITUTE/CLASS_ROOM2/";
	public static final String TRAINING_CENTER_DORMITORY_LADIES = "/docs/FileFolder/INSTITUTE/DORMITORY_LADIES/";
	public static final String TRAINING_CENTER_DORMITORY_GENTS = "/docs/FileFolder/INSTITUTE/DORMITORY_GENTS/";
//	*/

	/* ==================================Demo=================================== */

	/*
	public static final String UPLOAD_ADHAAR = "/docs/FileFolderDemo/KAUSHAL_PANJEE/aadhar/";
	public static final String UPLOAD_VOTER = "/docs/FileFolderDemo/KAUSHAL_PANJEE/voter/";
	public static final String UPLOAD_DL = "/docs/FileFolderDemo/KAUSHAL_PANJEE/dl/";
	public static final String UPLOAD_CAST = "/docs/FileFolderDemo/KAUSHAL_PANJEE/cast/";
	public static final String UPLOAD_MINORITY = "/docs/FileFolderDemo/KAUSHAL_PANJEE/minority/";
	public static final String UPLOAD_DISABLITY = "/docs/FileFolderDemo/KAUSHAL_PANJEE/disablity/";
	public static final String UPLOAD_NREGA = "/docs/FileFolderDemo/KAUSHAL_PANJEE/nrega/";
	public static final String UPLOAD_RASAN = "/docs/FileFolderDemo/KAUSHAL_PANJEE/ration/";
	public static final String UPLOAD_RSBY = "/docs/FileFolderDemo/KAUSHAL_PANJEE/rsby/";
	public static final String UPLOAD_PIP = "/docs/FileFolderDemo/KAUSHAL_PANJEE/pip/";
	public static final String UPLOAD_PMAYG = "/docs/FileFolderDemo/KAUSHAL_PANJEE/pmayg/";
	public static final String UPLOAD_SHG = "/docs/FileFolderDemo/KAUSHAL_PANJEE/shg/";
	public static final String UPLOAD_RESIDENT = "/docs/FileFolderDemo/KAUSHAL_PANJEE/resident/";
	public static final String UPLOAD_CENTER = "/docs/FileFolderDemo/KAUSHAL_PANJEE/center/";
	public static final String BANNER = "/docs/FileFolderDemo/KAUSHAL_PANJEE/banner/";

	public static final String TRAINING_CENTER_BUILDING = "/docs/FileFolderDemo/INSTITUTE/RSETI_BUILDING/";
	public static final String TRAINING_CENTER_CLASS_ROOM1 = "/docs/FileFolderDemo/INSTITUTE/CLASS_ROOM1/";
	public static final String TRAINING_CENTER_CLASS_ROOM2 = "/docs/FileFolderDemo/INSTITUTE/CLASS_ROOM2/";
	public static final String TRAINING_CENTER_DORMITORY_LADIES = "/docs/FileFolderDemo/INSTITUTE/DORMITORY_LADIES/";
	public static final String TRAINING_CENTER_DORMITORY_GENTS = "/docs/FileFolderDemo/INSTITUTE/DORMITORY_GENTS/";
//	*/

	/*====================================Local====================================*/
	/*
	 * public static final String UPLOAD_ADHAAR =
	 * "E:/UploadKP/FileFolder/images/aadhar/"; // File path for local public static
	 * final String UPLOAD_VOTER = "E:/UploadKP/FileFolder/images/voter/"; // File
	 * path for local public static final String UPLOAD_DL =
	 * "E:/UploadKP/FileFolder/images/dl/"; // File path for local public static
	 * final String UPLOAD_CAST = "E:/UploadKP/FileFolder/images/cast/"; // File
	 * path for local public static final String UPLOAD_MINORITY =
	 * "E:/UploadKP/FileFolder/images/minority/"; // File path for local public
	 * static final String UPLOAD_DISABLITY =
	 * "E:/UploadKP/FileFolder/images/disablity/"; // File path for local public
	 * static final String UPLOAD_NREGA = "E:/UploadKP/FileFolder/images/nrega/"; //
	 * File path for local public static final String UPLOAD_RASAN =
	 * "E:/UploadKP/FileFolder/images/ration/"; // File path for local public static
	 * final String UPLOAD_RSBY = "E:/UploadKP/FileFolder/images/rsby/"; // File
	 * path for local public static final String UPLOAD_PIP =
	 * "E:/UploadKP/FileFolder/images/pip/"; public static final String UPLOAD_PMAYG
	 * = "E:/UploadKP/FileFolder/images/pmayg/"; public static final String
	 * UPLOAD_SHG = "E:/UploadKP/FileFolder/images/shg/"; public static final String
	 * UPLOAD_RESIDENT = "E:/UploadKP/FileFolder/images/resident/"; // File path for
	 * local public static final String UPLOAD_CENTER =
	 * "E:/UploadKP/FileFolder/images/center/"; // File path for local public static
	 * final String BANNER = "E:/UploadKP/FileFolder/images/banner/"; // File path
	 * for local public static final String TRAINING_CENTER_BUILDING =
	 * "E:/UploadKP/FileFolder/images/INSTITUTE/RSETI_BUILDING/"; public static
	 * final String TRAINING_CENTER_CLASS_ROOM1 =
	 * "E:/UploadKP/FileFolder/images/INSTITUTE/CLASS_ROOM1/"; public static final
	 * String TRAINING_CENTER_CLASS_ROOM2 =
	 * "E:/UploadKP/FileFolder/images/INSTITUTE/CLASS_ROOM2/"; public static final
	 * String TRAINING_CENTER_DORMITORY_LADIES =
	 * "E:/UploadKP/FileFolder/images/INSTITUTE/DORMITORY_LADIES/"; public static
	 * final String TRAINING_CENTER_DORMITORY_GENTS =
	 * "E:/UploadKP/FileFolder/images/INSTITUTE/DORMITORY_GENTS/"; //
	 */

	public static final String dataNameErr = "No data available.";
	public static final int dataCodeErr = 202;

	public static final String appVersionNameErr = "Please upgrade your app first.";
	public static final int appVersionCodeErr = 301;

	public static char randomChar(String characterSet) {
		return characterSet.charAt(MyConstants.random.nextInt(characterSet.length()));
	}

	public static String shuffleString(String input) {
		char[] array = input.toCharArray();
		for (int i = array.length - 1; i > 0; i--) {
			int index = MyConstants.random.nextInt(i + 1);
			char temp = array[index];
			array[index] = array[i];
			array[i] = temp;
		}
		return new String(array);
	}

	public static String randomNo() {
		Random randomNo = new Random();
		StringBuilder oneTimePassword = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			int randomNumber = randomNo.nextInt(10);
			oneTimePassword.append(randomNumber);
		}
		String oneTimePasswrd = oneTimePassword.toString().trim();
		return oneTimePasswrd;
	}

	public static MultipartFile convertToMultipartFile(byte[] bytes, String fileExtension) {
		return new MultipartFile() {
			@Override
			public String getName() {
				return "file";
			}

			@Override
			public String getOriginalFilename() {
				return "file" + fileExtension; // e.g., "file.jpg"
			}

			@Override
			public String getContentType() {
				return getContentTypeByExtension(fileExtension); // Determine content type based on the extension
			}

			@Override
			public boolean isEmpty() {
				return bytes.length == 0;
			}

			@Override
			public long getSize() {
				return bytes.length;
			}

			@Override
			public byte[] getBytes() {
				return bytes;
			}

			@Override
			public InputStream getInputStream() {
				return new ByteArrayInputStream(bytes);
			}

			@Override
			public void transferTo(File dest) throws IOException {
				try (OutputStream os = new FileOutputStream(dest)) {
					os.write(bytes);
				}
			}

			private String getContentTypeByExtension(String extension) {
				switch (extension.toLowerCase()) {
				case ".jpg":
				case ".jpeg":
					return "image/jpeg";
				case ".png":
					return "image/png";
				case ".gif":
					return "image/gif";
				case ".pdf":
					return "application/pdf";
				default:
					return "application/octet-stream"; // Fallback for unknown types
				}
			}
		};
	}

	public static String detectFileType(byte[] data) {
		if (data.length < 4) {
			return ".jpeg";
		}
		// Check for JPEG
		if (data[0] == (byte) 0xFF && data[1] == (byte) 0xD8) {
			return ".jpg";
		}
		// Check for PNG
		if (data[0] == (byte) 0x89 && data[1] == (byte) 0x50 && data[2] == (byte) 0x4E && data[3] == (byte) 0x47) {
			return ".png";
		}
		// Check for PDF
		if (data.length >= 4 && data[0] == (byte) 0x25 && data[1] == (byte) 0x50 && data[2] == (byte) 0x44
				&& data[3] == (byte) 0x46) {
			return ".pdf";
		}
		return "4044";
	}

	public static String getFileExtension(String fileName) {
		if (fileName == null || fileName.isEmpty()) {
			return ""; // Return empty if the filename is null or empty
		}

		int lastIndexOfDot = fileName.lastIndexOf('.');
		if (lastIndexOfDot == -1 || lastIndexOfDot == 0) {
			return ""; // No extension found
		}

		return fileName.substring(lastIndexOfDot + 1); // Get the extension
	}

	public static String getImageAsBase64(String filePath) {
		try {
			Path path = Paths.get(filePath);
			byte[] fileBytes = Files.readAllBytes(path);
			return Base64.getEncoder().encodeToString(fileBytes);
		} catch (NoSuchFileException e) {
			// System.err.println("File not found: " + e.getMessage());
			return null;
		} catch (IOException e) {
			// System.err.println("IOException: " + e.getMessage());
			return null;
		}
	}

}
