package fr.sgr.formation.voteapp.election.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import fr.sgr.formation.voteapp.election.modele.Election;
import fr.sgr.formation.voteapp.election.services.ElectionInvalideException.ErreurElection;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateurInvalideException;

/**
 * Bean mettant à disposition les services permettant de valider les
 * informations d'une election.
 */
@Service
public class ValidationElectionServices {

	/**
	 * Vérifie qu'une election est valide.
	 * 
	 * @param election
	 *            Election à valider.
	 * @return true si l'utilisateur est valide, false si aucun utilisateur
	 *         n'est passé en paramètre.
	 * @throws UtilisateurInvalideException
	 *             Levée si l'utilisateur est invalide.
	 */
	public boolean validerElection(Election election) throws ElectionInvalideException {
		if (election == null) {
			return false;
		}

		validerTitre(election);
		validerDescription(election);

		/** Validation des champs. */
		return true;
	}

	private void validerTitre(Election election) throws ElectionInvalideException {
		if (StringUtils.isBlank(election.getTitre())) {
			throw new ElectionInvalideException(ErreurElection.TITRE_OBLIGATOIRE);
		}
	}

	private void validerDescription(Election election) throws ElectionInvalideException {
		if (StringUtils.isBlank(election.getDescription())) {
			throw new ElectionInvalideException(ErreurElection.DESCRIPTION_OBLIGATOIRE);
		}
	}
}
