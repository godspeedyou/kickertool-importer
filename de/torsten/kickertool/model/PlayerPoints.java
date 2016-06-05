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
import javax.persistence.ManyToOne;

import javafx.beans.property.SimpleIntegerProperty;

@Entity
public class PlayerPoints {
	private final SimpleIntegerProperty points = new SimpleIntegerProperty();
	private int id;
	private Player player;

	public PlayerPoints() {
	}

	public PlayerPoints(Player player) {
		this.player = player;
	}

	public void add(int p) {
		this.points.set(this.points.get() + p);
	}

	public void setPoints(int points) {
		this.points.set(points);
	}

	public int getPoints() {
		return points.get();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
