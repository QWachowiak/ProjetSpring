package fr.sgr.formation.voteapp;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.utilisateurs.modele.Adresse;
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
		Utilisateur admin = new Utilisateur();
		admin.setLogin("admin");
		admin.setNom("AdminNom");
		admin.setPrenom("AdminPrenom");
		admin.setMotDePasse("admin");
		admin.setEmail("quentin.wachowiak@wanadoo.fr");
		Set<ProfilsUtilisateur> profils = new HashSet<ProfilsUtilisateur>();
		profils.add(ProfilsUtilisateur.ADMINISTRATEUR);
		admin.setProfils(profils);
		try {
			utilServ.creer(admin, admin);
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
		user.setEmail("user.email@server");
		Calendar calendar = Calendar.getInstance();
		calendar.set(1992, 12, 31);
		user.setDateDeNaissance(calendar);
		Adresse adresse = new Adresse();
		adresse.setRue("Rue machin truc");
		adresse.setVille(rennes);
		user.setAdresse(adresse);
		Set<ProfilsUtilisateur> profilsUser = new HashSet<ProfilsUtilisateur>();
		profilsUser.add(ProfilsUtilisateur.ADMINISTRATEUR);
		profilsUser.add(ProfilsUtilisateur.UTILISATEUR);
		user.setProfils(profilsUser);
		try {
			utilServ.creer(admin, user);
		} catch (UtilisateurInvalideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DroitAccesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
