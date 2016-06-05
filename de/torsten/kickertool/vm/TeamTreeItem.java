/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.vm;

import java.util.stream.Collectors;

import de.torsten.kickertool.model.Team;
import de.torsten.kickertool.view.GenericTreeItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TeamTreeItem implements GenericTreeItem<PlayerTreeItem> {

	private final Team team;

	public TeamTreeItem(Team team) {
		this.team = team;
	}

	@Override
	public ObservableList<PlayerTreeItem> getChildren() {
		return FXCollections
				.observableArrayList(team.getPlayers().stream().map(PlayerTreeItem::new).collect(Collectors.toList()));
	}

	@Override
	public String getName() {
		// TODO von der anderen Stelle wiederverwenden
		return null;
	}

}
