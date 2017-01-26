package fr.sgr.formation.voteapp.election.services;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.election.modele.Election;
import fr.sgr.formation.voteapp.election.services.ElectionInvalideException.ErreurElection;
import fr.sgr.formation.voteapp.traces.modele.Trace;
import fr.sgr.formation.voteapp.traces.modele.TypeAction;
import fr.sgr.formation.voteapp.traces.services.TracesServices;
import fr.sgr.formation.voteapp.utilisateurs.modele.ProfilsUtilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.services.DroitAccesException;
import fr.sgr.formation.voteapp.utilisateurs.services.DroitAccesException.ErreurDroits;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class ElectionServices {

	/** Services de validation d'une election. */
	@Autowired
	private ValidationElectionServices validationServices;
	/** Services de notification des événements. */
	@Autowired
	private TracesServices tracesServices;

	@Autowired
	private EntityManager entityManager;

	@Transactional(propagation = Propagation.REQUIRED)
	public Election init(Utilisateur createur, Election nouvelleElection)
			throws ElectionInvalideException, DroitAccesException {

		/** On commence par indiquer qu'une tentative de création a lieu. */
		Trace trace = tracesServices.init(createur, TypeAction.ELEC_CREATION);

		if (!createur.getProfils().contains(ProfilsUtilisateur.GERANT)) {
			throw new DroitAccesException(ErreurDroits.ACCES_GERANT);
		}

		if (createur == null) {
			throw new ElectionInvalideException(ErreurElection.UTILISATEUR_OBLIGATOIRE);
		}

		/**
		 * Validation de l'election: lève une exception si l'election est
		 * invalide.
		 */
		validationServices.validerElection(nouvelleElection);

		/**
		 * On indique dans la trace que la création s'est correctement réalisée.
		 */
		trace.setResultat("Création OK");
		trace.setDescription("Création de l'election d'id " + nouvelleElection.getId()
				+ " par le gérant de login " + createur.getLogin());

		/** Persistance de l'election. */
		entityManager.persist(nouvelleElection);
		log.info("=====> Création de l'election : {}.", nouvelleElection);

		return nouvelleElection;
	}

	/**
	 * Permet de rechercher des traces en fonction de plusieurs critères
	 * facultatifs.
	 * 
	 * @param demandeur
	 *            Utilisateur qui demande un affichage des traces.
	 * @param titre
	 *            Critère de recherche pouvant être null.
	 * @param email
	 *            Critère de recherche pouvant être null.
	 * @param typeAction
	 *            Critère de recherche pouvant être null.
	 * @param dateDebut
	 *            Critère de recherche pouvant être null.
	 * @param dateFin
	 *            Critère de recherche pouvant être null.
	 * @param page
	 *            Numéro de la page de traces demandée.
	 * @param nombreItems
	 *            Nombre d'items à renvoyer sur la page.
	 * @return Liste des traces correspondants aux critères et à la page
	 *         demandée.
	 * @throws DroitAccesException
	 *             Levée si l'utilisateur n'est pas administrateur.
	 */
	public List<Election> afficherPage(Utilisateur demandeur, String nom, String email,
			TypeAction typeAction, Date dateDebut, Date dateFin, int page,
			int nombreItems) throws DroitAccesException {
		/**
		 * On commence par indiquer qu'une tentative d'affichage des
		 * utilisateurs a lieu.
		 */
		Trace trace = init(demandeur, TypeAction.TR_CONSULT);

		if (!demandeur.getProfils().contains(ProfilsUtilisateur.ADMINISTRATEUR)) {
			throw new DroitAccesException(ErreurDroits.ACCES_ADMINISTRATEUR);
		}

		/**
		 * A faire
		 */

		// Exemple pour dire comment on renvoie tous les utilisateurs
		Query query = entityManager.createQuery("SELECT * FROM Trace");

		/**
		 * On indique dans la trace que la récupération de la liste s'est
		 * correctement réalisée.
		 */
		trace.setResultat("Liste des traces OK");
		trace.setDescription(
				"Affichage de la liste des traces par l'utilisateur : "
						+ demandeur.getLogin());

		return (List<Trace>) query.getResultList();
	}

}
