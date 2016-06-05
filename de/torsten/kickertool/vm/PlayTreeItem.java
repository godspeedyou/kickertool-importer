/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.vm;

import java.util.stream.Collectors;

import de.torsten.kickertool.model.Play;
import de.torsten.kickertool.view.GenericTreeItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@SuppressWarnings("nls")
public class PlayTreeItem implements GenericTreeItem<SetTreeItem> {

	private final Play play;

	public PlayTreeItem(Play play) {
		this.play = play;
	}

	@Override
	public ObservableList<SetTreeItem> getChildren() {
		return FXCollections.observableArrayList(play.getDisciplines().stream().flatMap(d -> d.getSets().stream())
				.map(SetTreeItem::new).collect(Collectors.toList()));
	}

	@Override
	public String getName() {
		return String.format("%s vs. %s.", play.getTeam1(), play.getTeam2());
	}

	@Override
	public String toString() {
		return getName();
	}

}
