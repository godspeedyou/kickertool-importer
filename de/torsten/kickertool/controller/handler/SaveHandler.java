package de.torsten.kickertool.controller.handler;

import java.util.Collection;

import de.torsten.kickertool.database.DatabaseConnector;
import de.torsten.kickertool.model.Game;
import de.torsten.kickertool.model.Player;
import de.torsten.kickertool.model.Team;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/************************************************
 * 
 * copyright (c) energy & meteo systems GmbH, 2016
 * 
 * mail@energymeteo.com www.energymeteo.com
 * 
 ************************************************/

public final class SaveHandler implements EventHandler<ActionEvent> {
	private ObservableList<Player> existingPlayers;
	private Collection<Game> games;

	public SaveHandler(ObservableList<Player> existingPlayers, Collection<Game> games) {
		this.existingPlayers = existingPlayers;
		this.games = games;
	}

	@Override
	public void handle(ActionEvent event) {
		savePlayers(existingPlayers);
		saveTeams(games);
		saveGames(games);
	}

	private void saveTeams(Collection<Game> gamesToSaveTeamsFrom) {
		for (Game game : gamesToSaveTeamsFrom) {
			for (Team team : game.getTeams()) {
				new DatabaseConnector().saveTeam(team);
			}
		}
	}

	private static void savePlayers(Collection<Player> existingPlayers) {
		DatabaseConnector conn = new DatabaseConnector();
		for (Player player : existingPlayers) {
			conn.save(player);
		}
	}

	private void saveGames(Collection<Game> gamesToSave) {
		DatabaseConnector conn = new DatabaseConnector();
		for (Game game : gamesToSave) {
			conn.save(game);
		}
	}
}