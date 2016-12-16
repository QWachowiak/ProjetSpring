package fr.sgr.formation.voteapp.utilisateurs.services;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UtilisateurService {

	public static List<Utilisateur> listUtilisateurs;

	public void creer(Utilisateur utilisateur) {
		if (utilisateur.getLogin() != null && utilisateur.getNom() != null && utilisateur.getPrenom() != null) {
			if (utilisateur.getLogin().length() < 20 && utilisateur.getNom().length() < 20
					&& utilisateur.getPrenom().length() < 20) {
				listUtilisateurs.add(utilisateur);
				log.info("Appel du service de création des utilisateurs", utilisateur.getLogin(), utilisateur.getNom(),
						utilisateur.getPrenom());
			} else {
				log.info("Erreur");
			}
		} else {
			log.info("Erreur");

		}

	}

	public void lire(String login) {

	}
}
