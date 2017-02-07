package fr.sgr.formation.voteapp.election.modele;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = { "id" })
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Election {

	@Id
	@GeneratedValue
	long id;
	@ManyToOne
	Utilisateur utilisateurOrigine;
	@Temporal(TemporalType.DATE)
	Date dateDeDebut;
	@Temporal(TemporalType.DATE)
	Date dateDeFin;
	private String titre;
	private String description;
	private String image;
	private boolean cloture = false;

}
