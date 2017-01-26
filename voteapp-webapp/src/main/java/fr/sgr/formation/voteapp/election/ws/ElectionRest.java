package fr.sgr.formation.voteapp.election.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.sgr.formation.voteapp.election.modele.Election;
import fr.sgr.formation.voteapp.election.services.ElectionInvalideException;
import fr.sgr.formation.voteapp.election.services.ElectionServices;
import fr.sgr.formation.voteapp.traces.services.TracesServices;
import fr.sgr.formation.voteapp.utilisateurs.services.DroitAccesException;
import fr.sgr.formation.voteapp.utilisateurs.services.UtilisateursServices;
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

	// @RequestMapping(method = RequestMethod.DELETE)
	// public void supprimer(@PathVariable String login) {
	// log.info("=====> Suppression de l'utilisateur de login {}.", login);
	//
	// }
	//
	// @RequestMapping(method = RequestMethod.GET)
	// public Utilisateur lire(@PathVariable String login) {
	// log.info("=====> Récupération de l'utilisateur de login {}.", login);
	// tracesServices.init(utilisateursServices.rechercherParLogin(login),
	// TypeAction.USR_CONSULT);
	// return utilisateursServices.rechercherParLogin(login);
	// }
	//
	// @RequestMapping(value = "/motDePasse", method = RequestMethod.GET)
	// public void renouvellerMotdePasse(@PathVariable String login) throws
	// Exception {
	// log.info("=====> Envoi d'un nouveau mot de passe par mail à l'utilisateur
	// de login : {}.", login);
	// Trace trace =
	// tracesServices.init(utilisateursServices.rechercherParLogin(login),
	// TypeAction.USR_RENOUVMDP);
	// String nouveauMdp = emailServices.genererMotDePasse();
	// utilisateursServices.rechercherParLogin(login).setMotDePasse(nouveauMdp);
	// emailServices.renouvellerMotDePasse(utilisateursServices.rechercherParLogin(login),
	// nouveauMdp);
	//
	// /** Si l'email est passé, on met à jour la trace. */
	// trace.setResultat("Renouvellement de mot de passe OK");
	// }
	//
	// @RequestMapping(value = "/liste", method = RequestMethod.GET)
	// public List<Utilisateur> afficher(@PathVariable String login,
	// @RequestParam(value = "page") int page,
	// @RequestParam(value = "nbItems") int nombreItems,
	// @RequestParam(value = "prenom", required = false) String prenom,
	// @RequestParam(value = "nom", required = false) String nom,
	// @RequestParam(value = "codePostal", required = false) String codePostal,
	// @RequestParam(value = "profil", required = false) ProfilsUtilisateur
	// profil) throws DroitAccesException {
	// log.info("=====> Récupération d'une liste d'utilisateurs");
	//
	// return
	// utilisateursServices.afficherPage(utilisateursServices.rechercherParLogin(login),
	// prenom, nom,
	// villeServices.rechercherParCodePostal(codePostal), profil,
	// page, nombreItems);
	// }
	//
	// @ExceptionHandler({ Exception.class })
	// @ResponseStatus(value = HttpStatus.BAD_REQUEST)
	// public DescriptionErreur gestionErreur(Exception exception) {
	// return new DescriptionErreur("erreur", exception.getMessage());
	// }
	//
	// @ExceptionHandler({ UtilisateurInvalideException.class })
	// @ResponseStatus(value = HttpStatus.BAD_REQUEST)
	// public DescriptionErreur
	// gestionErreurUtilisateurInvalide(UtilisateurInvalideException exception)
	// {
	// return new DescriptionErreur(exception.getErreur().name(),
	// exception.getErreur().getMessage());
	// }
	//
	// @ExceptionHandler({ DroitAccesException.class })
	// @ResponseStatus(value = HttpStatus.BAD_REQUEST)
	// public DescriptionErreur gestionErreurDroitAcces(DroitAccesException
	// exception) {
	// return new DescriptionErreur(exception.getErreur().name(),
	// exception.getErreur().getMessage());
	// }

}
