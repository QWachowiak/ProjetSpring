package fr.sgr.formation.voteapp;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.utilisateurs.modele.ProfilsUtilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Ville;
import fr.sgr.formation.voteapp.utilisateurs.services.DroitAccesException;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateurInvalideException;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateursServices;
import fr.sgr.formation.voteapp.utilisateurs.services.VilleService;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
public class Initialisation {

	@Autowired
	private VilleService villeService;
	@Autowired
	private UtilisateursServices utilServ;

	@PostConstruct
	@Transactional(propagation = Propagation.REQUIRED)
	public void init() {
		log.info("Initialisation des villes par défaut dans la base...");
		Ville rennes = new Ville();
		rennes.setCodePostal("35000");
		rennes.setNom("Rennes");

		villeService.creer(rennes);

		// Création du premier admin
		Utilisateur user2 = new Utilisateur();
		user2.setLogin("admin");
		user2.setNom("AdminNom");
		user2.setPrenom("AdminPrenom");
		user2.setMotDePasse("admin");
		List<ProfilsUtilisateur> profils = new ArrayList<ProfilsUtilisateur>();
		profils.add(ProfilsUtilisateur.ADMINISTRATEUR);
		user2.setProfils(profils);
		try {
			utilServ.creer(user2, user2);
		} catch (UtilisateurInvalideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DroitAccesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Création d'un utilisateur non admin
		Utilisateur user = new Utilisateur();
		user.setLogin("12");
		user.setNom("Michel");
		user.setPrenom("Philippe");
		user.setMotDePasse("jesuis12");
		try {
			utilServ.creer(user2, user);
		} catch (UtilisateurInvalideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DroitAccesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
