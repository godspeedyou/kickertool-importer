/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.view.column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import de.torsten.kickertool.model.Player;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class ColumnCreator<T> {
	private static TableColumn<Player, ?> createColumn(Field field, ViewColumn viewCol) {
		ColumnCreator<?> columnCreator = createColumnCreator(field, viewCol);
		return columnCreator.createSimpleColumn(viewCol.title(), field.getName());
	}

	private static ColumnCreator<?> createColumnCreator(Field field, ViewColumn viewCol) {
		Class<?> fieldClass = field.getType();
		if (fieldClass.equals(SimpleBooleanProperty.class)) {
			return new BooleanColumnCreator();
		} else if (fieldClass.equals(SimpleDoubleProperty.class)) {
			return new DoubleColumnCreator();
		} else if (viewCol.type() == ColumnType.EDITABLE) {
			if (fieldClass.equals(String.class)) {
				return new StringColumnCreator();
			} else if (fieldClass.equals(Integer.class)) {
				return new NumberColumnCreator();
			}
		}
		return new ColumnCreator<>();
	}

	void setEditableFields(BiConsumer<Player, T> biConsumer,
			Callback<TableColumn<Player, T>, TableCell<Player, T>> tableCell, TableColumn<Player, T> col) {
		col.setCellFactory(tableCell);
		col.setOnEditCommit(new EventHandler<CellEditEvent<Player, T>>() {
			@Override
			public void handle(CellEditEvent<Player, T> t) {
				ObservableList<Player> rows = t.getTableView().getItems();
				Player selected = rows.get(t.getTablePosition().getRow());
				biConsumer.accept(selected, t.getNewValue());
			}
		});
	}

	TableColumn<Player, T> createSimpleColumn(String text, String field) {
		TableColumn<Player, T> moneyCol = new TableColumn<>(text);
		moneyCol.setCellValueFactory(new PropertyValueFactory<>(field));
		return moneyCol;
	}

	public Collection<SortableColumn> createColumns() {
		Class<Player> playerClass = Player.class;
		Field[] fields = playerClass.getDeclaredFields();
		List<SortableColumn> simpleColumns = new ArrayList<>();
		for (Field field : fields) {
			simpleColumns.add(createColumn(field));
		}
		return simpleColumns.stream().filter(Objects::nonNull).sorted().collect(Collectors.toList());
	}

	private SortableColumn createColumn(Field field) {
		ViewColumn viewCol = field.getAnnotation(ViewColumn.class);
		if (viewCol != null) {
			TableColumn<Player, ?> simpleCol = ColumnCreator.createColumn(field, viewCol);
			return new SortableColumn(simpleCol, viewCol.columnPosition(), viewCol.rankSortPosition(),
					viewCol.isPrintable(), field.getName());
		}
		return null;
	}
}