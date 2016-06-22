/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.view.column;

import de.torsten.kickertool.model.Player;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;

public class BooleanColumnCreator extends ColumnCreator<Boolean> {
	@Override
	protected TableColumn<Player, Boolean> createSimpleColumn(String text, String field) {
		TableColumn<Player, Boolean> col = super.createSimpleColumn(text, field);
		col.setCellFactory(CheckBoxTableCell.forTableColumn(col));
		return col;
	}
}
