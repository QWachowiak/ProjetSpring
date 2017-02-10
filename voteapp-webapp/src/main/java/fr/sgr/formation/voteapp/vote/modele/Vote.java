package fr.sgr.formation.voteapp.vote.modele;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

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
public class Vote {
	@Id
	@GeneratedValue
	private long id;
	@OneToOne
	private Utilisateur utilisateur;
	private ValeurVote valeurvote;

}
