package fr.sgr.formation.voteapp.utilisateurs.modele;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(of = { "codePostal" })
public class Ville {
	@Id
	private String codePostal;
	private String nom;
}
