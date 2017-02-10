package fr.sgr.formation.voteapp.utilisateurs.services;

import lombok.Builder;
import lombok.Getter;

/**
 * Exception levée pour indiquer qu'un utilisateur est invalide.
 */
public class DroitAccesException extends Exception {

	/** Identifie l'erreur. */
	@Getter
	private ErreurDroits erreur;

	@Builder
	public DroitAccesException(ErreurDroits erreur, Throwable cause) {
		super(cause);
		this.erreur = erreur;
	}

	public DroitAccesException(ErreurDroits erreur) {
		this.erreur = erreur;
	}

	public enum ErreurDroits {
		ACCES_ADMINISTRATEUR("L'utilisateur doit être un administrateur pour effectuer cette action."),
		ACCES_GERANT("L'utilisateur doit être un gérant d'élection pour effectuer cette action."),
		ACCES_PROPRIETAIRE("L'utilisateur doit être propriétaire de cette élection pour effectuer cette action."),
		ACCES_UTILISATEUR("L'utilisateur doit exister sur le système pour effectuer une action. Sans blague !");

		@Getter
		public String message;

		private ErreurDroits(String message) {
			this.message = message;
		}
	}
}
