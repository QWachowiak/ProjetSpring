package fr.sgr.formation.voteapp.utilisateurs.services;

import javax.persistence.EntityManager;

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
		if (ville != null) {
			/** Vérification de l'existence de l'utilisateur. */
			if (entityManager.find(Ville.class, ville.getId()) == null) {
				creer(ville);
			} else {
				// modifier();
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void creer(Ville ville) {
		entityManager.persist(ville);
	}

}
