package fr.sgr.formation.voteapp.vote.services;

import java.util.Date;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.election.modele.Election;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.services.DroitAccesException;
import fr.sgr.formation.voteapp.vote.modele.ValeurVote;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VotesServices {

	@Autowired
	private EntityManager entityManager;

	/**
	 * Crée un vote pour une élection
	 * 
	 * @param vote
	 * @param election
	 * @throws DroitAccesException
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void creerVote(Utilisateur votant, ValeurVote valeur, Election election) {

		/**
		 * On commence par vérifier que "votant" est non null. Puis on crée une
		 * trace (tracesServices.init(...)). Puis on vérifie que l'élection
		 * existe. Puis on vérifie qu'elle n'est pas cloturée. Puis on vérifie
		 * que l'utilisateur n'a pas déjà voté. Puis on créer l'objet vote à
		 * partir de la valeur du vote, du votant et de l'élection. Puis on MAJ
		 * la trace en disant que ça marche. Puis on ajoute le vote à
		 * l'élection.
		 */

		Date today = new Date();
		// on regarde si la date du jour est avant la date de fin de l'élection.
		// Si l'élection n'existe pas, on devrait avoir une nullpointer
		// exception
		if (election.getDateDeFin().compareTo(today) > 0) { // voir la
															// compatibilité
															// entre date.util
															// et
															// temporaltype.date
			// dans ce cas on ajoute le vote
			election.getVotes().add(nouveauVote);
			entityManager.persist(nouveauVote);
			log.info("=====> A voté pour l'élection", election);
		} else { // sinon pas de vote
			log.info("=====> Tentative de vote pour une élection avortée");
		}
	}

}
