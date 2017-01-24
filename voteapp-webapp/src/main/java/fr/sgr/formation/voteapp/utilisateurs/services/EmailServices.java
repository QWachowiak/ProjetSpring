package fr.sgr.formation.voteapp.utilisateurs.services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.notifications.services.NotificationsServices;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class EmailServices {
	/** Services de notification des événements. */
	@Autowired
	private NotificationsServices notificationsServices;

	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	static final String SMTP_AUTH_USER = "projetspringensai@gmail.com";
	static final String SMTP_AUTH_PWD = "ensai2017";

	/**
	 * Envoie un mail avec mot de passe généré automatiquement à l'utilisateur.
	 * 
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void renouvellerMotDePasse(Utilisateur utilisateur, String nouveauMdp) throws Exception {
		log.info("=====> Envoi d'un nouveau mot de passe pour l'utilisateur de login {}.", utilisateur.getLogin());

		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", "587");

		Authenticator auth = new SMTPAuthenticator();
		Session mailSession = Session.getDefaultInstance(props, auth);
		// décommenter pour débugger en console
		// mailSession.setDebug(true);
		Transport transport = mailSession.getTransport();

		MimeMessage message = new MimeMessage(
				mailSession);
		message.setContent("Vous avez demandé un renouvellement de mot de passe. Votre nouveau mot de passe est : "
				+ nouveauMdp + "<br />"
				+ "Vous pouvez l'utiliser dès maintenant pour vous authentifier sur VoteApp.",
				"text/html; charset=utf-8");
		message.setSubject("Vote App - Renouvellement du mot de passe");
		message.setFrom(new InternetAddress("projetspringensai@gmail.com"));
		// Adresse à renseigner plus tard : utilisateur.getEmail()
		message.addRecipient(Message.RecipientType.TO, new InternetAddress("quentin.wachowiak@wanadoo.fr"));

		transport.connect();
		transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		transport.close();
	}

	/**
	 * Méthode qui génère un mot de passe aléatoire.
	 * 
	 * @return Le mot de passe
	 */
	public String genererMotDePasse() {
		String characteres = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		String mdp = "";
		for (int i = 0; i < 8; i++) {
			int position = (int) Math.floor(Math.random() * characteres.length());
			mdp = mdp + characteres.charAt(position);
		}
		return mdp;
	}
}
