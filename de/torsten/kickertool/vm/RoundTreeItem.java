/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.vm;

import java.util.Collection;
import java.util.stream.Collectors;

import de.torsten.kickertool.model.Set;
import de.torsten.kickertool.view.GenericTreeItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RoundTreeItem implements GenericTreeItem<SetTreeItem> {

	private final int round;
	private final Collection<Set> sets;

	/**
	 * TODO auch die Teams einlesen
	 * 
	 * @param round
	 * @param sets
	 */
	public RoundTreeItem(Integer round, Collection<Set> sets) {
		this.round = round;
		this.sets = sets;
	}

	/**
	 * TODO nicht die nackten Sets, sondern synthetische Objekte von Sets kombiniert mit Teams ausgeben, um direkt die
	 * Namen mit den Ergebnissen zu verbinden
	 */
	@Override
	public ObservableList<SetTreeItem> getChildren() {
		return sets.stream().map(SetTreeItem::new).collect(Collectors.toCollection(FXCollections::observableArrayList));
	}

	@Override
	public String getName() {
		return String.valueOf(round);
	}

	@Override
	public String toString() {
		return getName();
	}
}
