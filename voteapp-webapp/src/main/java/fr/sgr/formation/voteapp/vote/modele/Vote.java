package fr.sgr.formation.voteapp.vote.modele;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
public class Vote {
	@Id
	@GeneratedValue
	private long id;
	private Utilisateur utilisateur;
	private ValeurVote valeurvote;

}
