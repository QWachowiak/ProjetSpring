package fr.sgr.formation.voteapp.vote.services;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.election.modele.Election;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.vote.modele.ValeurVote;
import fr.sgr.formation.voteapp.vote.modele.Vote;
import fr.sgr.formation.voteapp.vote.services.VoteInvalideException.ErreurVote;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VotesServices {

	@Autowired
	private EntityManager entityManager;

	/**
	 * 
	 * @param votant
	 * @param valeur
	 * @param election
	 * @throws VoteInvalideException
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void creerVote(Utilisateur votant, ValeurVote valeur, Election election) throws VoteInvalideException {

		/**
		 * On commence par vérifier que "votant" est non null. Puis on crée une
		 * trace (tracesServices.init(...)). Puis on vérifie que l'élection
		 * existe. Puis on vérifie qu'elle n'est pas cloturée. Puis on vérifie
		 * que l'utilisateur n'a pas déjà voté. Puis on créer l'objet vote à
		 * partir de la valeur du vote, du votant et de l'élection. Puis on MAJ
		 * la trace en disant que ça marche. Puis on ajoute le vote à
		 * l'élection.
		 */

		/** Vérification que le votant existe **/
		if (votant == null) {
			throw new VoteInvalideException(ErreurVote.VOTANT_ABSENT);
		}
		/** Vérification que l'élection existe */
		if (election == null) {
			throw new VoteInvalideException(ErreurVote.ELECTION_INEXISTANTE);
		}
		/** Vérification que l'élection n'est pas cloturée **/
		if (election.isCloture() == true) {
			throw new VoteInvalideException(ErreurVote.ELECTION_FERMEE);
		}
		/** Vérification que l'utilisateur n'a pas voté **/
		if (election.getVotes().contains(votant) == true) {
			throw new VoteInvalideException(ErreurVote.VOTANT_DEJAVOTE);
		}
		/** Création du vote **/
		Vote vote = new Vote();
		vote.setUtilisateur(votant);
		vote.setValeurvote(valeur);
		election.getVotes().add(vote);
		log.info("=====> L'utilisateur de login {} a voté pour l'élection d'id {}", votant.getLogin(),
				election.getId());

	}

}