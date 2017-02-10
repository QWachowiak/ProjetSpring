package fr.sgr.formation.voteapp.utilisateurs.services;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.sgr.formation.voteapp.notifications.services.NotificationsServices;
import fr.sgr.formation.voteapp.traces.services.TracesServices;
import fr.sgr.formation.voteapp.utilisateurs.modele.ProfilsUtilisateur;
import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;

@RunWith(MockitoJUnitRunner.class)
public class UtilisateursServicesTest {
	/** Classe des services à tester. */
	@InjectMocks
	private UtilisateursServices services = new UtilisateursServices();

	@Mock
	private ValidationUtilisateurServices validationServices;
	@Mock
	private NotificationsServices notificationsServices;
	@Mock
	private TracesServices tracesServices;
	@Mock
	private VilleService villeServices;
	@Mock
	private EntityManager entityManager;

	@Test
	public void updateUtilisateurAdminNul() {
		try {
			/** Etant donnée une session d'administrateur ouverte. */
			Utilisateur admin = new Utilisateur();

			/** When: Lorsqu'on appelle le service de mise à jour. */
			services.update(admin, null);

			fail("Une exception devrait être levée.");
		} catch (UtilisateurInvalideException e) {
			/** Then : Alors une exception est levée. */
			Assert.assertEquals(UtilisateurInvalideException.ErreurUtilisateur.UTILISATEUR_OBLIGATOIRE, e.getErreur());
		} catch (DroitAccesException e) {
			fail("Une exception d'utilisateur invalide devrait être levée.");
		}
	}

	@Test
	public void updateUtilisateurNonAdmin() {
		try {
			/**
			 * Etant données une session d'utilisateur ouverte et un utilisateur
			 * différent à créer/modifier.
			 */
			Utilisateur createur = new Utilisateur();
			Utilisateur utilisateurAModifier = new Utilisateur();

			/**
			 * Lorsque la session en cours n'est pas celle d'un administrateur.
			 */
			Set<ProfilsUtilisateur> profils = new HashSet<ProfilsUtilisateur>();
			profils.add(ProfilsUtilisateur.UTILISATEUR);
			profils.add(ProfilsUtilisateur.GERANT);
			createur.setProfils(profils);

			/** When: Lorsqu'on appelle le service de mise à jour. */
			services.update(createur, utilisateurAModifier);
			fail("Une exception devrait être levée.");
		} catch (UtilisateurInvalideException e) {
			fail("Une exception de droits d'accès devrait être levée.");
		} catch (DroitAccesException e) {
			/** Then: Alors une exception est levée. */
			Assert.assertEquals(DroitAccesException.ErreurDroits.ACCES_ADMINISTRATEUR, e.getErreur());
		}
	}

}
