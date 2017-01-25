package fr.sgr.formation.voteapp.traces.modele;

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
public class Trace {
	@Id
	@GeneratedValue
	long id;
	TypeAction typeAction;
	@Temporal(TemporalType.DATE)
	Date date;
	String resultat;
	String description;
	@ManyToOne
	Utilisateur proprietaire;
}
