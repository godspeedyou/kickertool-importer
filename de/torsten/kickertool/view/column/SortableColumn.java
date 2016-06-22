/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.view.column;

import com.google.common.collect.ComparisonChain;

import de.torsten.kickertool.model.Player;
import javafx.scene.control.TableColumn;

public final class SortableColumn implements Comparable<SortableColumn> {
	private final TableColumn<Player, ?> column;
	private final int sortPosition;
	private final int rankSortPosition;
	private final boolean printable;
	private final String field;

	public SortableColumn(TableColumn<Player, ?> column, int sortPosition, int rankSortPosition, boolean printable,
			String field) {
		this.column = column;
		this.sortPosition = sortPosition;
		this.rankSortPosition = rankSortPosition;
		this.printable = printable;
		this.field = field;
	}

	public String getField() {
		return field;
	}

	public boolean isPrintable() {
		return printable;
	}

	@Override
	public int compareTo(SortableColumn that) {
		return ComparisonChain.start().compare(this.sortPosition, that.sortPosition).result();
	}

	public int getRankSortPosition() {
		return rankSortPosition;
	}

	public TableColumn<Player, ?> getColumn() {
		return column;
	}

	public int getSortPosition() {
		return sortPosition;
	}
}