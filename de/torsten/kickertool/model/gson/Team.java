/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.model.gson;

import java.util.Collection;

public class Team {
	private String id;

	private Collection<Player> players;

	public String getId() {
		return id;
	}

	public Collection<Player> getPlayers() {
		return players;
	}

}
