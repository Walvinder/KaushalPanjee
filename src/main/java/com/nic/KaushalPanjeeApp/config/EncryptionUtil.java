package com.nic.KaushalPanjeeApp.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

	public static final String ENCRYPT_IV_KEY = "$10A80$10A80$10A";
	// public static final String ENCRYPT_KEY = "$10A80$10A80$10A$10A80$10A80$10A";
	public static final String ENCRYPT_KEY = "$10A80$10A80$10A";

	private static final String PASSALGORITHM = "SHA-512";

	private static final String MASTER_KEY = "$A4E@TN2#V";
	public static final String MASTER_IV_KEY = "$R2B@JKD4#WJ7PK&";
	private static final String ALGORITHM = "AES/CBC/PKCS5PADDING";

	// Convert master key to 256-bit secret key using SHA-256
	private static SecretKeySpec getKeyFromMaster() throws NoSuchAlgorithmException {
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		byte[] keyBytes = sha.digest(MASTER_KEY.getBytes(StandardCharsets.UTF_8));
		return new SecretKeySpec(keyBytes, "AES");
	}

	/* ============ AES-256 Encryption ================== */
	public String encryptNew(String data) throws Exception {
		SecretKeySpec keySpec = getKeyFromMaster();
		// System.out.println("Key length (bits): " + (keySpec.getEncoded().length *
		// 8));
		IvParameterSpec ivSpec = new IvParameterSpec(MASTER_IV_KEY.getBytes(StandardCharsets.UTF_8));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(encrypted);
	}

	/* ============ AES-256 Decryption ================== */
	public String decryptNew(String encryptedData) throws Exception {
		SecretKeySpec keySpec = getKeyFromMaster();
		IvParameterSpec ivSpec = new IvParameterSpec(MASTER_IV_KEY.getBytes(StandardCharsets.UTF_8));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
		return new String(decrypted, StandardCharsets.UTF_8);
	}

	public String encrypt(String data) throws Exception {
		SecretKeySpec keySpec = new SecretKeySpec(ENCRYPT_KEY.getBytes(StandardCharsets.UTF_8), "AES");
		// System.out.println("Key length (bits): " + (keySpec.getEncoded().length *
		// 8));
		IvParameterSpec ivSpec = new IvParameterSpec(ENCRYPT_IV_KEY.getBytes(StandardCharsets.UTF_8));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	public String decrypt(String encryptedData) throws Exception {
		SecretKeySpec keySpec = new SecretKeySpec(ENCRYPT_KEY.getBytes(StandardCharsets.UTF_8), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(ENCRYPT_IV_KEY.getBytes(StandardCharsets.UTF_8));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
		return new String(decryptedBytes, StandardCharsets.UTF_8);
	}

	public String hash(String data) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(PASSALGORITHM);
		byte[] hashedBytes = digest.digest(data.getBytes());

		// Convert byte array to hex string
		StringBuilder hexString = new StringBuilder();
		for (byte b : hashedBytes) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

}
