package com.xformix.comicbook.service;

import java.util.List;
import java.util.Set;

import com.xformix.comicbook.entity.MarvelCharacter;
import com.xformix.comicbook.entity.MarvelComic;
import com.xformix.comicbook.entity.MarvelSeries;

public interface MarvelService {
    String NAME = "comicbook_MarvelService";

    List<MarvelCharacter> getCharacters(String startsWith);

    List<MarvelSeries> getSeries(String startsWith);

    List<MarvelComic> getComics(String startsWith);

    List<MarvelComic> getComicsBySeriesId(Integer seriesId);

    MarvelComic getComicById(Integer comicId);
    
}