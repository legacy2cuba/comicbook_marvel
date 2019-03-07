package com.xformix.comicbook.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.haulmont.cuba.core.sys.AppContext;
import com.itextpdf.text.log.SysoCounter;
import com.xformix.apitools.service.APIServiceBean;
import com.xformix.comicbook.entity.MarvelCharacter;
import com.xformix.comicbook.entity.MarvelComic;
import com.xformix.comicbook.entity.MarvelSeries;

@Service(MarvelService.NAME)
public class MarvelServiceBean extends APIServiceBean implements MarvelService {

	private static Logger logger = Logger.getLogger(MarvelServiceBean.class);

	/**
	 * These keys are specified in app.properties
	 */
	private static String publicKey = AppContext.getProperty("marvelapi.publicKey");
	private static String privateKey = AppContext.getProperty("marvelapi.privateKey");

	private static String ts = "1";
	private static String hash = null;

	@Override
	protected String getBaseURL(String type, Integer id) {
		String url = "https://gateway.marvel.com/v1/public/" + type;
		if (id >= 0) {
			url += "/" + id;
		}
		url += "?ts=" + ts + "&apikey=" + publicKey + "&hash=" + getHash();
		url += "&limit=50";
		url += "&offset=0";
		return url;
	}

	private String getHash() {
		if (hash == null) {
			hash = getHash(ts + privateKey + publicKey);
		}
		return hash;
	}

	@Override
	public List<MarvelCharacter> getCharacters(String startsWith) {
		return getResults("characters", "nameStartsWith=" + encode(startsWith), MarvelCharacter.class);
	}

	@Override
	public List<MarvelSeries> getSeries(String startsWith) {
		return getResults("series", "titleStartsWith=" + encode(startsWith), MarvelSeries.class);
	}

	@Override
	public List<MarvelComic> getComics(String startsWith) {
		return getResults("comics", "format=comic&noVariants=true&titleStartsWith=" + encode(startsWith),
				MarvelComic.class);
	}

	@Override
	public List<MarvelComic> getComicsBySeriesId(Integer series) {
		return getResults("comics", "format=comic&noVariants=true&series=" + series, MarvelComic.class);
	}

	@Override
	public MarvelComic getComicById(Integer comicId) {
		List<MarvelComic> results = getResults("comics", "format=comic&noVariants=true", comicId, MarvelComic.class);
		if (results == null || results.size() == 0)
			return null;
		return results.get(0);
	}

}