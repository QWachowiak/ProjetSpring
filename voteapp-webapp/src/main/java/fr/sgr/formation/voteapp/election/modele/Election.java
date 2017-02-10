package fr.sgr.formation.voteapp.election.modele;

import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import fr.sgr.formation.voteapp.utilisateurs.modele.Utilisateur;
import fr.sgr.formation.voteapp.vote.modele.Vote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@EqualsAndHashCode(of = { "id" })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Election {

	@Id
	@GeneratedValue
	private long id;
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

	@ElementCollection(targetClass = Vote.class)
	@CollectionTable(name = "votes", joinColumns = @JoinColumn(name = "id") )
	@Singular
	private List<Vote> votes;
}
