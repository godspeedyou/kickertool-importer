/************************************************

copyright (c) energy & meteo systems GmbH, 2016

mail@energymeteo.com
www.energymeteo.com

 ************************************************/

package de.torsten.kickertool.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Importer {

	public de.torsten.kickertool.model.gson.Game importGame(String filePath)
			throws JsonSyntaxException, JsonIOException, IOException {
		Gson gson = new GsonBuilder().create();
		try (BufferedReader reader = getReader(filePath)) {
			de.torsten.kickertool.model.gson.Game gsonGame = gson.fromJson(reader,
					de.torsten.kickertool.model.gson.Game.class);
			return gsonGame;
		}
	}

	private BufferedReader getReader(String filePath) throws IOException {
		Path path = FileSystems.getDefault().getPath(filePath);
		BufferedReader reader = Files.newBufferedReader(path, Charset.defaultCharset());
		return reader;
	}

}
