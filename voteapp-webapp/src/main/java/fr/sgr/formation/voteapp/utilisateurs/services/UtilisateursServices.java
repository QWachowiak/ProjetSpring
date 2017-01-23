package fr.sgr.formation.voteapp.utilisateurs.services;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.notifications.services.NotificationsServices;
import fr.sgr.formation.voteapp.utilisateurs.modele.ProfilsUtilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
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
	private NotificationsServices notificationsServices;

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
		if (!createur.getProfils().contains(ProfilsUtilisateur.ADMINISTRATEUR)) {
			throw new DroitAccesException(ErreurDroits.ACCES_ADMINISTRATEUR);
		}

		if (nouvelUtilisateur == null) {
			throw new UtilisateurInvalideException(ErreurUtilisateur.UTILISATEUR_OBLIGATOIRE);
		}

		/** Validation de l'existance de l'utilisateur. */
		if (rechercherParLogin(nouvelUtilisateur.getLogin()) != null) {
			throw new UtilisateurInvalideException(ErreurUtilisateur.UTILISATEUR_EXISTANT);
		}

		/**
		 * Validation de l'utilisateur: lève une exception si l'utilisateur est
		 * invalide.
		 */
		validationServices.validerUtilisateur(nouvelUtilisateur);

		/** Notification de l'événement de création */
		notificationsServices.notifier("Création de l'utilisateur: " + nouvelUtilisateur.toString());

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

		/**
		 * On vérifie d'abord qu'un utilisateur non-admin ne cherche pas à
		 * atteindre une autre fiche que la sienne.
		 */
		if ((!modifiant.getLogin().equals(utilisateurAModifier.getLogin()))
				&& (!modifiant.getProfils().contains(ProfilsUtilisateur.ADMINISTRATEUR))) {
			throw new DroitAccesException(ErreurDroits.ACCES_ADMINISTRATEUR);
		}

		/** Mise à jour de tous les champs renseignés dans la requête. */
		if (!modifications.getPrenom().equals(utilisateurAModifier.getPrenom())) {
			utilisateurAModifier.setPrenom(modifications.getPrenom());
		}
		if (!modifications.getNom().equals(utilisateurAModifier.getNom())) {
			utilisateurAModifier.setNom(modifications.getNom());
		}
		if (!modifications.getEmail().equals(utilisateurAModifier.getEmail())) {
			utilisateurAModifier.setEmail(modifications.getEmail());
		}
		if (!modifications.getMotDePasse().equals(utilisateurAModifier.getMotDePasse())) {
			utilisateurAModifier.setMotDePasse(modifications.getMotDePasse());
		}
		if (!modifications.getDateDeNaissance().equals(utilisateurAModifier.getDateDeNaissance())) {
			utilisateurAModifier.setDateDeNaissance(modifications.getDateDeNaissance());
		}
		if (!modifications.getAdresse().equals(utilisateurAModifier.getAdresse())) {
			utilisateurAModifier.setAdresse(modifications.getAdresse());
		}

		/** Mise à jour des champs uniquement possible pour l'administrateur. */
		if (!modifications.getImage().equals(utilisateurAModifier.getAdresse())) {
			if (modifiant.getProfils().contains(ProfilsUtilisateur.ADMINISTRATEUR)) {
				utilisateurAModifier.setImage(modifications.getImage());
			} else {
				throw new DroitAccesException(ErreurDroits.ACCES_ADMINISTRATEUR);
			}
		}
		if (!modifications.getProfils().equals(utilisateurAModifier.getProfils())) {
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

		/** Notification de l'événement de création */
		notificationsServices.notifier("Modification de l'utilisateur: " + utilisateurAModifier.getLogin());

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

}
