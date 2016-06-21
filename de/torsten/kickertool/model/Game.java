/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.google.common.base.Objects;

/**
 * TODO Ranking speichern: Verbindung aus Player und Platz, also Extratabelle, Referenz auf Player und Attribut Platz.
 * Das ganze wird dann manuell eingegeben und kann dann nach dem Speichern automatisch wieder geladen werden
 * 
 * @author torsten
 *
 */
@Entity
public class Game {
	private String id;

	private int identifier;

	private String created;

	private Collection<Play> plays = new ArrayList<>();

	private Collection<Team> teams = new ArrayList<>();

	private Collection<Player> players = new ArrayList<>();

	private String name;

	private final Map<String, Player> idToPlayer = new HashMap<>();

	private final Map<String, Team> idToTeam = new HashMap<>();

	private final Map<Player, PlayerPoints> playerToInGamePoints = new HashMap<>();
	private final Map<Player, PlayerPoints> playerToDypPoints = new HashMap<>();

	private Collection<PlayerPoints> playerPoints = new ArrayList<>();

	private final Map<Player, Integer> playerToPositiveGoals = new HashMap<>();

	private final Map<Player, Integer> playerToNegativeGoals = new HashMap<>();

	public Game() {
	}

	public Game(de.torsten.kickertool.model.gson.Game gsonGame, String created) {
		this.name = gsonGame.getName();
		this.created = created;
		this.id = gsonGame.getId();
		for (de.torsten.kickertool.model.gson.Player player : gsonGame.getPlayers()) {
			Player p = new Player(player);
			this.idToPlayer.put(p.getId(), p);
			this.players.add(p);
		}
		this.teams.addAll(createTeams(gsonGame.getTeams()));
		this.plays.addAll(createPlays(gsonGame.getPlays()));
		init();
	}

	public void init() {
		initPlayerPoints(playerToInGamePoints);
		if (playerPoints.isEmpty()) {
			initPlayerPoints(playerToDypPoints);
			playerPoints.addAll(playerToDypPoints.values());
		} else {
			playerPoints.stream().forEach(p -> playerToDypPoints.put(p.getPlayer(), p));
		}
		this.players.stream().forEach(p -> playerToPositiveGoals.put(p, 0));
		this.players.stream().forEach(p -> playerToNegativeGoals.put(p, 0));
	}

	private void initPlayerPoints(Map<Player, PlayerPoints> playerToPoints) {
		this.players.stream().forEach(p -> playerToPoints.put(p, new PlayerPoints(p)));
	}

	public void addInGamePoints(Player player, int points) {
		playerToInGamePoints.get(player).add(points);
	}

	public void addPositiveGoals(Player player, int positiveGoals) {
		playerToPositiveGoals.put(player, playerToPositiveGoals.get(player) + positiveGoals);
	}

	public void addNegativeGoals(Player player, int negativeGoals) {
		playerToNegativeGoals.put(player, playerToNegativeGoals.get(player) + negativeGoals);
	}

	public void addDypPoints(Player player, int points) {
		playerToDypPoints.get(player).add(points);
	}

	private Collection<Play> createPlays(Collection<de.torsten.kickertool.model.gson.Play> gsonTeams) {
		Collection<Play> newTeams = new ArrayList<>();
		for (de.torsten.kickertool.model.gson.Play play : gsonTeams) {
			newTeams.add(new Play(play, idToTeam));
		}
		return newTeams;
	}

	private Collection<Team> createTeams(Collection<de.torsten.kickertool.model.gson.Team> gsonTeams) {
		Collection<Team> newTeams = new ArrayList<>();
		for (de.torsten.kickertool.model.gson.Team team : gsonTeams) {
			Team t = new Team(team, idToPlayer);
			newTeams.add(t);
			idToTeam.put(t.getId(), t);
		}
		return newTeams;
	}

	@Transient
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

	@ManyToMany
	public Collection<Player> getPlayers() {
		return players;
	}

	public void setPlayers(Collection<Player> players) {
		this.players = players;
	}

	@OneToMany(cascade = javax.persistence.CascadeType.ALL)
	public Collection<Play> getPlays() {
		return plays;
	}

	public void setPlays(Collection<Play> plays) {
		this.plays = plays;
	}

	@OneToMany
	public Collection<Team> getTeams() {
		return teams;
	}

	public void setTeams(Collection<Team> teams) {
		this.teams = teams;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("created", created).toString();
	}

	public void setInGamePoints(Player selected, int points) {
		this.playerToInGamePoints.get(selected).setPoints(points);
	}

	public int getInGamePoints(Player value) {
		return playerToInGamePoints.get(value).getPoints();
	}

	public void setDypPoints(Player selected, int points) {
		this.playerToDypPoints.get(selected).setPoints(points);
	}

	public int getDypPoints(Player value) {
		PlayerPoints points = playerToDypPoints.get(value);
		if (points == null) {
			return 0;
		}
		return points.getPoints();
	}

	public int getGoalsPositive(Player player) {
		Integer points = playerToPositiveGoals.get(player);
		if (points == null) {
			return 0;
		}
		return points;
	}

	public int getGoalsNegative(Player player) {
		Integer points = playerToNegativeGoals.get(player);
		if (points == null) {
			return 0;
		}
		return points;
	}

	@OneToMany(cascade = javax.persistence.CascadeType.ALL)
	public Collection<PlayerPoints> getPlayerPoints() {
		return playerPoints;
	}

	public void setPlayerPoints(Collection<PlayerPoints> playerPoints) {
		this.playerPoints = playerPoints;
	}

}
