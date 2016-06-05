/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.view;

import javafx.collections.ObservableList;

public interface GenericTreeItem<T extends GenericTreeItem<?>> {
	ObservableList<T> getChildren();

	String getName();
}
