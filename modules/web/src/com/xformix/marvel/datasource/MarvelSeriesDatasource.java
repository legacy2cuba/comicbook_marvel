package com.xformix.marvel.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import com.xformix.comicbook.entity.MarvelCharacter;
import com.xformix.comicbook.entity.MarvelSeries;
import com.xformix.comicbook.service.MarvelService;

public class MarvelSeriesDatasource extends CustomCollectionDatasource<MarvelSeries, UUID> {

	private MarvelService marvelService = AppBeans.get(MarvelService.NAME);

	@Override
	protected Collection<MarvelSeries> getEntities(Map params) {
		String startsWith = (String) params.get("startsWith");
		if (startsWith == null || startsWith.equals(""))
			return new ArrayList<>();
		return marvelService.getSeries(startsWith);
	}

}
