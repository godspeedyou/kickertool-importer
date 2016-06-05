/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.google.common.base.Joiner;

@Entity
public class Team {

	private String id;
	private int identifier;

	private Collection<Player> players = new ArrayList<>();

	public Team() {
	}

	public Team(de.torsten.kickertool.model.gson.Team team, Map<String, Player> idToPlayer) {
		this.id = team.getId();
		for (de.torsten.kickertool.model.gson.Player player : team.getPlayers()) {
			players.add(idToPlayer.get(player.getId()));
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	@ManyToMany
	public Collection<Player> getPlayers() {
		return players;
	}

	public void setPlayers(Collection<Player> players) {
		this.players = players;
	}

	@Override
	public String toString() {
		// return Objects.toStringHelper(this).add("players", printTeamPlayers(this)).toString(); //$NON-NLS-1$
		return printTeamPlayers(this);
	}

	private String printTeamPlayers(Team team) {
		return Joiner.on(", ").join(team.getPlayers().stream().map(Player::getName).collect(Collectors.toList())); //$NON-NLS-1$
	}
}
