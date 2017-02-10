package fr.sgr.formation.voteapp.vote.services;

import java.util.Date;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.election.modele.Election;
import fr.sgr.formation.voteapp.utilisateurs.services.DroitAccesException;
import fr.sgr.formation.voteapp.vote.modele.Vote;

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
	public void creerVote(Vote nouveauVote, Election election) {
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
