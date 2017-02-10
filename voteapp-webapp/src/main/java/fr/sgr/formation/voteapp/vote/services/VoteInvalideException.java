package fr.sgr.formation.voteapp.vote.services;

import lombok.Builder;
import lombok.Getter;

/**
 * Exception levée pour indiquer qu'un vote est invalide.
 */
public class VoteInvalideException extends Exception {

	/** Identifie l'erreur. */
	@Getter
	private ErreurVote erreur;

	@Builder
	public VoteInvalideException(ErreurVote erreur, Throwable cause) {
		super(cause);
		this.erreur = erreur;
	}

	public VoteInvalideException(ErreurVote erreur) {
		this.erreur = erreur;
	}

	public enum ErreurVote {
		VOTANT_ABSENT("Le votant n'est pas un utilisateur inscrit sur le système."),
		ELECTION_INEXISTANTE("L'élection pour laquelle vous souhaitez voter n'existe pas."),
		ELECTION_FERMEE("L'élection pour laquelle vous souhaitez voter a déjà été clôturée."),
		VOTANT_DEJAVOTE("Vous avez déjà voté une fois sur cette élection.");

		@Getter
		public String message;

		private ErreurVote(String message) {
			this.message = message;
		}
	}
}