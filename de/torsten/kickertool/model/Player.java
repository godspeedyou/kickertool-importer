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
import javax.persistence.Transient;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

import de.torsten.kickertool.view.column.ColumnType;
import de.torsten.kickertool.view.column.ViewColumn;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

@Entity
public class Player implements Comparable<Player> {
	public static final String FIELD_NAME = "name"; //$NON-NLS-1$
	public static final String FIELD_POINTS = "points"; //$NON-NLS-1$
	public static final String FIELD_GAMES = "games"; //$NON-NLS-1$
	public static final String FIELD_MONTHLY_PAYER = "monthlyPayer"; //$NON-NLS-1$
	public static final String FIELD_MONEY = "money"; //$NON-NLS-1$
	public static final String FIELD_IN_GAME_POINTS = "inGamePoints"; //$NON-NLS-1$
	public static final String FIELD_POSITION = "position"; //$NON-NLS-1$
	public static final String TITLE_GAMES = "Anzahl Dyp-Teilnahmen"; //$NON-NLS-1$
	public static final String TITLE_IN_GAME_POINTS = "Punkte innerhalb aller Teilnahmen"; //$NON-NLS-1$
	private String id;
	@ViewColumn(title = "Monatsbeitragzahler", columnPosition = 40)
	private final SimpleBooleanProperty monthlyPayer = new SimpleBooleanProperty();

	private int identifier;

	@ViewColumn(title = "#", columnPosition = 0, isPrintable = true)
	private final SimpleIntegerProperty position = new SimpleIntegerProperty();
	@ViewColumn(title = "Name", columnPosition = 10, type = ColumnType.EDITABLE, isPrintable = true)
	private final SimpleStringProperty name = new SimpleStringProperty();
	@ViewColumn(title = "Punkte", columnPosition = 20, rankSortPosition = 0, type = ColumnType.EDITABLE, isPrintable = true)
	private final SimpleIntegerProperty points = new SimpleIntegerProperty();
	@ViewColumn(title = "Punkte \u2300", columnPosition = 25, isPrintable = true)
	private final SimpleDoubleProperty averagePoints = new SimpleDoubleProperty();
	@ViewColumn(title = TITLE_GAMES, columnPosition = 30, rankSortPosition = 10, isPrintable = true)
	private final SimpleIntegerProperty games = new SimpleIntegerProperty();
	@ViewColumn(title = TITLE_IN_GAME_POINTS, columnPosition = 50, rankSortPosition = 20, isPrintable = true)
	private final SimpleIntegerProperty inGamePoints = new SimpleIntegerProperty();
	@ViewColumn(title = "Tore (+)", columnPosition = 60, isPrintable = true)
	private final SimpleIntegerProperty goalsPositive = new SimpleIntegerProperty();
	@ViewColumn(title = "Tore (+) \u2300", columnPosition = 70, isPrintable = true)
	private final SimpleDoubleProperty averageGoalsPositive = new SimpleDoubleProperty();
	@ViewColumn(title = "Tore (-)", columnPosition = 80, isPrintable = true)
	private final SimpleIntegerProperty goalsNegative = new SimpleIntegerProperty();
	@ViewColumn(title = "Tore (-) \u2300", columnPosition = 90, isPrintable = true)
	private final SimpleDoubleProperty averageGoalsNegative = new SimpleDoubleProperty();
	@ViewColumn(title = "Tore (+/-)", columnPosition = 100, isPrintable = true)
	private final SimpleIntegerProperty goalsSum = new SimpleIntegerProperty();
	@ViewColumn(title = "Tore (+/-) \u2300", columnPosition = 130, isPrintable = true)
	private final SimpleDoubleProperty averageGoalsSum = new SimpleDoubleProperty();
	@ViewColumn(title = "Geldgewinn", columnPosition = 140, isPrintable = true)
	private final SimpleDoubleProperty money = new SimpleDoubleProperty();
	@ViewColumn(title = "Prozent am Gesamtgewinn", columnPosition = 145, isPrintable = true)
	private final SimpleIntegerProperty moneyPercentage = new SimpleIntegerProperty();
	@ViewColumn(title = "Reingewinn", columnPosition = 150)
	private final SimpleDoubleProperty margin = new SimpleDoubleProperty();

