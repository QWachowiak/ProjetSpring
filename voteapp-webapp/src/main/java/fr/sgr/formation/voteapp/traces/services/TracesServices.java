package fr.sgr.formation.voteapp.traces.services;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.traces.modele.Trace;
import fr.sgr.formation.voteapp.traces.modele.TypeAction;
import fr.sgr.formation.voteapp.utilisateurs.modele.ProfilsUtilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.services.DroitAccesException;
import fr.sgr.formation.voteapp.utilisateurs.services.DroitAccesException.ErreurDroits;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class TracesServices {

	@Autowired
	private EntityManager entityManager;

	@Transactional(propagation = Propagation.REQUIRED)
	public Trace init(Utilisateur utilisateurOrigine, TypeAction typeAction) {
		Trace trace = new Trace();
		trace.setUtilisateurOrigine(utilisateurOrigine);
		/**
		 * Par défaut, la valeur d'une date est initialisée égale à la date
		 * correspondant à son initialisation.
		 */
		trace.setDate(new Date());
		trace.setTypeAction(typeAction);

		/** Définition des résultats et descriptions par défaut. */
		switch (typeAction) {
		case USR_CREATION:
			trace.setResultat("Création en erreur.");
			trace.setDescription(
					"Tentative de création d'un utilisateur, par l'utilisateur : " + utilisateurOrigine.getLogin());
			break;
		case USR_CONSULT:
			trace.setResultat("Consultation OK");
			trace.setDescription("Consultation du profil par l'utilisateur : " + utilisateurOrigine.getLogin());
			break;
		case USR_MODIF:
			trace.setResultat("Modification en erreur.");
			trace.setDescription(
					"Tentative de modification d'un profil par l'utilisateur : " + utilisateurOrigine.getLogin());
			break;
		case USR_RENOUVMDP:
			trace.setResultat("Renouvellement de mot de passe en erreur.");
			trace.setDescription("Demande de renouvellement de mot de passe par mail par l'utilisateur : "
					+ utilisateurOrigine.getLogin());
		case USR_LISTE:
			trace.setResultat("Affichage de la liste des utilisateurs en erreur.");
			trace.setDescription("Tentative d'affichage de la liste des utilisateurs par l'utilisateur : "
					+ utilisateurOrigine.getLogin());
			break;
		case TR_CONSULT:
			trace.setResultat("Affichage de la liste des traces en erreur.");
			trace.setDescription("Tentative d'affichage de la liste des traces par l'utilisateur : "
					+ utilisateurOrigine.getLogin());
			break;
		default:
			break;
		}

		entityManager.persist(trace);
		return trace;
	}

	/**
	 * Permet de rechercher des traces en fonction de plusieurs critères
	 * facultatifs.
	 * 
	 * @param demandeur
	 *            Utilisateur qui demande un affichage des traces.
	 * @param nom
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
	public List<Trace> afficherPage(Utilisateur demandeur, String nom, String email,
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
