package fr.sgr.formation.voteapp.utilisateurs.services;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.sgr.formation.voteapp.utilisateurs.modele.Ville;

@Service
public class VilleService {
	@Autowired
	private EntityManager entityManager;

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(Ville ville) {
		if (ville != null && entityManager.find(Ville.class, ville.getCodePostal()) == null) {
			creer(ville);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void creer(Ville ville) {
		entityManager.persist(ville);
	}

	/**
	 * Retourne la ville identifiée par le code postal.
	 * 
	 * @param codePostal
	 *            Code postal de la ville à trouver.
	 * @return Ville correspondant au code postal, ou null si aucune n'existe.
	 */
	public Ville rechercherParCodePostal(String codePostal) {
		if (StringUtils.isNotBlank(codePostal)) {
			return entityManager.find(Ville.class, codePostal);
		}
		return null;
	}

}