	// TODO Teilnahme an Datum anzeigen
	// TODO beste Teams (welche Teams haben am meisten gewonnen

	public Player() {
	}

	public Player(String name, int points, int games) {
		this.name.set(name);
		this.points.set(points);
		this.games.set(games);
	}

	public Player(de.torsten.kickertool.model.gson.Player player) {
		this.id = player.getId();
		this.name.set(player.getName());
	}

	public boolean isMonthlyPayer() {
		return monthlyPayer.get();
	}

	public void setMonthlyPayer(boolean monthlyPayer) {
		this.monthlyPayer.set(monthlyPayer);
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

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	@Transient
	public int getPoints() {
		return points.get();
	}

	public void setPoints(int points) {
		this.points.set(points);
	}

	public void addPoints(int newPoints) {
		points.set(points.get() + newPoints);
	}

	@Transient
	public int getInGamePoints() {
		return inGamePoints.get();
	}

	public void setInGamePoints(int points) {
		this.inGamePoints.set(points);
	}

	public void addInGamePoints(int newPoints) {
		inGamePoints.set(inGamePoints.get() + newPoints);
	}

	@Transient
	public int getPosition() {
		return position.get();
	}

	public void setPosition(int position) {
		this.position.set(position);
	}

	@Transient
	public int getGames() {
		return games.get();
	}

	public void setGames(int games) {
		this.games.set(games);
	}

	public void addGames(int newGames) {
		games.set(games.get() + newGames);
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("name", name).add("points", points).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Player) {
			Player that = (Player) object;
			return Objects.equal(this.name, that.name);
		}
		return false;
	}

	@Override
	public int compareTo(Player that) {
		return ComparisonChain.start().
		// Hohe Punkte sollen an den Anfang
				compare(that.points.get(), this.points.get()).compare(that.games.get(), this.games.get())
				.compare(that.getInGamePoints(), this.getInGamePoints()).result();
	}

	public ObservableValue<Boolean> monthlyPayerProperty() {
		return monthlyPayer;
	}

	@Transient
	public double getMoney() {
		return money.get();
	}

	public void setMoney(double money) {
		this.money.set(money);
	}

	@Transient
	public int getMoneyPercentage() {
		return moneyPercentage.get();
	}

	public void setMoneyPercentage(int moneyPercentage) {
		this.moneyPercentage.set(moneyPercentage);
	}

	@Transient
	public double getAveragePoints() {
		return averagePoints.get();
	}

	public void setAveragePoints(double averagePoints) {
		this.averagePoints.set(averagePoints);
	}

	@Transient
	public double getMargin() {
		return margin.get();
	}

	public void setMargin(double margin) {
		this.margin.set(margin);
	}

	@Transient
	public int getGoalsPositive() {
		return goalsPositive.get();
	}

	public void setGoalsPositive(int goalsPositive) {
		this.goalsPositive.set(goalsPositive);
	}

	public void addGoalsPositive(int newGoalsPositive) {
		goalsPositive.set(goalsPositive.get() + newGoalsPositive);
	}

	public void setGoalsNegative(int goalsNegative) {
		this.goalsNegative.set(goalsNegative);
	}

	@Transient
	public int getGoalsNegative() {
		return goalsNegative.get();
	}

	public void addGoalsNegative(int newGoalsNegative) {
		goalsNegative.set(goalsNegative.get() - newGoalsNegative);
	}

	@Transient
	public int getGoalsSum() {
		return goalsSum.get();
	}

	public void setGoalsSum(int goalsSum) {
		this.goalsSum.set(goalsSum);
	}

	@Transient
	public double getAverageGoalsPositive() {
		return averageGoalsPositive.get();
	}

	public void setAverageGoalsPositive(double averageGoalsPositive) {
		this.averageGoalsPositive.set(averageGoalsPositive);
	}

	@Transient
	public double getAverageGoalsNegative() {
		return averageGoalsNegative.get();
	}

	public void setAverageGoalsNegative(double averageGoalsNegative) {
		this.averageGoalsNegative.set(averageGoalsNegative);
	}

	@Transient
	public double getAverageGoalsSum() {
		return averageGoalsSum.get();
	}

	public void setAverageGoalsSum(double averageGoalsSum) {
		this.averageGoalsSum.set(averageGoalsSum);
	}

}
