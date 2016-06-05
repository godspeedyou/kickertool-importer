/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.vm;

import de.torsten.kickertool.model.Player;
import de.torsten.kickertool.view.GenericTreeItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlayerTreeItem implements GenericTreeItem<GenericTreeItem<?>> {
	private final Player player;

	public PlayerTreeItem(Player player) {
		this.player = player;
	}

	@Override
	public ObservableList<GenericTreeItem<?>> getChildren() {
		return FXCollections.emptyObservableList();
	}

	@Override
	public String getName() {
		return player.getName();
	}

	@Override
	public String toString() {
		return getName();
	}

}
