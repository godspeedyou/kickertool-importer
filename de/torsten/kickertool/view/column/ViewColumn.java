/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

************************************************/

package de.torsten.kickertool.view.column;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ViewColumn {
	String title();

	int columnPosition();

	int rankSortPosition() default -1;

	ColumnType type() default ColumnType.SIMPLE;

	boolean isPrintable() default false;
}
