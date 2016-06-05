/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Play {
	private Team team1;
	private Team team2;
	private Collection<Discipline> disciplines;
	private int identifier;
	private int round;

	public Play() {
	}

	public Play(de.torsten.kickertool.model.gson.Play play, Map<String, Team> idToTeam) {
		this.team1 = idToTeam.get(play.getTeam1().getId());
		this.team2 = idToTeam.get(play.getTeam2().getId());
		this.disciplines = createDisciplines(play.getDisciplines());
		this.round = play.getRound();
	}

	private Collection<Discipline> createDisciplines(
			Collection<de.torsten.kickertool.model.gson.Discipline> gsonDisciplines) {
		Collection<Discipline> newDisciplines = new ArrayList<>();
		for (de.torsten.kickertool.model.gson.Discipline discipline : gsonDisciplines) {
			newDisciplines.add(new Discipline(discipline));
		}
		return newDisciplines;
	}

	public void setTeam1(Team team1) {
		this.team1 = team1;
	}

	public void setTeam2(Team team2) {
		this.team2 = team2;
	}

	public void setDisciplines(Collection<Discipline> disciplines) {
		this.disciplines = disciplines;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getIdentifier() {
		return identifier;
	}

	@OneToMany(cascade = javax.persistence.CascadeType.ALL)
	public Collection<Discipline> getDisciplines() {
		return disciplines;
	}

	@ManyToOne
	public Team getTeam1() {
		return team1;
	}

	@ManyToOne
	public Team getTeam2() {
		return team2;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}
}
