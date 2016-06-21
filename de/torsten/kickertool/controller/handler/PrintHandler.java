package de.torsten.kickertool.controller.handler;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;

import de.torsten.kickertool.model.Game;
import de.torsten.kickertool.model.Player;
import de.torsten.kickertool.view.column.ColumnCreator;
import de.torsten.kickertool.view.column.SortableColumn;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/************************************************
 * 
 * copyright (c) energy & meteo systems GmbH, 2016
 * 
 * mail@energymeteo.com www.energymeteo.com
 * 
 ************************************************/

@SuppressWarnings("nls")
public final class PrintHandler implements EventHandler<ActionEvent> {
	private final Window stage;
	private final ObservableList<Player> existingPlayers;
	private static final Logger LOGGER = Logger.getLogger(PrintHandler.class.getSimpleName());
	private final Collection<Game> games;

	public PrintHandler(Window window, ObservableList<Player> existingPlayers, Collection<Game> games) {
		this.stage = window;
		this.existingPlayers = existingPlayers;
		this.games = games;
	}

	@Override
	public void handle(ActionEvent event) {
		File file = new FileChooser().showOpenDialog(stage);
		if (file == null) {
			return;
		}

		String table = createTable();

		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
			writer.write(table);
		} catch (IOException e) {
			LOGGER.severe("Could not save file " + file.getAbsolutePath());
		}
	}

	private String createTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("[table]");

		Collection<SortableColumn> cols = new ColumnCreator<>().createColumns().stream()
				.filter(SortableColumn::isPrintable).sorted().collect(Collectors.toList());
		sb.append(createTableHeader(getTitles(cols)));
		Collection<String> fields = getFields(cols);
		Map<String, PropertyDescriptor> fieldToProperty = getFieldToProperty();

		new ArrayList<>(existingPlayers).stream().sorted()
				.forEach(p -> sb.append(createTableRow(p, fields, fieldToProperty)));
		sb.append("[/table]");
		String table = sb.toString();
		return table;
	}

	private Map<String, PropertyDescriptor> getFieldToProperty() {
		Map<String, PropertyDescriptor> fieldToProperty = new HashMap<>();

		try {
			for (PropertyDescriptor descr : Introspector.getBeanInfo(Player.class).getPropertyDescriptors()) {
				fieldToProperty.put(descr.getName(), descr);
			}
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fieldToProperty;
	}

	private Collection<String> getTitles(Collection<SortableColumn> cols) {
		return cols.stream().map(SortableColumn::getColumn).map(TableColumn::getText).collect(Collectors.toList());
	}

	private Collection<String> getFields(Collection<SortableColumn> cols) {
		return cols.stream().map(SortableColumn::getField).collect(Collectors.toList());
	}

	private String createTableHeader(Collection<String> titles) {
		return "[tr]" + titles.stream().map(t -> tableData(bold(t))).collect(Collectors.joining())
				+ tableData(bold("Jackpot in Euro: "
						+ String.valueOf(existingPlayers.stream().mapToDouble(Player::getMoney).sum())))
				+ "Anzahl der Turniere: " + games.size() + "[/tr]";
	}

	private String tableData(String string) {
		return "[td]" + string + "[/td]";
	}

	private String bold(String string) {
		return "[b]" + string + "[/b]";
	}

	private String createTableRow(Player p, Collection<String> fields,
			Map<String, PropertyDescriptor> fieldToProperty) {
		boolean underscore = false;

		Collection<String> values = new ArrayList<>();

		for (String field : fields) {
			Object value = getValue(p, field, fieldToProperty);

			if (field.equals(Player.FIELD_POSITION)) {
				int position = (int) value;
				underscore = position == 12;
			}
			if (underscore) {
				value = underScore(String.valueOf(value));
			}
			values.add(String.valueOf(value));
		}

		String row = "[tr][td]" + Joiner.on("[/td][td]").join(values) + "[/td][/tr]";
		return row;
	}

	private Object getValue(Player p, String field, Map<String, PropertyDescriptor> fieldToProperty) {
		Object value = null;
		try {
			value = fieldToProperty.get(field).getReadMethod().invoke(p);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	private static String underScore(String row) {
		return "[u]" + row + "[/u]";
	}

}