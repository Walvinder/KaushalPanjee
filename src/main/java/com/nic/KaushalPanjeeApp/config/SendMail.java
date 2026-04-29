package com.nic.KaushalPanjeeApp.config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class SendMail {
	public void sendMail(String p_to, String p_subject, String p_message) throws Exception {
		try {
			String E_MAIL = "grameen.kaushal@nic.in";
			String host = "smtpgwhyd.nic.in";// or IP address

			/* =====new code===== */
			Properties p = System.getProperties();
			p.put("mail.smtp.host", host);

			p.put("mail.transport.protocol", "smtp");
			p.put("mail.smtp.ssl.protocols", "TLSv1.2");
			p.put("mail.smtp.debug", "true");

			Session mailSession = Session.getInstance(p);
			mailSession.setDebug(false);

			MimeMessage m = new MimeMessage(mailSession);
			m.setFrom(new InternetAddress(E_MAIL));
			m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(p_to));
			m.setSentDate(new Date());
			m.setSubject(p_subject);
			MimeMultipart mailBody = new MimeMultipart();
			MimeBodyPart mainBody = new MimeBodyPart();
			mainBody.setText(p_message);
			mailBody.addBodyPart(mainBody);
			m.setContent(mailBody);
			m.setContent(p_message, "text/html");

			Transport.send(m, m.getAllRecipients());

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			throw e;

		}

	}
}
