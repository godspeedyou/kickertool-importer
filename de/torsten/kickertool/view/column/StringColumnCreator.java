/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.view.column;

import de.torsten.kickertool.model.Player;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;

public class StringColumnCreator extends ColumnCreator<String> {
	@Override
	protected TableColumn<Player, String> createSimpleColumn(String text, String field) {
		TableColumn<Player, String> col = super.createSimpleColumn(text, field);
		setEditableFields((pl, po) -> pl.setName(po), TextFieldTableCell.forTableColumn(), col);
		return col;
	}
}
