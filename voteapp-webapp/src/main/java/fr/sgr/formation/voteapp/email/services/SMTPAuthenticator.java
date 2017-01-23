package fr.sgr.formation.voteapp.email.services;

import javax.mail.PasswordAuthentication;

public class SMTPAuthenticator extends javax.mail.Authenticator {
	public PasswordAuthentication getPasswordAuthentication() {
		String username = EmailServices.SMTP_AUTH_USER;
		String password = EmailServices.SMTP_AUTH_PWD;
		return new PasswordAuthentication(username, password);
	}
}
