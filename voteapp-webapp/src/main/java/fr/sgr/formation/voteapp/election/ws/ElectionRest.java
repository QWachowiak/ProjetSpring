package fr.sgr.formation.voteapp.election.ws;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sgr.formation.voteapp.election.modele.Election;
import fr.sgr.formation.voteapp.election.services.ElectionInvalideException;
import fr.sgr.formation.voteapp.election.services.ElectionServices;
import fr.sgr.formation.voteapp.traces.services.TracesServices;
import fr.sgr.formation.voteapp.utilisateurs.services.DroitAccesException;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateurInvalideException;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateursServices;
import fr.sgr.formation.voteapp.utilisateurs.ws.DescriptionErreur;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("election/{login}")
@Slf4j
public class ElectionRest {

	@Autowired
	private ElectionServices electionServices;

	@Autowired
	private UtilisateursServices utilisateursServices;

	@Autowired
	private TracesServices tracesServices;

	@RequestMapping(method = RequestMethod.PUT)
	public void creer(@PathVariable String login, @RequestBody Election election)
			throws ElectionInvalideException, DroitAccesException {
		log.info("=====> Création de l'election d'id {} (gerant : {}).", election.getId(),
				login);
		electionServices.init(utilisateursServices.rechercherParLogin(login), election);
	}

	@RequestMapping(value = "/liste", method = RequestMethod.GET)
	public HashMap<Election, String> afficher(@PathVariable String login,
			@RequestParam(value = "page") int page,
			@RequestParam(value = "nbItems") int nombreItems,
			@RequestParam(value = "titre", required = false) String titre,
			@RequestParam(value = "description", required = false) String description)
					throws DroitAccesException {
		log.info("=====> Récupération d'une liste d'elections");

		return electionServices.afficherPage(utilisateursServices.rechercherParLogin(login), titre, description, page,
				nombreItems);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public void cloture(@PathVariable String login, @RequestParam(value = "id") long id)
			throws DroitAccesException, ElectionInvalideException {
		log.info("=====> Suppression de l'election d'id {}.", id);
		electionServices.cloture(utilisateursServices.rechercherParLogin(login), electionServices.rechercherParId(id));
	}

	@ExceptionHandler({ Exception.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreur(Exception exception) {
		return new DescriptionErreur("erreur", exception.getMessage());
	}

	@ExceptionHandler({ UtilisateurInvalideException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreurUtilisateurInvalide(UtilisateurInvalideException exception) {
		return new DescriptionErreur(exception.getErreur().name(),
				exception.getErreur().getMessage());
	}

	@ExceptionHandler({ ElectionInvalideException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreurElectionInvalide(ElectionInvalideException exception) {
		return new DescriptionErreur(exception.getErreur().name(),
				exception.getErreur().getMessage());
	}

	@ExceptionHandler({ DroitAccesException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreurDroitAcces(DroitAccesException exception) {
		return new DescriptionErreur(exception.getErreur().name(),
				exception.getErreur().getMessage());
	}

}
