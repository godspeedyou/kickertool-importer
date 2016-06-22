package de.torsten.kickertool.controller.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import de.torsten.kickertool.controller.GameController;
import de.torsten.kickertool.controller.GameController.IteratorSupplier;
import de.torsten.kickertool.model.Game;
import de.torsten.kickertool.model.Player;
import de.torsten.kickertool.model.PlayerPoints;
import de.torsten.kickertool.view.TabPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/************************************************
 * 
 * copyright (c) energy & meteo systems GmbH, 2016
 * 
 * mail@energymeteo.com www.energymeteo.com
 * 
 ************************************************/

@SuppressWarnings("nls")
public final class FirstGamePlayersImporter implements EventHandler<ActionEvent> {
	private final Collection<Player> existingPlayers;
	private final TabPane<Game> tabPane;

	public FirstGamePlayersImporter(Collection<Player> existingPlayers, TabPane<Game> tabPane) {
		this.existingPlayers = existingPlayers;
		this.tabPane = tabPane;
	}

	@Override
	public void handle(ActionEvent event) {
		Game game = new Game();
		game.setCreated("31.03.2016");
		game.getPlayers().addAll(createPlayersForFirstGame());
		game.setPlayerPoints(game.getPlayers().stream().map(p -> new PlayerPoints(p)).collect(Collectors.toList()));
		IteratorSupplier pointsSupplier = new IteratorSupplier(GameController.getDypTourPoints());
		game.getPlayerPoints().forEach(p -> p.setPoints(pointsSupplier.get()));
		game.init();
		IteratorSupplier inGameSupplier = new IteratorSupplier(
				Arrays.asList(17, 11, 11, 11, 11, 11, 11, 9, 9, 9, 8, 8, 7, 7, 6, 6, 2, 2));
		for (Player player : game.getPlayers()) {
			game.addInGamePoints(player, inGameSupplier.get());
		}
		tabPane.addTab(game);
	}

	private Collection<Player> createPlayersForFirstGame() {
		Collection<Player> players = new ArrayList<>();
		for (String name : Arrays.asList("Jonathan Fiola", "Torben Klein", "David Schünemann", "Alexander Rancke",
				"Sebastian Dorschner", "Benjamin Dammann", "Claas Weymann", "Malte Wach", "Mohamed Mardini",
				"Birte Perner", "Sarah Schonenenberg", "Torben Kreye", "Martin Schippling", "Thomas Böttcher",
				"Sarah Jaeck", "Dennis Thyen", "Viktor Koopmann", "Ole Rust")) {
			Player player = new Player();
			player.setName(name);
			players.add(player);
		}

		return players;
	}

}