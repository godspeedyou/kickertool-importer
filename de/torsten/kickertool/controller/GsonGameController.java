/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.controller;

import java.util.ArrayList;
import java.util.Collection;

import de.torsten.kickertool.model.gson.Game;
import de.torsten.kickertool.model.gson.Play;
import de.torsten.kickertool.model.gson.Player;
import de.torsten.kickertool.model.gson.Team;

public class GsonGameController {

	void syncTeams(Game game) {
		for (Team team : game.getTeams()) {
			Collection<Player> playersForTeam = getPlayerForTeam(game, team);
			team.getPlayers().clear();
			team.getPlayers().addAll(playersForTeam);
		}
	}

	void syncPlays(Game game) {
		for (Play play : game.getPlays()) {
			Team team1 = getTeam(game, play.getTeam1());
			Team team2 = getTeam(game, play.getTeam2());
			play.setTeam1(team1);
			play.setTeam2(team2);
		}
	}

	private Team getTeam(Game game, Team teamToFind) {
		for (Team team : game.getTeams()) {
			if (team.getId().equals(teamToFind.getId())) {
				return team;
			}
		}
		return null;
	}

	private Collection<Player> getPlayerForTeam(Game game, Team team) {
		Collection<Player> playersForTeam = new ArrayList<>();
		outer: for (Player teamPlayer : team.getPlayers()) {
			for (Player player : game.getPlayers()) {
				if (player.getId().equals(teamPlayer.getId())) {
					playersForTeam.add(player);
					continue outer;
				}
			}
		}
		return playersForTeam;
	}

}
