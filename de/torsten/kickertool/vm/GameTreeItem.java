/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.vm;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.torsten.kickertool.model.Game;
import de.torsten.kickertool.model.Play;
import de.torsten.kickertool.view.GenericTreeItem;
import de.torsten.kickertool.vm.GameTreeItem.AttributeTreeItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GameTreeItem implements GenericTreeItem<AttributeTreeItem<?, ?>> {

	public static final class AttributeTreeItem<T, U extends GenericTreeItem<?>> implements GenericTreeItem<U> {
		private final String name;
		private final Function<T, U> mapper;
		private final Collection<T> collection;

		public AttributeTreeItem(String name, Function<T, U> mapper, Collection<T> collection) {
			this.name = name;
			this.mapper = mapper;
			this.collection = collection;
		}

		@Override
		public ObservableList<U> getChildren() {
			return FXCollections
					.observableArrayList(collection.stream().map(mapper::apply).collect(Collectors.toList()));
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return getName();
		}
	}

	private final Game game;

	public GameTreeItem(Game game) {
		this.game = game;
	}

	@SuppressWarnings("nls")
	@Override
	public ObservableList<AttributeTreeItem<?, ?>> getChildren() {
		ObservableList<AttributeTreeItem<?, ?>> list = FXCollections.observableArrayList();
		list.add(new AttributeTreeItem<>("Plays", PlayTreeItem::new, game.getPlays()));
		list.add(new AttributeTreeItem<>("Teams", TeamTreeItem::new, game.getTeams()));
		list.add(new AttributeTreeItem<>("Players", PlayerTreeItem::new, game.getPlayers()));

		Set<Entry<Integer, List<Play>>> entrySets = game.getPlays().stream()
				.collect(Collectors.groupingBy(p -> p.getRound())).entrySet();

		list.add(new AttributeTreeItem<Entry<Integer, List<Play>>, RoundTreeItem>("Rounds",
				e -> new RoundTreeItem(e.getKey(), e.getValue().stream().flatMap(p -> p.getDisciplines().stream())
						.flatMap(d -> d.getSets().stream()).collect(Collectors.toList())),
				entrySets));
		return list;
	}

	@Override
	public String getName() {
		return game.getName();
	}

	@Override
	public String toString() {
		return getName();
	}

}
