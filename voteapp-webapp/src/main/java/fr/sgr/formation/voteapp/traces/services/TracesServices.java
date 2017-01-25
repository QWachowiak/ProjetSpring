package fr.sgr.formation.voteapp.traces.services;

import java.util.Date;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.traces.modele.Trace;
import fr.sgr.formation.voteapp.traces.modele.TypeAction;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS)
public class TracesServices {

	@Autowired
	private EntityManager entityManager;

	@Transactional(propagation = Propagation.REQUIRED)
	public Trace init(Utilisateur proprietaire, TypeAction typeAction) {
		Trace trace = new Trace();
		trace.setProprietaire(proprietaire);
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
					"Tentative de création d'un utilisateur, par l'utilisateur : " + proprietaire.getLogin());
			break;
		case USR_CONSULT:
			trace.setResultat("Consultation OK");
			trace.setDescription("Consultation du profil par l'utilisateur : " + proprietaire.getLogin());
			break;
		case USR_MODIF:
			trace.setResultat("Modification en erreur.");
			trace.setDescription(
					"Tentative de modification d'un profil par l'utilisateur : " + proprietaire.getLogin());
			break;
		case USR_RENOUVMDP:
			trace.setResultat("Renouvellement de mot de passe en erreur.");
			trace.setDescription("Demande de renouvellement de mot de passe par mail par l'utilisateur : "
					+ proprietaire.getLogin());
		default:
			break;
		}

		entityManager.persist(trace);
		return trace;
	}

}
