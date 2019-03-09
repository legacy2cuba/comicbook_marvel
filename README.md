# comicbook_marvel
Comicbook app which accesses Marvel API. 
Demonstrates use of the com.xformix.apitools library for accessing services.

Intro
=====
An example of an application which was initially an MS Access application 
Thank you Bob Beltrami!
http://bobbeltrami.com/comics.htm#cbcd

It was migrated automatically using a proprietary tool created by Transformix to migrate MS Access applications to CUBA applications.

If you would like Transformix to give you an estimate for migrating your MS Access application to the CUBA platform please 
email sales@xformix.com.

Enhancement
===========
The CUBA application was then enhanced manually by connecting to the Marvel API to grab information on Marvel comics.

You should obtain your own developer key (valid for both map and search apis) from https://developer.marvel.com/ and replace 
the "marvelapi.publicKey" and "marvelapi.privateKey" properties specified in modules/core/src/com/xformix/comicbook/app.properties.

The first step in using the apitools library is to look at the API documentation and define entities which will be populated by calls to the web service.

For example, the com.xformix.comicbook.entity.MarvelComic class was defined after referring to: 
https://developer.marvel.com/docs#!/public/getComicsCollection_get_6

```java
public class MarvelComic extends BaseUuidEntity {
    private static final long serialVersionUID = 6471978615339841615L;

    @MetaProperty
    protected String title;

    @JSONPath(path="id")
    @MetaProperty
    protected Integer marvelid;

    @MetaProperty
    private Integer issueNumber;

    @JSONPath(path="prices[0]/price")
    @MetaProperty
    private Double price;

    @JSONPath(path="dates[0]/date")
    @MetaProperty
    private Date issueDate;

    @JSONPath(path="images[0]/path|{/standard_xlarge.}|images[0]/extension")
    @MetaProperty
    protected String image;

//    can't add @MetaProperty here because JPA does not support String[]
    @JSONPath(path="creators/items/name")
    private String[] creators;
    ...
```

Notes on JSONPath annotation
============================
The properties of the MarvelComic class will be populated from the json Comic object by reflection unless a JSONPath annotation is found for the property.

ie, there is a "title" property in the Marvel Comic json object, so no JSONPath annotation is required for the title property of MarvelComic.

However, we want to populate the MarvelComic's "marvelid" property with the Comic's "id" property, so we have a JSONPath annotation for marvelid. 

The price property is more interesting - the 
```java
@JSONPath(path="prices[0]/price")
``` 
annotation says "take the first entry (if there is one) 
in the Comic's "prices" json array and use the "price" property of that json object to populate the MarvelComic's "price" property.

The image property demonstrates a sort of templating aspect of the JSONPath annotation. The annotation
```java
@JSONPath(path="images[0]/path|{/standard_xlarge.}|images[0]/extension")
``` 
says "concatenate the path property of the first object of the 
images json array with the text "/standard_xlarge." and then concatenate that with the extension property of the first object of the 
images json array"

The creators String array property is constructed using the 
```java
@JSONPath(path="creators/items/name")
```
annotation. Since creators/items is a json array in the Comic json object, the MarvelComic creators String array is populated from the
"name" property of all the items in that array.

APIServiceBean
==============

The MarvelServiceBean class extends the APIServiceBean class, which provides helper methods to make setting up a connection to the service easier. The getBaseURL should be
implemented with knowledge of the service and how to construct the authorization hash.
```java
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
```

The services are then accessed with calls to the getResults method, passing in the path that is appended to the base URL, 
along with the additional query string and the class that will be populated from the json returned by the service call.
```java
	@Override
	public List<MarvelCharacter> getCharacters(String startsWith) {
		return getResults("characters", "nameStartsWith=" + encode(startsWith), MarvelCharacter.class);
	}
```

The Datasource
==============
The MarvelCharacterDatasource merely calls the MarvelCharacterService and passes on the Collection of MarvelCharacters to the user of the Datasource.

