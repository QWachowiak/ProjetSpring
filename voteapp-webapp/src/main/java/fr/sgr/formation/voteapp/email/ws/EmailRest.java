package fr.sgr.formation.voteapp.email.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sgr.formation.voteapp.email.services.EmailServices;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateursServices;
import fr.sgr.formation.voteapp.utilisateurs.ws.DescriptionErreur;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("email/{login}")
@Slf4j
public class EmailRest {
	@Autowired
	private EmailServices emailServices;

	@Autowired
	private UtilisateursServices utilisateursServices;

	@RequestMapping(method = RequestMethod.GET)
	public void lire(@PathVariable String login) throws Exception {
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
}
