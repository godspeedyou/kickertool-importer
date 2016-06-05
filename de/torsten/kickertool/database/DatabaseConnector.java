/************************************************

copyright (c) energy & meteo systems GmbH, 2014

mail@energymeteo.com
www.energymeteo.com

 ************************************************/

package de.torsten.kickertool.database;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.torsten.database.SessionFactory;
import de.torsten.kickertool.model.Game;
import de.torsten.kickertool.model.Player;
import de.torsten.kickertool.model.Team;

public class DatabaseConnector {
	private static final String LOG_SAVING = "Saving"; //$NON-NLS-1$
	private static final String LOG_LOADING = "Loading"; //$NON-NLS-1$
	private static final String QUERY_SELECT_PLAYERS_BY_NAME = "select h from %s as h where name like :name"; //$NON-NLS-1$
	private final static Logger LOGGER = Logger.getLogger(DatabaseConnector.class.getSimpleName());
	private final static String PLAYER_CLASS_NAME = Player.class.getSimpleName();

	/*
	 * (nicht-Javadoc)
	 * 
	 * @see de.torsten.parser.database.PlaylistRepository#load(java.lang.String)
	 */
	public Player load(String name) {
		logPlayerAccess(LOG_LOADING, name);
		Session session = SessionFactory.getInstance().getCurrentSession();
		try {
			// TODO zu SessionFactory auslagern
			session.beginTransaction();

			// nur ein ergebnis erwarten, wenn es doch mehrere sind, sonst fehler
			Player player = (Player) session.createQuery(String.format(QUERY_SELECT_PLAYERS_BY_NAME, PLAYER_CLASS_NAME))
					.setParameter(Player.FIELD_NAME, name).uniqueResult();
			resolveReferences(player);
			return player;
		} catch (HibernateException e) {
			LOGGER.log(Level.WARNING, String.format("Error retreiving %s %s from db", PLAYER_CLASS_NAME, name), e); //$NON-NLS-1$
		} finally {
			session.close();
		}
		return null;
	}

	public Collection<Player> loadAllPlayers() {
		LOGGER.info("Loading all players"); //$NON-NLS-1$
		Session session = SessionFactory.getInstance().getCurrentSession();
		try {
			// TODO zu SessionFactory auslagern
			session.beginTransaction();

			// nur ein ergebnis erwarten, wenn es doch mehrere sind, sonst fehler
			List<Player> players = session.createQuery("from Player").list();
			return players;
		} catch (HibernateException e) {
			LOGGER.log(Level.WARNING, String.format("Error retreiving all %s from db", PLAYER_CLASS_NAME), e); //$NON-NLS-1$
		} finally {
			session.close();
		}
		return null;
	}

	public Collection<Game> loadAllGames() {
		LOGGER.info("Loading all games"); //$NON-NLS-1$
		Session session = SessionFactory.getInstance().getCurrentSession();
		try {
			// TODO zu SessionFactory auslagern
			session.beginTransaction();

			// nur ein ergebnis erwarten, wenn es doch mehrere sind, sonst fehler
			Collection<Game> games = session.createQuery("from Game").list(); //$NON-NLS-1$
			games.forEach(g -> g.getPlayers().size());
			games.forEach(g -> g.getPlays().forEach(p -> p.getDisciplines().forEach(d -> d.getSets().size())));
			games.forEach(g -> g.getTeams().forEach(t -> t.getPlayers().size()));
			games.forEach(g -> g.getPlayerPoints().forEach(pp -> pp.getPlayer().toString()));
			return games;
		} catch (HibernateException e) {
			LOGGER.log(Level.WARNING, String.format("Error retreiving all %s from db", PLAYER_CLASS_NAME), e); //$NON-NLS-1$
		} finally {
			session.close();
		}
		return null;
	}

	private static void logPlayerAccess(String action, String title) {
		LOGGER.info(String.format("%s %s from database.", action, title)); //$NON-NLS-1$
	}

	private static void resolveReferences(Player playlist) {
		// if (playlist == null) {
		// return;
		// }
		// for (Video video : playlist.getVideos()) {
		// video.getImages().size();
		// }
	}

	/*
	 * (nicht-Javadoc)
	 * 
	 * @see de.torsten.parser.database.PlaylistRepository#save(de.torsten.parser.model.youtube.Playlist)
	 */
	public void save(Player player) {
		logPlayerAccess(LOG_SAVING, player.getName());
		Session session = SessionFactory.getInstance().getCurrentSession();
		try {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(player);
			session.flush();
			tx.commit();
		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}
	}

	public void save(Game game) {
		logPlayerAccess(LOG_SAVING, game.getName());
		Session session = SessionFactory.getInstance().getCurrentSession();
		try {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(game);
			session.flush();
			tx.commit();

		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}
	}

	public void saveTeam(Team team) {
		logPlayerAccess(LOG_SAVING, team.getId());
		Session session = SessionFactory.getInstance().getCurrentSession();
		try {
			Transaction tx = session.beginTransaction();
			session.saveOrUpdate(team);
			session.flush();
			tx.commit();

		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}
	}

	// @Override
	// public void close() {
	// /*
	// * nichts zu tun
	// */
	// }
}
