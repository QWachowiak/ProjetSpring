package fr.sgr.formation.voteapp.utilisateurs.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.traces.modele.Trace;
import fr.sgr.formation.voteapp.traces.modele.TypeAction;
import fr.sgr.formation.voteapp.traces.services.TracesServices;
import fr.sgr.formation.voteapp.utilisateurs.modele.ProfilsUtilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Ville;
import fr.sgr.formation.voteapp.utilisateurs.services.DroitAccesException.ErreurDroits;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateurInvalideException.ErreurUtilisateur;
import lombok.extern.slf4j.Slf4j;

/**
 * Sur la création et modification d'un utilisateur : - Vérification des champs
 * obligatoires - Vérification de la validité (longueur) des champs - Appeler un
 * service de notification inscrivant dans la log création ou modification de
 * l'utilisateur Sur la récupération d'un utilisateur Vérification de
 * l'existance de l'utilisateur Retourner l'utilisateur Sur la suppression d'un
 * utilisateur Vérification de l'existance de l'utilisateur Retourner
 * l'utilisateur Appeler un service de notification inscrivant dans la log la
 * suppression de l'utilisateur
 */
@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class UtilisateursServices {
	/** Services de validation d'un utilisateur. */
	@Autowired
	private ValidationUtilisateurServices validationServices;
	/** Services de notification des événements. */
	@Autowired
	private TracesServices tracesServices;
	/** Services de gestion des villes. */
	@Autowired
	private VilleService villeServices;

	@Autowired
	private EntityManager entityManager;

	@Transactional(propagation = Propagation.REQUIRED)
	public Utilisateur update(Utilisateur createur, Utilisateur utilisateurAModifier)
			throws UtilisateurInvalideException, DroitAccesException {
		if (utilisateurAModifier == null) {
			throw new UtilisateurInvalideException(ErreurUtilisateur.UTILISATEUR_OBLIGATOIRE);
		}

		/** Vérification de l'existence de l'utilisateur. */
		if (rechercherParLogin(utilisateurAModifier.getLogin()) == null) {
			creer(createur, utilisateurAModifier);
		} else {
			modifier(createur, rechercherParLogin(utilisateurAModifier.getLogin()), utilisateurAModifier);
		}

		return utilisateurAModifier;
	}

	/**
	 * Crée un nouvel utilisateur sur le système.
	 * 
	 * @param createur
	 *            Utilisateur qui crée.
	 * @param nouvelUtilisateur
	 *            Utilisateur à créer dans le système.
	 * @return L'Utilisateur créé.
	 * @throws UtilisateurInvalideException
	 *             Levée si l'utilisateur à créer n'est pas valide.
	 * @throws DroitAccesException
	 *             Levée si l'utilisateur qui demande la création n'a pas les
	 *             droits pour le faire.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Utilisateur creer(Utilisateur createur, Utilisateur nouvelUtilisateur)
			throws UtilisateurInvalideException, DroitAccesException {
		/** On commence par indiquer qu'une tentative de création a lieu. */
		Trace trace = tracesServices.init(createur, TypeAction.USR_CREATION);

		if (!createur.getProfils().contains(ProfilsUtilisateur.ADMINISTRATEUR)) {
			throw new DroitAccesException(ErreurDroits.ACCES_ADMINISTRATEUR);
		}

		if (nouvelUtilisateur == null) {
			throw new UtilisateurInvalideException(ErreurUtilisateur.UTILISATEUR_OBLIGATOIRE);
		}

		/** Validation de l'existence de l'utilisateur. */
		if (rechercherParLogin(nouvelUtilisateur.getLogin()) != null) {
			throw new UtilisateurInvalideException(ErreurUtilisateur.UTILISATEUR_EXISTANT);
		}

		/**
		 * Vérification (et éventuellement création) de la présence de la ville
		 * dans le système
		 */
		if (nouvelUtilisateur.getAdresse() != null) {
			villeServices.update(nouvelUtilisateur.getAdresse().getVille());
		}

		/**
		 * Validation de l'utilisateur: lève une exception si l'utilisateur est
		 * invalide.
		 */
		validationServices.validerUtilisateur(nouvelUtilisateur);

		/**
		 * On indique dans la trace que la création s'est correctement réalisée.
		 */
		trace.setResultat("Création OK");
		trace.setDescription("Création de l'utilisateur de login " + nouvelUtilisateur.getLogin()
				+ " par l'administrateur de login " + createur.getLogin());

		/** Persistance de l'utilisateur. */
		entityManager.persist(nouvelUtilisateur);
		log.info("=====> Création de l'utilisateur : {}.", nouvelUtilisateur);

		return nouvelUtilisateur;
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
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Utilisateur modifier(Utilisateur modifiant, Utilisateur utilisateurAModifier, Utilisateur modifications)
			throws UtilisateurInvalideException, DroitAccesException {
		/** On commence par indiquer qu'une tentative de modification a lieu. */
		Trace trace = tracesServices.init(modifiant, TypeAction.USR_MODIF);

		/**
		 * On vérifie d'abord qu'un utilisateur non-admin ne cherche pas à
		 * atteindre une autre fiche que la sienne.
		 */
		if ((!modifiant.getLogin().equals(utilisateurAModifier.getLogin()))
				&& (!modifiant.getProfils().contains(ProfilsUtilisateur.ADMINISTRATEUR))) {
			throw new DroitAccesException(ErreurDroits.ACCES_ADMINISTRATEUR);
		}

		/** Mise à jour de tous les champs renseignés dans la requête. */
		if (!utilisateurAModifier.getPrenom().equals(modifications.getPrenom())) {
			utilisateurAModifier.setPrenom(modifications.getPrenom());
		}
		if (!utilisateurAModifier.getNom().equals(modifications.getNom())) {
			utilisateurAModifier.setNom(modifications.getNom());
		}
		if (!utilisateurAModifier.getEmail().equals(modifications.getEmail())) {
			utilisateurAModifier.setEmail(modifications.getEmail());
		}
		if (!utilisateurAModifier.getMotDePasse().equals(modifications.getMotDePasse())) {
			utilisateurAModifier.setMotDePasse(modifications.getMotDePasse());
		}
		if (utilisateurAModifier.getDateDeNaissance() == null ? modifications.getDateDeNaissance() != null
				: !utilisateurAModifier.getDateDeNaissance().equals(modifications.getDateDeNaissance())) {
			utilisateurAModifier.setDateDeNaissance(modifications.getDateDeNaissance());
		}

		if (utilisateurAModifier.getAdresse() == null) {
			if (modifications.getAdresse() != null) {
				villeServices.update(modifications.getAdresse().getVille());
				utilisateurAModifier.setAdresse(modifications.getAdresse());
			}
		} else {
			if (!utilisateurAModifier.getAdresse().equals(modifications.getAdresse())) {
				if (modifications.getAdresse() != null) {
					villeServices.update(modifications.getAdresse().getVille());
				}
				utilisateurAModifier.setAdresse(modifications.getAdresse());
			}
		}

		/** Mise à jour des champs uniquement possible pour l'administrateur. */
		if (utilisateurAModifier.getImage() == null ? modifications.getImage() != null
				: !utilisateurAModifier.getImage().equals(modifications.getImage())) {
			if (modifiant.getProfils().contains(ProfilsUtilisateur.ADMINISTRATEUR)) {
				utilisateurAModifier.setImage(modifications.getImage());
			} else {
				throw new DroitAccesException(ErreurDroits.ACCES_ADMINISTRATEUR);
			}
		}
		if (utilisateurAModifier.getProfils() == null ? modifications.getProfils() != null
				: !utilisateurAModifier.getProfils().equals(modifications.getProfils())) {
			if (modifiant.getProfils().contains(ProfilsUtilisateur.ADMINISTRATEUR)) {
				utilisateurAModifier.setProfils(modifications.getProfils());
			} else {
				throw new DroitAccesException(ErreurDroits.ACCES_ADMINISTRATEUR);
			}
		}

		/**
		 * Validation de l'utilisateur: lève une exception si l'utilisateur est
		 * invalide.
		 */
		validationServices.validerUtilisateur(utilisateurAModifier);

		/**
		 * On indique dans la trace que la modification s'est correctement
		 * réalisée.
		 */
		trace.setResultat("Modification OK");
		trace.setDescription("Modification du profil de l'utilisateur de login " + utilisateurAModifier.getLogin()
				+ " par l'administrateur de login " + modifiant.getLogin());

		return utilisateurAModifier;
	}

	/**
	 * Retourne l'utilisateur identifié par le login.
	 * 
	 * @param login
	 *            Login identifiant l'utilisateur.
	 * @return Retourne l'utilisateur identifié par le login.
	 */
	public Utilisateur rechercherParLogin(String login) {
		log.info("=====> Recherche de l'utilisateur de login {}.", login);

		if (StringUtils.isNotBlank(login)) {
			return entityManager.find(Utilisateur.class, login);
		}

		return null;
	}

	/**
	 * Permet de rechercher des utilisateurs en fonction de plusieurs critères
	 * facultatifs.
	 * 
	 * @param prenom
	 *            Critère de recherche pouvant être null.
	 * @param nom
	 *            Critère de recherche pouvant être null.
	 * @param ville
	 *            Critère de recherche pouvant être null.
	 * @param profil
	 *            Critère de recherche pouvant être null.
	 * @param page
	 *            Numéro de la page demandée.
	 * @param nombreItems
	 *            Nombre d'items demandés.
	 * @return Une page de la liste des utilisateurs correspondant aux critères
	 * @throws DroitAccesException
	 */
	public List<Utilisateur> afficherPage(Utilisateur demandeur, String prenom, String nom, Ville ville,
			ProfilsUtilisateur profil, int page,
			int nombreItems) throws DroitAccesException {
		/**
		 * On commence par indiquer qu'une tentative d'affichage des
		 * utilisateurs a lieu.
		 */
		Trace trace = tracesServices.init(demandeur, TypeAction.USR_LISTE);

		if (!demandeur.getProfils().contains(ProfilsUtilisateur.ADMINISTRATEUR)) {
			throw new DroitAccesException(ErreurDroits.ACCES_ADMINISTRATEUR);
		}

		/**
		 * A faire L'utilisateur doit pouvoir filtrer la liste des utilisateurs
		 * retournés sur différents critères: Nom (recherche du type
		 * "contient"), Prénom (recherche du type "contient"), Ville, Profil.
		 */

		Query query = entityManager.createQuery(
				"SELECT * FROM utilisateur, ville, profils_utilisateurs "
						+ "WHERE utilisateur.ville_codepostal = ville.code_postal "
						+ "AND utilisateur.login = profils_utilisateur.login_utilisateur"
						+ "AND LOWER(utilisateur.nom) REGEXP :custNom "
						+ "AND LOWER(utilisateur.nom) REGEXP :custPrenom "
						+ "AND LOWER(ville.nom) REGEXP :custVille "
						+ "AND LOWER(profils_utilisateurs.profils) REGEXP :custProfil ");
		if (nom != null) {
			query = query.setParameter("custNom", "\'" + nom + "\'");
		} else {
			query = query.setParameter("custNom", "\'[a-z]\'");
		}
		if (prenom != null) {
			query = query.setParameter("custPrenom", "\'" + prenom + "\'");
		} else {
			query = query.setParameter("custPrenom", "\'[a-z]\'");
		}
		if (ville != null) {
			query = query.setParameter("custVille", ville);
		} else {
			query = query.setParameter("custVille", "\'[a-z]\'");
		}
		if (profil != null) {
			query = query.setParameter("custProfil", profil);
		} else {
			query = query.setParameter("custProfil", "\'[a-z]\'");
		}
		// query = query.setMaxResults(nombreItems);

		/*
		 * query = query.setParameter("custName", prenom); query =
		 * query.setParameter("custName", ville); query =
		 * query.setParameter("custName", profil); // query =
		 * query.getResultList(); //
		 * http://localhost:8080/utilisateurs/admin/list?json=nom=Michel&prenom=
		 * Philippe=&ville=null&profil=UTILISATEUR&page=1&nombreItems=1 //
		 * Exemple pour dire comment on renvoie tous les utilisateurs // Query
		 * query = entityManager.createQuery("SELECT * FROM // Utilisateurs");
		 * 
		 * /** On indique dans la trace que la récupération de la liste s'est
		 * correctement réalisée.
		 */
		trace.setResultat("Liste des utilisateurs OK");
		trace.setDescription(
				"Affichage de la liste des utilisateurs par l'utilisateur : "
						+ demandeur.getLogin());

		List<Utilisateur> res = (List<Utilisateur>) query.getResultList();
		System.out.println(res);
		return res;
	}

}
