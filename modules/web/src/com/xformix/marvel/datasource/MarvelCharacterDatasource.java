package com.xformix.marvel.datasource;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.data.impl.CustomCollectionDatasource;
import com.xformix.comicbook.entity.MarvelCharacter;
import com.xformix.comicbook.service.MarvelService;

public class MarvelCharacterDatasource extends CustomCollectionDatasource<MarvelCharacter, UUID> {

    private MarvelService marvelService = AppBeans.get(MarvelService.NAME);

    @Override
    protected Collection<MarvelCharacter> getEntities(Map params) {
	String startsWith = (String) params.get("startsWith");
	return marvelService.getCharacters(startsWith);
    }

}
