package uk.ac.dotrural.irp.ecosystem.transport;

import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailHandler {

	private static final String BUNDLE_NAME = "uk.ac.dotrural.irp.ecosystem.transport.email";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public boolean sendActivationEmail(String name, String to, String token) {
		return sendEmail(
				to, // Now set the actual message
				RESOURCE_BUNDLE.getString("email.activation.subject"),
				String.format(
						RESOURCE_BUNDLE.getString("email.activation.template"),
						name, token, to, token, to));
	}

	private boolean sendEmail(String to, String subject, String contents) {
		// Setup mail server
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host",
				RESOURCE_BUNDLE.getString("email.smtp.host"));
//		properties.remove("mail.smtp.auth");
//		properties.remove("mail.smtp.starttls.enable");
//		properties.remove("mail.smtp.port");
//		properties.put("mail.smtp.auth", "false");
//		properties.put("mail.smtp.starttls.enable", "false");
//		properties.put("mail.smtp.port",
//				RESOURCE_BUNDLE.getString("email.smtp.port"));

		 
		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);
//				new javax.mail.Authenticator() {
//					protected PasswordAuthentication getPasswordAuthentication() {
//						return new PasswordAuthentication(RESOURCE_BUNDLE
//								.getString("email.user"), RESOURCE_BUNDLE
//								.getString("email.pass"));
//					}
//				});

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);
			// Set From: header field of the header.
			message.setFrom(new InternetAddress(RESOURCE_BUNDLE
					.getString("email.from")));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));

			// Set Subject: header field
			message.setSubject(subject);
			message.setText(contents);

			// Send message
			Transport.send(message);
			return true;
		} catch (MessagingException mex) {
			mex.printStackTrace(System.err);
			return false;
		}
	}

	public boolean sendFeedbackemail(String message) {
		return sendEmail("getthere@abdn.ac.uk",
				RESOURCE_BUNDLE.getString("email.feedback.subject"),
				String.format(
						RESOURCE_BUNDLE.getString("email.feedback.template"),
						message));
	}
}
