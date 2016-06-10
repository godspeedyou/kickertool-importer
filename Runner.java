import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.torsten.kickertool.controller.GameController;
import de.torsten.kickertool.controller.TreeOwner;
import de.torsten.kickertool.controller.handler.ImportDypHandler;
import de.torsten.kickertool.database.DatabaseConnector;
import de.torsten.kickertool.model.Game;
import de.torsten.kickertool.model.Player;
import de.torsten.kickertool.view.GenericTreeItem;
import de.torsten.kickertool.view.ViewCreator;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

/************************************************
 * copyright (c) energy & meteo systems GmbH, 2016
 * 
 * mail@energymeteo.com www.energymeteo.com
 ************************************************/

@SuppressWarnings("nls")
public class Runner extends Application implements de.torsten.kickertool.view.TabPane<Game>, TreeOwner {
	static final Logger LOGGER = Logger.getLogger(Runner.class.getSimpleName());
	private ObservableList<Player> existingPlayers;
	private Collection<Game> games = new HashSet<>();
	private TreeView<GenericTreeItem<?>> tree;
	private Stage primaryStage;
	private TabPane tabPane;
	private ViewCreator creator;

	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, IOException {
		launch(args);
	}

	@Override
	public void addTab(Game importGame) {
		tabPane.getTabs().add(creator.createGameTab(importGame));
		games.add(importGame);
	}

	@Override
	public void start(Stage stage) throws Exception {

		games = FXCollections.observableArrayList(loadAllGames());
		games.forEach(Game::init);
		GameController gameController = new GameController();
		games.forEach(gameController::calculatePoints);

		Collection<Player> loadAllPlayers = loadAllPlayers();
		existingPlayers = FXCollections.observableArrayList(loadAllPlayers);
		this.primaryStage = stage;
		creator = new ViewCreator(tree, tabPane, existingPlayers, games, new ImportDypHandler(stage, this, this),
				getUserPrefs());
		creator.createView(stage);
	}

	private Collection<Player> loadAllPlayers() {
		return new DatabaseConnector().loadAllPlayers();
	}

	private Collection<Game> loadAllGames() {
		return new DatabaseConnector().loadAllGames();
	}

	@Override
	public void stop() {
		Preferences userPrefs = getUserPrefs();
		userPrefs.putDouble("stage.x", primaryStage.getX());
		userPrefs.putDouble("stage.y", primaryStage.getY());
		userPrefs.putDouble("stage.width", primaryStage.getWidth());
		userPrefs.putDouble("stage.height", primaryStage.getHeight());
	}

	private Preferences getUserPrefs() {
		return Preferences.userNodeForPackage(getClass());
	}

	@Override
	public void refreshRoot() {
		tree.setRoot(creator.createTreeRoot());
	}

}
