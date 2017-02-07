package fr.sgr.formation.voteapp.election.services;

import java.util.HashMap;
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
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateurInvalideException;
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
	 * Permet de rechercher des elections en fonction de plusieurs critères
	 * facultatifs.
	 * 
	 * @param titre
	 *            Critère de recherche pouvant être null.
	 * @param description
	 *            Critère de recherche pouvant être null.
	 * @param page
	 *            Numéro de la page demandée.
	 * @param nombreItems
	 *            Nombre d'items demandés.
	 * @return Une page de la liste des elections correspondant aux critères
	 * @throws DroitAccesException
	 */
	public HashMap<Utilisateur, String> afficherPage(Utilisateur demandeur, String titre, String description, int page,
			int nombreItems) throws DroitAccesException {
		/**
		 * On commence par indiquer qu'une tentative d'affichage des elections a
		 * lieu.
		 */
		Trace trace = tracesServices.init(demandeur, TypeAction.ELEC_LISTE);

		if (!demandeur.getProfils().contains(ProfilsUtilisateur.GERANT)) {
			throw new DroitAccesException(ErreurDroits.ACCES_GERANT);
		}

		/**
		 * L'utilisateur doit pouvoir filtrer la liste des elections retournés
		 * sur différents critères: Titre (recherche du type "contient"),
		 * Description (recherche du type "contient").
		 */
		log.info("=====> Consultation d'une liste d'elections");
		Query query = entityManager.createQuery(
				"SELECT e FROM Election e ");
		/*
		 * Etant donné que jpql utilise les noms des classes java mappées
		 * comme @Entity les jointures son malaisées, on choisit de trier les
		 * résultats de la requête globale avec java
		 */
		List<Election> res = (List<Election>) query.getResultList();
		for (Election e : res) {
			if (titre != null) {
				if (!e.getTitre().contains(titre)) {
					res.remove(e);
				}
			}
			if (description != null) {
				if (!e.getDescription().contains(description)) {
					res.remove(e);
				}
			}
		}

		/*
		 * On indique dans la trace que la récupération de la liste s'est
		 * correctement réalisée.
		 */
		trace.setResultat("Liste des elections OK");
		trace.setDescription(
				"Affichage de la liste des elections par l'utilisateur : "
						+ demandeur.getLogin());

		/*
		 * Afficher seulement la page désirée avec le nombre d'items désirés
		 */
		HashMap<Utilisateur, String> pageRecherchee = new HashMap();
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
					// pageRecherchee.put(res.get(i), pp);
				}

			} else {
				System.out.println("La page demandée n'existe pas.");
			}

		} else {
			System.out.println("Division par zéro!");
		}

		return pageRecherchee;
	}

	/**
	 * Retourne l'election identifié par l'id.
	 * 
	 * @param id
	 *            id identifiant l'election.
	 * @return Retourne l'election identifié par l'id.
	 */
	public Election rechercherParId(long id) {
		log.info("=====> Recherche de l'election d'id {}.", id);

		if (id != 0) {
			return entityManager.find(Election.class, id);
		}

		return null;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Election cloture(Utilisateur createur, Election election)
			throws ElectionInvalideException, DroitAccesException {

		/** On commence par indiquer qu'une tentative de création a lieu. */
		Trace trace = tracesServices.init(createur, TypeAction.ELEC_CLOTURE);

		if (!createur.getProfils().contains(ProfilsUtilisateur.GERANT)) {
			throw new DroitAccesException(ErreurDroits.ACCES_GERANT);
		}

		if (createur == null) {
			throw new ElectionInvalideException(ErreurElection.UTILISATEUR_OBLIGATOIRE);
		}

		if (!createur.equals(election.getUtilisateurOrigine())) {
			throw new ElectionInvalideException(ErreurElection.GERANT_ORIGINE);
		}

		election.setCloture(true);

		/**
		 * On indique dans la trace que la cloture s'est correctement réalisée.
		 */
		trace.setResultat("Cloture OK");
		trace.setDescription("Cloture de l'election d'id " + election.getId()
				+ " par le gérant de login " + createur.getLogin());

		return election;
	}

	/**
	 * Modifie un utilisateur déjà présent dans le système.
	 * 
	 * @param modifiant
	 *            Utilisateur qui demande la modification.
	 * @param utilisateurAModifier
	 *            Utilisateur dont le profil est modifié.
	 * @param modifications
	 *            Utilisateur qui sert de cible pour l'utilisateur à modifier.
	 * @return L'Utilisateur modifié.
	 * @throws UtilisateurInvalideException
	 *             Levée si l'utilisateur obtenu n'est pas valide.
	 * @throws DroitAccesException
	 *             Levée si l'utilisateur qui demande la modification n'a pas
	 *             les droits pour cette action.
	 * @throws ElectionInvalideException
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Election modifier(Utilisateur modifiant, Election electionAModifier, Election modifications)
			throws UtilisateurInvalideException, DroitAccesException, ElectionInvalideException {
		/** On commence par indiquer qu'une tentative de modification a lieu. */
		Trace trace = tracesServices.init(modifiant, TypeAction.ELEC_MODIF);

		/**
		 * On vérifie d'abord qu'un utilisateur non-gerant ne cherche pas à
		 * modifier une election.
		 */
		if (!modifiant.getProfils().contains(ProfilsUtilisateur.GERANT)) {
			throw new DroitAccesException(ErreurDroits.ACCES_GERANT);
		}

		if (modifiant == null) {
			throw new ElectionInvalideException(ErreurElection.UTILISATEUR_OBLIGATOIRE);
		}

		if (!modifiant.equals(electionAModifier.getUtilisateurOrigine())) {
			throw new ElectionInvalideException(ErreurElection.GERANT_ORIGINE);
		}

		/** Mise à jour de tous les champs renseignés dans la requête. */
		if (!electionAModifier.getTitre().equals(modifications.getTitre())) {
			electionAModifier.setTitre(modifications.getTitre());
		}
		if (!electionAModifier.getDescription().equals(modifications.getDescription())) {
			electionAModifier.setDescription(modifications.getDescription());
		}
		if (!electionAModifier.getDateDeDebut().equals(modifications.getDateDeDebut())) {
			electionAModifier.setDateDeDebut(modifications.getDateDeDebut());
		}
		if (!electionAModifier.getDateDeFin().equals(modifications.getDateDeFin())) {
			electionAModifier.setDateDeFin(modifications.getDateDeFin());
		}

		/** Mise à jour des champs uniquement possible pour l'administrateur. */
		if (electionAModifier.getImage() == null ? modifications.getImage() != null
				: !electionAModifier.getImage().equals(modifications.getImage())) {
			if (modifiant.getProfils().contains(ProfilsUtilisateur.GERANT)) {
				electionAModifier.setImage(modifications.getImage());
			} else {
				throw new DroitAccesException(ErreurDroits.ACCES_GERANT);
			}
		}

		/**
		 * Validation de l'utilisateur: lève une exception si l'utilisateur est
		 * invalide.
		 */
		validationServices.validerElection(electionAModifier);

		/**
		 * On indique dans la trace que la modification s'est correctement
		 * réalisée.
		 */
		trace.setResultat("Modification OK");
		trace.setDescription("Modification de l'election d'id " + electionAModifier.getId()
				+ " par le gérant de login " + modifiant.getLogin());

		return electionAModifier;
	}
}
