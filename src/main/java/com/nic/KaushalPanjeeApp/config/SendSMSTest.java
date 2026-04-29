package com.nic.KaushalPanjeeApp.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SendSMSTest {
	private static final String USER_ID = "ddugky.otp";// "mnrlm.otp";
	private static final String PASSWD = "T6%40e5%248D";// "Ty%40%2312Qa";//Ty@#12Qa, T6@e5$8D --

	public TrustManager[] get_trust_mgr() {
		TrustManager[] certs = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String t) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String t) {
			}
		} };
		return certs;
	}

	public String sendSMS(SMSBean smsBean) {

		String result = null;

		/*********************************************/
		try {
			String https_url = "https://smsgw.sms.gov.in/failsafe/HttpLink?username=" + USER_ID + "&pin=" + PASSWD
					+ "&";

			String replyTo = "DDUGKY";
			String recipient = smsBean.getMobile();
			String messageBody = smsBean.getSms();
			String dlt_entity_id = "1001187746616357554";
			String dlt_template_id = smsBean.getTemplateId();

			messageBody = URLEncoder.encode(messageBody, "UTF-8");

			StringBuffer URI = new StringBuffer();
			URI.append(https_url);
			URI.append("signature=" + replyTo);
			URI.append("&mnumber=" + recipient);
			URI.append("&message=" + messageBody);
			URI.append("&dlt_entity_id=" + dlt_entity_id);
			URI.append("&dlt_template_id=" + dlt_template_id);
			result = "";
			System.err.println("sms https_url:-" + URI.toString());

			SSLContext ssl_ctx = SSLContext.getInstance("TLS");
			TrustManager[] trust_mgr = get_trust_mgr();
			ssl_ctx.init(null, // key manager
					trust_mgr, // trust manager
					new SecureRandom()); // random number generator
			HttpsURLConnection.setDefaultSSLSocketFactory(ssl_ctx.getSocketFactory());
			URL url = null;

			/*
			 * URI uri = new URI(URI.toString());
			 * 
			 * // Convert URI to URL url = uri.toURL();
			 */

			url = new URL(URI.toString());
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			// Print results
			result = sb.toString();
			// System.out.println("Result:" + result);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public String encodeMsg(String msg) {

		String hexString = "";
		for (char ch : msg.trim().toCharArray()) {
			hexString = hexString.trim().concat(String.format("%04x", (int) ch));
		}
		return hexString;
	}
}
