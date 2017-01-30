package fr.sgr.formation.voteapp.traces.ws;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sgr.formation.voteapp.traces.modele.Trace;
import fr.sgr.formation.voteapp.traces.modele.TypeAction;
import fr.sgr.formation.voteapp.traces.services.TracesServices;
import fr.sgr.formation.voteapp.utilisateurs.services.DroitAccesException;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateursServices;
import fr.sgr.formation.voteapp.utilisateurs.ws.DescriptionErreur;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("traces/{login}")
@Slf4j
public class TracesRest {
	@Autowired
	private UtilisateursServices utilisateursServices;

	@Autowired
	private TracesServices tracesServices;

	@RequestMapping(method = RequestMethod.GET)
	public HashMap<Trace, String> afficher(@PathVariable String login,
			@RequestParam(value = "page") int page,
			@RequestParam(value = "nbItems") int nombreItems,
			@RequestParam(value = "nom", required = false) String nom,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "typeAction", required = false) TypeAction typeAction,
			@RequestParam(value = "dateDebut", required = false) Date dateDebut,
			@RequestParam(value = "dateFin", required = false) Date dateFin) throws DroitAccesException {
		log.info("=====> Récupération d'une liste de traces");

		return tracesServices.afficherPage(utilisateursServices.rechercherParLogin(login), nom, email,
				typeAction, dateDebut, dateFin, page, nombreItems);
	}

	@ExceptionHandler({ DroitAccesException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreurDroitAcces(DroitAccesException exception) {
		return new DescriptionErreur(exception.getErreur().name(), exception.getErreur().getMessage());
	}

}
