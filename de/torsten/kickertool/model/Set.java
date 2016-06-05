/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "GameSet")
public class Set {
	// private String id;
	private int team1;
	private int team2;
	private int identifier;

	public Set() {
	}

	public Set(de.torsten.kickertool.model.gson.Set set) {
		// this.id = set.getId();
		this.team1 = set.getTeam1();
		this.team2 = set.getTeam2();
	}

	// @Id
	// public String getId() {
	// return id;
	// }
	//
	// public void setId(String id) {
	// this.id = id;
	// }
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public int getTeam1() {
		return team1;
	}

	public void setTeam1(int team1) {
		this.team1 = team1;
	}

	public int getTeam2() {
		return team2;
	}

	public void setTeam2(int team2) {
		this.team2 = team2;
	}
}
