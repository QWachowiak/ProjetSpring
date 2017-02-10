package fr.sgr.formation.voteapp.vote.services;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.election.modele.Election;
import fr.sgr.formation.voteapp.traces.modele.Trace;
import fr.sgr.formation.voteapp.traces.modele.TypeAction;
import fr.sgr.formation.voteapp.traces.services.TracesServices;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.vote.modele.ValeurVote;
import fr.sgr.formation.voteapp.vote.modele.Vote;
import fr.sgr.formation.voteapp.vote.services.VoteInvalideException.ErreurVote;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class VotesServices {

	@Autowired
	EntityManager entityManager;

	@Autowired
	TracesServices tracesServices;

	/**
	 * Gère l'enregistrement d'un vote dans le système.
	 * 
	 * @param votant
	 *            Utilisateur qui vote.
	 * @param valeur
	 *            Valeur du vote.
	 * @param election
	 *            Election à laquelle l'utilisateur vote.
	 * @throws VoteInvalideException
	 *             Levée si l'action de vote n'est pas valide.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void creerVote(Utilisateur votant, ValeurVote valeur, Election election) throws VoteInvalideException {

		/** Vérification que le votant existe **/
		if (votant == null) {
			throw new VoteInvalideException(ErreurVote.VOTANT_ABSENT);
		}

		Trace trace = tracesServices.init(votant, TypeAction.VOTE);

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

		trace.setResultat("Vote OK");
		trace.setDescription(
				"L'utilisateur de login " + votant.getLogin() + " a voté pour l'élection d'id " + election.getId());

		entityManager.persist(vote);
		election.getVotes().add(vote);
		log.info("=====> L'utilisateur de login {} a voté pour l'élection d'id {}", votant.getLogin(),
				election.getId());

	}

}