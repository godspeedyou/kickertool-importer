package de.torsten.kickertool.controller.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;

import de.torsten.kickertool.model.Player;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

	public PrintHandler(Window window, ObservableList<Player> existingPlayers) {
		this.stage = window;
		this.existingPlayers = existingPlayers;
	}

	@Override
	public void handle(ActionEvent event) {
		File file = new FileChooser().showOpenDialog(stage);
		if (file == null) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("[table]");
		Supplier<Integer> counter = new Supplier<Integer>() {
			private int i = 1;

			@Override
			public Integer get() {
				return i++;
			}
		};
		sb.append(
				"[tr][td][b]Platz[/b][/td][td][b]Spieler[/b][/td][td][b]Punkte[/b][/td][td][b]Anzahl Spiele[/b][/td][td][b]Punkte innerhalb der Spiele[/b][/td][td][b]Preisgeld in Euro (gesamt "
						+ existingPlayers.stream().mapToDouble(Player::getMoney).sum() + " Euro)[/b][/td][/tr]");
		new ArrayList<>(existingPlayers).stream().sorted().forEach(p -> sb.append(createTableRow(p, counter.get())));
		sb.append("[/table]");

		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
			writer.write(sb.toString());
		} catch (IOException e) {
			LOGGER.severe("Could not save file " + file.getAbsolutePath());
		}
	}

	private String createTableRow(Player p, Integer place) {

		List<String> cells = new ArrayList<>(Arrays.asList(p.getPosition(), p.getName(), p.getPoints(), p.getGames(),
				p.getInGamePoints(), p.getMoney())).stream().map(String::valueOf)
						.map(p.getPosition() == 12 ? PrintHandler::underScore : pl -> pl).collect(Collectors.toList());

		String row = "[tr][td]" + Joiner.on("[/td][td]").join(cells) + "[/td][/tr]";
		return row;
	}

	private static String underScore(String row) {
		return "[u]" + row + "[/u]";
	}

}