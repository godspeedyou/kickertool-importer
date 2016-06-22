/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.view.column;

import de.torsten.kickertool.model.Player;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.NumberStringConverter;

public class NumberColumnCreator extends ColumnCreator<Number> {
	@Override
	protected TableColumn<Player, Number> createSimpleColumn(String text, String field) {
		TableColumn<Player, Number> col = super.createSimpleColumn(text, field);
		setEditableFields((pl, po) -> pl.setPoints(po.intValue()),
				TextFieldTableCell.<Player, Number> forTableColumn(new NumberStringConverter()), col);
		return col;
	}
}
