package de.torsten.kickertool.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.torsten.kickertool.controller.GameController;
import de.torsten.kickertool.controller.handler.FirstGamePlayersImporter;
import de.torsten.kickertool.controller.handler.ImportDypHandler;
import de.torsten.kickertool.controller.handler.PrintHandler;
import de.torsten.kickertool.controller.handler.SaveHandler;
import de.torsten.kickertool.model.Game;
import de.torsten.kickertool.model.Player;
import de.torsten.kickertool.view.column.ColumnCreator;
import de.torsten.kickertool.view.column.SortableColumn;
import de.torsten.kickertool.vm.GameTreeItem;
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
 * 
 * copyright (c) energy & meteo systems GmbH, 2016
 * 
 * mail@energymeteo.com www.energymeteo.com
 * 
 ************************************************/

public final class ViewCreator implements de.torsten.kickertool.view.TabPane<Game> {
	private TreeView<GenericTreeItem<?>> tree;
	private TabPane tabPane;
	private final ObservableList<Player> existingPlayers;
	private EventHandler<ActionEvent> importDypHandler;
	private final Collection<Game> games;
	private final Preferences preferences;

	public ViewCreator(TreeView<GenericTreeItem<?>> tree, ObservableList<Player> existingPlayers,
			Collection<Game> games, Preferences preferences) {
		this.tree = tree;
		this.existingPlayers = existingPlayers;
		this.games = games;
		this.preferences = preferences;
	}

	@Override
	public void addTab(Game importGame) {
		tabPane.getTabs().add(createGameTab(importGame));
		games.add(importGame);
	}

	public void createView(Stage stage) {
		stage.setTitle("DYP");

		Preferences userPrefs = preferences;
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

	public Tab createGameTab(Game game) {
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

		Collection<SortableColumn> simpleColumns = new ColumnCreator<>().createColumns();

		TableView<Player> table = new TableView<>();
		table.setEditable(true);
		table.getColumns().addAll(simpleColumns.stream().map(SortableColumn::getColumn).collect(Collectors.toList()));

		Collection<TableColumn<Player, ?>> rankSortedColumns = simpleColumns.stream()
				.filter(c -> c.getRankSortPosition() != -1)
				.sorted((c1, c2) -> Integer.compare(c1.getRankSortPosition(), c2.getRankSortPosition()))
				.map(SortableColumn::getColumn).collect(Collectors.toList());
		table.getSortOrder().addAll(rankSortedColumns);

		table.setItems(players);
		table.sort();
		return table;
	}

	private TableColumn<Player, Number> createSyntheticPointsColumn(Function<Player, ObservableValue<Number>> function,
			BiConsumer<Player, Integer> biConsumer, String text) {
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

	public TreeItem<GenericTreeItem<?>> createTreeRoot() {
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
		items.add(
				createMenuItem("Erste Spielerpunkte importieren", new FirstGamePlayersImporter(existingPlayers, this)));
		items.add(createMenuItem("Speichern", new SaveHandler(existingPlayers, games)));
		items.add(createMenuItem("Als Dynamic-Board-Tabelle exportieren",
				new PrintHandler(stage, existingPlayers, games)));
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

	public void setImportDypHandler(ImportDypHandler importDypHandler) {
		this.importDypHandler = importDypHandler;
	}

}