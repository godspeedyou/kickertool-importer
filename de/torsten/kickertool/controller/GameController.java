/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.google.common.collect.ComparisonChain;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.torsten.kickertool.importer.Importer;
import de.torsten.kickertool.model.Discipline;
import de.torsten.kickertool.model.Game;
import de.torsten.kickertool.model.Play;
import de.torsten.kickertool.model.Player;
import de.torsten.kickertool.model.Set;
import de.torsten.kickertool.model.Team;

public class GameController {

	private static final Logger LOGGER = Logger.getLogger(GameController.class.getSimpleName());

	public static class IteratorSupplier implements Supplier<Integer> {
		private final Iterator<Integer> iterator;
		private int current;

		public IteratorSupplier(Collection<Integer> collection) {
			this.iterator = collection.iterator();
		}

		@Override
		public Integer get() {
			if (iterator.hasNext()) {
				current = iterator.next();
			}
			return current;
		}
	}

	private static final Collection<Integer> DYP_TOUR_POINTS = Arrays.asList(6, 6, 5, 5, 4, 4, 3, 3, 2);

	private static final Collection<Integer> DYP_TOUR_POT_PERCENTAGES = Arrays.asList(20, 15, 12, 10, 9, 8, 7, 6, 5, 4,
			2, 2, 0);

	private static final int POINTS_DRAW = 1;
	private static final int POINTS_WIN = 2;

	public GameController() {
	}

	public Game importGame(File file) {
		de.torsten.kickertool.model.gson.Game gsonGame;
		String absolutePath = file.getAbsolutePath();
		try {
			gsonGame = new Importer().importGame(absolutePath);
		} catch (JsonSyntaxException | JsonIOException | IOException e) {
			LOGGER.severe("Could not load" + absolutePath); //$NON-NLS-1$
			return null;
		}
		GsonGameController gsonGameController = new GsonGameController();
		gsonGameController.syncTeams(gsonGame);
		gsonGameController.syncPlays(gsonGame);
		Game game = new Game(gsonGame, file.getName());
		calculatePoints(game);

		return game;
	}

	public void calculatePoints(Game game) {
		addPoints(game);

		List<Player> players = new ArrayList<>(game.getPlayers());
		Collections.sort(players, Collections.reverseOrder(new Comparator<Player>() {

			@Override
			public int compare(Player o1, Player o2) {
				return ComparisonChain.start().compare(game.getInGamePoints(o1), game.getInGamePoints(o2)).result();
			}
		}));

		if (game.getPlayerPoints().stream().allMatch(p -> p.getPoints() == 0)) {
			Supplier<Integer> pointsSupplier = new IteratorSupplier(DYP_TOUR_POINTS);
			for (Player player : players) {
				game.addDypPoints(player, pointsSupplier.get());
			}
		}
	}

	public void syncPlayers(Collection<Player> existingPlayers, Game game) {
		Collection<Player> newPlayers = new ArrayList<>();
		current: for (Player player : game.getPlayers()) {
			for (Player existingPlayer : existingPlayers) {
				if (existingPlayer.getName().equals(player.getName())) {
					player.setId(existingPlayer.getId());
					player.setIdentifier(existingPlayer.getIdentifier());
					existingPlayer.addPoints(game.getDypPoints(player));
					existingPlayer.addGames(1);
					existingPlayer.addInGamePoints(game.getInGamePoints(player));
					continue current;
				}
			}
			player.addInGamePoints(game.getInGamePoints(player));
			player.addPoints(game.getDypPoints(player));
			player.addGames(1);
			newPlayers.add(player);
		}
		existingPlayers.addAll(newPlayers);

		double pot = existingPlayers.stream().mapToInt(Player::getGames).sum();

		List<Player> sortedPlayers = getDypPointSortedPlayers(existingPlayers);
		IteratorSupplier percentages = new IteratorSupplier(DYP_TOUR_POT_PERCENTAGES);
		for (Player player : sortedPlayers) {
			player.setMoney(pot * percentages.get() / 100.);
		}

		int i = 1;
		for (Player player : sortedPlayers) {
			player.setPosition(i++);
		}
	}

	private List<Player> getDypPointSortedPlayers(Collection<Player> existingPlayers) {
		List<Player> sortedPlayers = new ArrayList<>(existingPlayers);
		Collections.sort(sortedPlayers);
		return sortedPlayers;
	}

	private static void syncGame(Game game, Collection<Player> existingPlayers) {
		for (Player player : game.getPlayers()) {
			for (Player existingPlayer : existingPlayers) {
				if (player.getName().equals(existingPlayer.getName())) {
					player.setIdentifier(existingPlayer.getIdentifier());
					break;
				}
			}
		}
	}

	private void addPoints(Game game) {
		for (Play play : game.getPlays()) {
			Collection<Discipline> disciplines = play.getDisciplines();
			if (disciplines == null) {
				continue;
			}

			Team team1 = play.getTeam1();
			Team team2 = play.getTeam2();
			for (Discipline discipline : disciplines) {
				for (Set set : discipline.getSets()) {
					Team winnerTeam = getWinnerTeam(team1, team2, set);
					if (winnerTeam != null) {
						addPoints(game, winnerTeam, POINTS_WIN);
					} else {
						addPoints(game, team1, POINTS_DRAW);
						addPoints(game, team2, POINTS_DRAW);
					}
				}
			}
		}
	}

	private void addPoints(Game game, Team team, int points) {
		for (Player player : team.getPlayers()) {
			game.addInGamePoints(player, points);
		}
	}

	private Team getWinnerTeam(Team team1, Team team2, Set set) {
		if (set.getTeam1() > set.getTeam2()) {
			return team1;
		} else if (set.getTeam2() > set.getTeam1()) {
			return team2;
		}
		return null;
	}

	public static Collection<Integer> getDypTourPoints() {
		return DYP_TOUR_POINTS;
	}

}
