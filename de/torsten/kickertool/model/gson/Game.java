/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.model.gson;

import java.util.Collection;

import com.google.common.base.Objects;

public class Game {
	private String id;

	private String created;

	private Collection<Play> plays;

	private Collection<Team> teams;

	private Collection<Player> players;

	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public Collection<Player> getPlayers() {
		return players;
	}

	public void setPlayers(Collection<Player> players) {
		this.players = players;
	}

	public Collection<Play> getPlays() {
		return plays;
	}

	public Collection<Team> getTeams() {
		return teams;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("created", created).toString();
	}

}
