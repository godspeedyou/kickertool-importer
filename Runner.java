import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import de.torsten.kickertool.controller.GameController;
import de.torsten.kickertool.controller.TreeOwner;
import de.torsten.kickertool.controller.handler.FirstGamePlayersImporter;
import de.torsten.kickertool.controller.handler.ImportDypHandler;
import de.torsten.kickertool.controller.handler.PrintHandler;
import de.torsten.kickertool.controller.handler.SaveHandler;
import de.torsten.kickertool.database.DatabaseConnector;
import de.torsten.kickertool.model.Game;
import de.torsten.kickertool.model.Player;
import de.torsten.kickertool.view.GenericTreeItem;
import de.torsten.kickertool.view.NumberTextField;
import de.torsten.kickertool.vm.GameTreeItem;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;

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
		// TODO hier oder woanders muss noch mit den existierenden Playern (aus der DB) gesynct werden. Sync wird
		// momentan nur innerhalb eines Games gemacht.
		// TODO am besten erst die zwei Rohlisten (Players, Teams) eines Games syncen, dann kann das Game, wie bisher,
		// sich selbst syncen,
		// ==> geht nicht, weil Namen erst korrigiert werden müssen
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
		creator = new ViewCreator(tree, tabPane, existingPlayers, new ImportDypHandler(stage, this, this));
		creator.createView(stage);
	}

	private Collection<Player> loadAllPlayers() {
		return new DatabaseConnector().loadAllPlayers();
	}

	private Collection<Game> loadAllGames() {
		return new DatabaseConnector().loadAllGames();
	}

	public static final class ViewCreator {
		private TreeView<GenericTreeItem<?>> tree;
		private TabPane tabPane;
		private final ObservableList<Player> existingPlayers;
		private final Collection<Game> games = new HashSet<>();
		private final EventHandler<ActionEvent> importDypHandler;

		private ViewCreator(TreeView<GenericTreeItem<?>> tree, TabPane tabPane, ObservableList<Player> existingPlayers,
				EventHandler<ActionEvent> importDypHandler) {
			this.tree = tree;
			this.tabPane = tabPane;
			this.existingPlayers = existingPlayers;
			this.importDypHandler = importDypHandler;
		}

		public void createView(Stage stage) {
			stage.setTitle("DYP");

			Preferences userPrefs = Preferences.userNodeForPackage(getClass());
			// get window location from user preferences: use x=100, y=100, width=400, height=400 as default
			double x = userPrefs.getDouble("stage.x", 100);
			double y = userPrefs.getDouble("stage.y", 100);
			double w = userPrefs.getDouble("stage.width", 400);
			double h = userPrefs.getDouble("stage.height", 400);

			stage.setX(x);
			stage.setY(y);
			stage.setWidth(w);
			stage.setHeight(h);

			Scene scene = createScene(stage);
			stage.setScene(scene);
			stage.show();
		}

		private Scene createScene(Stage stage) {

			TableView<Player> table = createTable(existingPlayers);
			final VBox vbox = createVbox(table);
			HBox hb = createAddBox();
			vbox.getChildren().addAll(hb);

			tabPane = new TabPane();
			tabPane.getTabs().add(createTab(vbox, "Tabelle"));
			for (Game game : games) {
				tabPane.getTabs().add(createGameTab(game));
			}
			tabPane.getTabs().add(createTab(createAllGamesTree(), "Alle Spiele"));

			MenuBar menuBar = createMenuBar(stage);
			BorderPane bp = new BorderPane();
			bp.setTop(menuBar);
			bp.setCenter(tabPane);
			Scene scene = new Scene(bp);
			return scene;
		}

		private static VBox createVbox(Node node) {
			final VBox vbox = new VBox();
			vbox.setSpacing(5);
			vbox.setPadding(new Insets(10, 0, 0, 10));
			VBox.setVgrow(node, Priority.ALWAYS);

			vbox.getChildren().addAll(node);
			return vbox;
		}

		private HBox createAddBox() {
			HBox hb = new HBox();
			TextField nameField = createTextField("Name");
			NumberTextField pointsField = createNumberTextField("Punkte");
			final Button addButton = new Button("Hinzufügen");
			addButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					existingPlayers.add(new Player(nameField.getText(), pointsField.getNumber().intValue(), 1));
					nameField.clear();
					pointsField.clear();
				}
			});
			hb.getChildren().addAll(nameField, pointsField, addButton);
			return hb;
		}

		private Tab createTab(final VBox vbox, String name) {
			Tab tab = new Tab();
			tab.setText(name);
			tab.setContent(vbox);
			return tab;
		}

		private Tab createGameTab(Game game) {
			TableView<Player> table = createTable(FXCollections.observableArrayList(game.getPlayers()));
			// TODO ingame muss gar nicht manipulierbar sein
			TableColumn<Player, Number> inGamePointsCol = createSyntheticPointsColumn(
					p -> new SimpleIntegerProperty(game.getInGamePoints(p)), (pl, po) -> game.setInGamePoints(pl, po),
					"Spielpunkte");
			table.getColumns().add(inGamePointsCol);
			table.getSortOrder().clear();
			table.getSortOrder().add(inGamePointsCol);
			table.sort();

			TableColumn<Player, Number> dypGamePointsCol = createSyntheticPointsColumn(
					p -> new SimpleIntegerProperty(game.getDypPoints(p)), (pl, po) -> game.setDypPoints(pl, po),
					"Dyp-Punkte");
			table.getColumns().add(dypGamePointsCol);

			VBox box = createVbox(table);
			Button button = new Button("Punkte übernehmen");
			button.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					new GameController().syncPlayers(existingPlayers, game);
				}
			});
			box.getChildren().add(button);
			return createTab(box, game.getName() + ": " + game.getCreated());
		}

		private TextField createTextField(String text) {
			final TextField field = new TextField();
			field.setPromptText(text);
			return field;
		}

		private NumberTextField createNumberTextField(String text) {
			final NumberTextField field = new NumberTextField();
			field.setPromptText(text);
			return field;
		}

		private TableView<Player> createTable(ObservableList<Player> players) {

			TableColumn<Player, String> nameCol = createNameColumn();

			TableColumn<Player, Number> pointsCol = createPointsColumn();

			TableColumn<Player, Number> inGamepointsCol = createSimpleColumn(Player.TITLE_IN_GAME_POINTS,
					Player.FIELD_IN_GAME_POINTS);
			TableColumn<Player, Number> gamesCol = createSimpleColumn(Player.TITLE_GAMES, Player.FIELD_GAMES);
			TableColumn<Player, Number> positionCol = createSimpleColumn("#", Player.FIELD_POSITION);
			TableColumn<Player, Number> moneyCol = createSimpleColumn("Geldgewinn", Player.FIELD_MONEY);

			TableView<Player> table = new TableView<>();
			TableColumn<Player, Boolean> payerCol = new TableColumn<>("Monatsbeitragzahler");
			payerCol.setCellValueFactory(new PropertyValueFactory<>(Player.FIELD_MONTHLY_PAYER));
			payerCol.setCellFactory(CheckBoxTableCell.forTableColumn(payerCol));

			table.setEditable(true);
			table.getColumns().addAll(
					Arrays.asList(positionCol, nameCol, pointsCol, gamesCol, payerCol, moneyCol, inGamepointsCol));
			table.getSortOrder().add(pointsCol);
			table.getSortOrder().add(gamesCol);
			table.getSortOrder().add(inGamepointsCol);

			table.setItems(players);
			table.sort();
			return table;
		}

		private TableColumn<Player, Number> createSimpleColumn(String text, String field) {
			TableColumn<Player, Number> moneyCol = new TableColumn<>(text);
			moneyCol.setCellValueFactory(new PropertyValueFactory<>(field));
			return moneyCol;
		}

		private TableColumn<Player, String> createNameColumn() {
			TableColumn<Player, String> nameCol = new TableColumn<>("Name");
			nameCol.setCellValueFactory(new PropertyValueFactory<>(Player.FIELD_NAME));
			nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
			nameCol.setOnEditCommit(new EventHandler<CellEditEvent<Player, String>>() {
				@Override
				public void handle(CellEditEvent<Player, String> t) {
					ObservableList<Player> rows = t.getTableView().getItems();
					Player selected = rows.get(t.getTablePosition().getRow());
					selected.setName(t.getNewValue());
				}
			});
			return nameCol;
		}

		private TableColumn<Player, Number> createPointsColumn() {
			TableColumn<Player, Number> pointsCol = new TableColumn<>("Punkte");
			pointsCol.setSortType(TableColumn.SortType.ASCENDING);
			pointsCol.setCellValueFactory(new PropertyValueFactory<>(Player.FIELD_POINTS));

			pointsCol.setCellFactory(TextFieldTableCell.<Player, Number> forTableColumn(new NumberStringConverter()));
			pointsCol.setOnEditCommit(new EventHandler<CellEditEvent<Player, Number>>() {
				@Override
				public void handle(CellEditEvent<Player, Number> t) {
					ObservableList<Player> rows = t.getTableView().getItems();
					Player selected = rows.get(t.getTablePosition().getRow());
					selected.setPoints(t.getNewValue().intValue());
				}
			});

			return pointsCol;
		}

		private TableColumn<Player, Number> createSyntheticPointsColumn(
				Function<Player, ObservableValue<Number>> function, BiConsumer<Player, Integer> biConsumer,
				String text) {
			TableColumn<Player, Number> pointsCol = new TableColumn<>(text);
			pointsCol.setSortType(TableColumn.SortType.DESCENDING);
			pointsCol.setCellValueFactory(new Callback<CellDataFeatures<Player, Number>, ObservableValue<Number>>() {

				@Override
				public ObservableValue<Number> call(CellDataFeatures<Player, Number> param) {
					return function.apply(param.getValue());
				}

			});

			pointsCol.setCellFactory(TextFieldTableCell.<Player, Number> forTableColumn(new NumberStringConverter()));
			pointsCol.setOnEditCommit(new EventHandler<CellEditEvent<Player, Number>>() {
				@Override
				public void handle(CellEditEvent<Player, Number> t) {
					ObservableList<Player> rows = t.getTableView().getItems();
					Player selected = rows.get(t.getTablePosition().getRow());
					biConsumer.accept(selected, t.getNewValue().intValue());
				}
			});

			return pointsCol;
		}

		private TreeItem<GenericTreeItem<?>> createTreeRoot() {
			GenericTreeItem<?> rootItem = createRootItem();
			TreeItem<GenericTreeItem<?>> treeRoot = createItem(rootItem);
			return treeRoot;
		}

		private GenericTreeItem<?> createRootItem() {
			GenericTreeItem<?> rootItem = new GenericTreeItem<GameTreeItem>() {

				@Override
				public ObservableList<GameTreeItem> getChildren() {
					return FXCollections
							.observableArrayList(games.stream().map(GameTreeItem::new).collect(Collectors.toList()));
				}

				@Override
				public String getName() {
					return "Alle Spiele";
				}

				@Override
				public String toString() {
					return getName();
				}
			};
			return rootItem;
		}

		private TreeItem<GenericTreeItem<?>> createItem(GenericTreeItem<?> object) {
			TreeItem<GenericTreeItem<?>> item = new TreeItem<>(object);
			item.setExpanded(true);
			item.getChildren().addAll(((Stream<? extends GenericTreeItem<?>>) object.getChildren().stream())
					.map(this::createItem).collect(Collectors.toList()));
			return item;
		}

		private VBox createAllGamesTree() {
			TreeItem<GenericTreeItem<?>> treeRoot = createTreeRoot();
			tree = new TreeView<>(treeRoot);
			tree.setShowRoot(true);
			StackPane root = new StackPane();
			root.getChildren().add(tree);
			return ViewCreator.createVbox(tree);
		}

		private Menu createMenuFile(Stage stage) {
			Menu menuFile = new Menu("Datei");
			menuFile.getItems().addAll(createMenuItems(stage));

			return menuFile;

		}

		private Collection<MenuItem> createMenuItems(Stage stage) {
			Collection<MenuItem> items = new ArrayList<>();
			items.add(createMenuItem("DYP importieren", importDypHandler));
			items.add(createMenuItem("Erste Spielerpunkte importieren", new FirstGamePlayersImporter(existingPlayers)));
			items.add(createMenuItem("Speichern", new SaveHandler(existingPlayers, games)));
			items.add(
					createMenuItem("Als Dynamic-Board-Tabelle exportieren", new PrintHandler(stage, existingPlayers)));
			items.add(createMenuItem("Alle geöffneten Spielpunkte übernehmen",
					event -> games.stream().forEach(g -> new GameController().syncPlayers(existingPlayers, g))));
			return items;
		}

		private MenuItem createMenuItem(String text, EventHandler<ActionEvent> handler) {
			MenuItem importDyp = new MenuItem(text);
			importDyp.setOnAction(handler);
			return importDyp;
		}

		private MenuBar createMenuBar(Stage stage) {
			MenuBar menuBar = new MenuBar();

			Menu menuFile = createMenuFile(stage);
			menuBar.getMenus().addAll(menuFile);

			return menuBar;
		}

	}

	@Override
	public void stop() {
		Preferences userPrefs = Preferences.userNodeForPackage(getClass());
		userPrefs.putDouble("stage.x", primaryStage.getX());
		userPrefs.putDouble("stage.y", primaryStage.getY());
		userPrefs.putDouble("stage.width", primaryStage.getWidth());
		userPrefs.putDouble("stage.height", primaryStage.getHeight());
	}

	@Override
	public void refreshRoot() {
		tree.setRoot(creator.createTreeRoot());
	}

}
