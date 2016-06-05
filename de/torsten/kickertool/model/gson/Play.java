/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.model.gson;

import java.util.Collection;

public class Play {
	private Team team1;
	private Team team2;
	private Collection<Discipline> disciplines;
	private int round;

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public Collection<Discipline> getDisciplines() {
		return disciplines;
	}

	public Team getTeam1() {
		return team1;
	}

	public Team getTeam2() {
		return team2;
	}

	public void setTeam1(Team team1) {
		this.team1 = team1;
	}

	public void setTeam2(Team team2) {
		this.team2 = team2;
	}
}
