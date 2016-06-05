package de.torsten.kickertool.controller.handler;

import java.io.File;
import java.util.List;

import de.torsten.kickertool.controller.GameController;
import de.torsten.kickertool.controller.TreeOwner;
import de.torsten.kickertool.model.Game;
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
public final class ImportDypHandler implements EventHandler<ActionEvent> {
	private final Window stage;
	private final de.torsten.kickertool.view.TabPane<Game> tabPane;
	private final TreeOwner owner;

	public ImportDypHandler(Window stage, de.torsten.kickertool.view.TabPane<Game> tabPane, TreeOwner owner) {
		this.stage = stage;
		this.tabPane = tabPane;
		this.owner = owner;
	}

	@Override
	public void handle(ActionEvent t) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("ktool-Dateien auswählen");
		List<File> file = fileChooser.showOpenMultipleDialog(stage);
		if (file != null) {
			GameController gameController = new GameController();
			file.stream().map(f -> gameController.importGame(f)).filter(g -> g != null).forEach(g -> tabPane.addTab(g));
			owner.refreshRoot();
		}
	}
}