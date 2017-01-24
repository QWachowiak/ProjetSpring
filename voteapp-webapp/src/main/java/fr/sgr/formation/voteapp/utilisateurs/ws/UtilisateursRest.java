package fr.sgr.formation.voteapp.utilisateurs.ws;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.utilisateurs.services.DroitAccesException;
import fr.sgr.formation.voteapp.utilisateurs.services.EmailServices;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateurInvalideException;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateursServices;
import fr.sgr.formation.voteapp.utilisateurs.services.VilleService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("utilisateurs/{login}")
@Slf4j
public class UtilisateursRest {
	@Autowired
	private UtilisateursServices utilisateursServices;

	@Autowired
	private EmailServices emailServices;

	@Autowired
	private VilleService villeServices;

	@RequestMapping(method = RequestMethod.PUT)
	public void update(@PathVariable String login, @RequestBody Utilisateur utilisateur)
			throws UtilisateurInvalideException, DroitAccesException {
		log.info("=====> Création ou modification de l'utilisateur de login {} (admin : {}).", utilisateur.getLogin(),
				login);
		// utilisateursServices.update(utilisateursServices.rechercherParLogin(login),
		// utilisateur);
		villeServices.update(utilisateur.getAdresse().getVille());
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public void supprimer(@PathVariable String login) {
		log.info("=====> Suppression de l'utilisateur de login {}.", login);

	}

	@RequestMapping(method = RequestMethod.GET)
	public List<Utilisateur> lire(@PathVariable String login) {
		log.info("=====> Récupération de l'utilisateur de login {}.", login);
		List<Utilisateur> listeCorrespondante = new ArrayList<Utilisateur>();
		listeCorrespondante.add(utilisateursServices.rechercherParLogin(login));
		return listeCorrespondante;
	}

	@RequestMapping(value = "/motDePasse", method = RequestMethod.GET)
	public void renouvellerMotdePasse(@PathVariable String login) throws Exception {
		log.info("=====> Envoi d'un nouveau mot de passe par mail à l'utilisateur de login : {}.", login);
		String nouveauMdp = emailServices.genererMotDePasse();
		utilisateursServices.rechercherParLogin(login).setMotDePasse(nouveauMdp);
		emailServices.renouvellerMotDePasse(utilisateursServices.rechercherParLogin(login), nouveauMdp);
	}

	@ExceptionHandler({ Exception.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreur(Exception exception) {
		return new DescriptionErreur("erreur", exception.getMessage());
	}

	@ExceptionHandler({ UtilisateurInvalideException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreurUtilisateurInvalide(UtilisateurInvalideException exception) {
		return new DescriptionErreur(exception.getErreur().name(), exception.getErreur().getMessage());
	}

	@ExceptionHandler({ DroitAccesException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreurDroitAcces(DroitAccesException exception) {
		return new DescriptionErreur(exception.getErreur().name(), exception.getErreur().getMessage());
	}

}
