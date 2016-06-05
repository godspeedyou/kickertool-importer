/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Discipline {

	/**
	 * Hinweis: Eigentlich hat eine {@link Discipline} auch eine String id, die hier als gegebene Id verwendet werden
	 * könnte.
	 */
	private int identifier;

	private Collection<Set> sets;

	public Discipline() {
	}

	public Discipline(de.torsten.kickertool.model.gson.Discipline discipline) {
		this.sets = createSets(discipline.getSets());
	}

	private Collection<Set> createSets(Collection<de.torsten.kickertool.model.gson.Set> gsonSets) {
		Collection<Set> newSets = new ArrayList<>();
		for (de.torsten.kickertool.model.gson.Set set : gsonSets) {
			newSets.add(new Set(set));
		}
		return newSets;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getIdentifier() {
		return identifier;
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	@OneToMany(cascade = javax.persistence.CascadeType.ALL)
	public Collection<Set> getSets() {
		return sets;
	}

	public void setSets(Collection<Set> sets) {
		this.sets = sets;
	}

}
