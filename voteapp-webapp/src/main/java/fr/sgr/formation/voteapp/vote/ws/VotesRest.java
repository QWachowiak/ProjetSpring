package fr.sgr.formation.voteapp.vote.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.sgr.formation.voteapp.election.services.ElectionServices;
import fr.sgr.formation.voteapp.utilisateurs.services.DroitAccesException;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateursServices;
import fr.sgr.formation.voteapp.utilisateurs.ws.DescriptionErreur;
import fr.sgr.formation.voteapp.vote.modele.ValeurVote;
import fr.sgr.formation.voteapp.vote.services.VoteInvalideException;
import fr.sgr.formation.voteapp.vote.services.VotesServices;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("vote/{login}")
@Slf4j
public class VotesRest {
	@Autowired
	private UtilisateursServices utilisateursServices;

	@Autowired
	private ElectionServices electionServices;

	@Autowired
	private VotesServices votesServices;

	@RequestMapping(method = RequestMethod.PUT)
	public void vote(@PathVariable String login, @RequestParam(value = "election") long idElection,
			@RequestParam(value = "vote") ValeurVote valeur)
					throws VoteInvalideException {
		log.info("=====> Vote de l'utilisateur de login {} à l'élection de login {}).", login, idElection);
		votesServices.creerVote(utilisateursServices.rechercherParLogin(login), valeur,
				electionServices.rechercherParId(idElection));
	}

	@ExceptionHandler({ VoteInvalideException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public DescriptionErreur gestionErreurDroitAcces(DroitAccesException exception) {
		return new DescriptionErreur(exception.getErreur().name(), exception.getErreur().getMessage());
	}

}