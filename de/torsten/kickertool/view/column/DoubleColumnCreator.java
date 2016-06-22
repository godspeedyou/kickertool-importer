/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.view.column;

import java.text.NumberFormat;

import de.torsten.kickertool.model.Player;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

public class DoubleColumnCreator extends ColumnCreator<Double> {
	private final class DoublePrecisionStringConverter extends StringConverter<Double> {
		private final NumberFormat nf = NumberFormat.getNumberInstance();

		public DoublePrecisionStringConverter() {
			nf.setMaximumFractionDigits(2);
			nf.setMinimumFractionDigits(0);
		}

		@Override
		public String toString(final Double value) {
			return nf.format(value);
		}

		@Override
		public Double fromString(final String s) {
			// Don't need this, unless table is editable, see DoubleStringConverter if needed
			return null;
		}
	}

	@Override
	TableColumn<Player, Double> createSimpleColumn(String text, String field) {
		TableColumn<Player, Double> col = super.createSimpleColumn(text, field);
		col.setCellFactory(TextFieldTableCell.<Player, Double> forTableColumn(new DoublePrecisionStringConverter()));
		return col;
	}
}
