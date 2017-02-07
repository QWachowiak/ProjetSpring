package fr.sgr.formation.voteapp.election.services;

import lombok.Builder;
import lombok.Getter;

/**
 * Exception levée pour indiquer qu'une election est invalide.
 */
public class ElectionInvalideException extends Exception {

	/** Identifie l'erreur. */
	@Getter
	private ErreurElection erreur;

	@Builder
	public ElectionInvalideException(ErreurElection erreur, Throwable cause) {
		super(cause);

		this.erreur = erreur;
	}

	public ElectionInvalideException(ErreurElection erreur) {
		this.erreur = erreur;
	}

	public enum ErreurElection {
		UTILISATEUR_OBLIGATOIRE("L'utilisateur est obligatoire pour effectuer l'opération."),
		TITRE_OBLIGATOIRE("Le titre de l'election est obligatoire."),
		DESCRIPTION_OBLIGATOIRE("La description de l'election est obligatoire."),
		ELECTION_EXISTANT("Une election de même id existe déjà sur le système."),
		GERANT_ORIGINE("Vous n'êtes pas le createur d'origine de l'election");

		@Getter
		public String message;

		private ErreurElection(String message) {
			this.message = message;
		}
	}
}
