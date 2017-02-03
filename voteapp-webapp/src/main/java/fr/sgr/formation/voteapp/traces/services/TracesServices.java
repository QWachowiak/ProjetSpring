package fr.sgr.formation.voteapp.traces.services;

import java.util.Date;
import java.util.HashMap;
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
		case ELEC_CREATION:
			trace.setResultat("Création en erreur.");
			trace.setDescription(
					"Tentative de création d'une election, par l'utilisateur : " + utilisateurOrigine.getLogin());
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
	public HashMap<Trace, String> afficherPage(Utilisateur demandeur, String nom, String email,
			TypeAction typeAction, Date dateDebut, Date dateFin, int page,
			int nombreItems) throws DroitAccesException {
		/**
		 * On commence par indiquer qu'une tentative d'affichage des
		 * utilisateurs a lieu.
		 */
		// Trace trace = init(demandeur, TypeAction.TR_CONSULT);

		if (!demandeur.getProfils().contains(ProfilsUtilisateur.ADMINISTRATEUR)) {
			throw new DroitAccesException(ErreurDroits.ACCES_ADMINISTRATEUR);
		}

		/**
		 * On doit pouvoir filtrer la liste des traces retournés sur différents
		 * critères: Email utilisateur, Nom Utilisateur (recherche du type
		 * "contient"), Type d'action, Période de la trace: Date de début et
		 * Date de fin
		 */
		log.info("=====> Consultation d'une liste de traces");
		Query query = entityManager.createQuery("SELECT t FROM Trace t ");
		/*
		 * Etant donné que jpql utilise les noms des classes java mappées
		 * comme @Entity les jointures son malaisées, on choisit de trier les
		 * résultats de la requête globale avec java
		 */
		List<Trace> res = (List<Trace>) query.getResultList();
		for (Trace t : res) {
			if (nom != null) {
				if (!t.getUtilisateurOrigine().getNom().contains(nom)) {
					res.remove(t);
				}
			}
			if (email != null) {
				if (!t.getUtilisateurOrigine().getEmail().equals(email)) {
					res.remove(t);
				}
			}
			if (typeAction != null) {
				if (!t.getTypeAction().equals(typeAction)) {
					res.remove(t);
				}
			}
			if (dateDebut != null) {
				if (!t.getDate().after(dateDebut)) {
					res.remove(t);
				}
			}
			if (dateFin != null) {
				if (!t.getDate().before(dateFin)) {
					res.remove(t);
				}
			}
		}

		/*
		 * On indique dans la trace que la récupération de la liste s'est
		 * correctement réalisée.
		 */
		// trace.setResultat("Liste des traces OK");
		// trace.setDescription(
		// "Affichage de la liste des traces par l'utilisateur : " +
		// demandeur.getLogin());

		/*
		 * Afficher seulement la page désirée avec le nombre d'items désirés
		 */
		HashMap<Trace, String> pageRecherchee = new HashMap();
		int l = res.size();
		if (nombreItems != 0) {
			int quotient = l / nombreItems;
			int nbPages = quotient;
			int reste = l % nombreItems;
			if (reste != 0) {
				nbPages = nbPages + 1;
			}
			/*
			 * Je ne veux garder que les utilisateurs dont l'index correspond à
			 * la page demandée:
			 */
			if (page <= nbPages) {
				int indexDeb = 0;
				if (page != 1) {
					indexDeb = (page - 1) * nombreItems;
				}
				int indexFin = indexDeb + nombreItems;
				if (indexFin > l) {
					indexFin = l;
				}
				for (int i = indexDeb; i < indexFin; i++) {
					String pp = Integer.valueOf(pageRecherchee.size() + 1).toString() + "/"
							+ Integer.valueOf(l).toString()
							+ " items et " + Integer.valueOf(page).toString() + "/"
							+ Integer.valueOf(nbPages).toString()
							+ " pages";
					pageRecherchee.put(res.get(i), pp);
				}

			} else {
				System.out.println("La page demandée n'existe pas.");
			}

		} else {
			System.out.println("Division par zéro!");
		}

		return pageRecherchee;
	}

}
