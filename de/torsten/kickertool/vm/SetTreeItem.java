/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.vm;

import com.google.common.base.Objects;

import de.torsten.kickertool.model.Set;
import de.torsten.kickertool.view.GenericTreeItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SetTreeItem implements GenericTreeItem<GenericTreeItem<?>> {
	private final Set set;

	public SetTreeItem(Set set) {
		this.set = set;
	}

	@Override
	public ObservableList<GenericTreeItem<?>> getChildren() {
		return FXCollections.emptyObservableList();
	}

	@SuppressWarnings("nls")
	@Override
	public String getName() {
		return Objects.toStringHelper(this).add("points", set.getTeam1() + ":" + set.getTeam2()).toString();
	}

	@Override
	public String toString() {
		return getName();
	}

}
