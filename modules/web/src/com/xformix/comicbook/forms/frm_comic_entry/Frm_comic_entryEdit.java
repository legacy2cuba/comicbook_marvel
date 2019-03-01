package com.xformix.comicbook.forms.frm_comic_entry;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.persistence.PersistenceContext;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowManager.OpenType;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.ClasspathResource;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.Image;
import com.haulmont.cuba.gui.components.Image.ScaleMode;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.TextInputField;
import com.haulmont.cuba.gui.components.TextInputField.TextChangeEvent;
import com.haulmont.cuba.gui.components.UrlResource;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.Datasource.ItemChangeEvent;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.executors.BackgroundWorker;
import com.haulmont.cuba.gui.executors.UIAccessor;
import com.xformix.comicbook.entity.MarvelComic;
import com.xformix.comicbook.entity.MarvelSeries;
import com.xformix.comicbook.entity.QryListPublisher;
import com.xformix.comicbook.entity.TblLocation;
import com.xformix.comicbook.entity.Tblcatalog;
import com.xformix.comicbook.entity.Tbltitle;
import com.xformix.comicbook.service.MarvelService;
import com.xformix.comicbook.web.tblcatalog.TblcatalogEdit;
import com.xformix.cuba.utils.Utils;

public class Frm_comic_entryEdit extends AbstractEditor<Tbltitle> {

    private MarvelService marvelService = AppBeans.get(MarvelService.NAME);

    @Inject
    TextField SeriesName;

    @Inject
    LookupField Title;

    @Inject
    LookupField ComicTitle;

    @Inject
    LookupField Publisher;

    @Inject
    LookupField findTitleLookup;

    @Inject
    Image image;

    @Inject
    GroupTable<Tblcatalog> ComicDetail;

    @Inject
    CollectionDatasource<MarvelSeries, UUID> marvelSeriesDs;

    @Inject
    GroupDatasource<Tbltitle, Integer> tbltitleSearchDs;

    @Inject
    GroupDatasource<Tbltitle, Integer> tbltitlesDs;

    @Inject
    Datasource<Tbltitle> tbltitleDs;

    @Inject
    GroupDatasource<Tblcatalog, Integer> tblcatalogDs;
    
    @Inject
    CollectionDatasource<QryListPublisher, Long> qrylistpublisheresDs;

    @Inject
    protected BackgroundWorker backgroundWorker;

    @Inject
    Metadata metadata;

    UIAccessor uiAccessor;

    Thread timeoutThread;
    String titleLookupString = "";
    List<String> seriesOpts = new ArrayList<>();
    List<Integer> seriesIds = new ArrayList<>();
    List<String> titleOpts = new ArrayList<>();
    List<String> findTitleOpts = new ArrayList<>();
    List<MarvelComic> comics;
    MarvelComic selectedComic;
    boolean textChanged = false;
    Collection<Tbltitle> foundTitles;

    private Frm_comic_entryCombo combo;

    @Override
    public void init(Map<String, Object> params) {
	super.init(params);
	uiAccessor = backgroundWorker.getUIAccessor();

	qrylistpublisheresDs.refresh();
	List<String> opts = new ArrayList<>();
	for (QryListPublisher pub : qrylistpublisheresDs.getItems()) {
	    if(pub.getPublisher() != null)
		opts.add(pub.getPublisher());
	};
	Publisher.setOptionsList(opts);

	Publisher.setNewOptionAllowed(true);
	Publisher.setNewOptionHandler(new LookupField.NewOptionHandler() {
	    @Override
	    public void addNewOption(String caption) {
		tbltitleDs.getItem().setPublisher(caption);
		opts.add(caption);
		Publisher.setOptionsList(opts);
		Publisher.setValue(caption);
	    }
	});

	tblcatalogDs.addItemChangeListener(new Datasource.ItemChangeListener<Tblcatalog>() {
	    @Override
	    public void itemChanged(ItemChangeEvent<Tblcatalog> e) {
		if (e.getItem() != null) {
		    Integer comicId = e.getItem().getMarvelComicId();
		    MarvelComic comic = marvelService.getComicById(comicId);
		    setImage(comic);
		}
	    }

	});
	
	ComicTitle.setNewOptionAllowed(true);
	ComicTitle.setNewOptionHandler(new LookupField.NewOptionHandler() {
	    @Override
	    public void addNewOption(String caption) {
		ComicTitle.setValue(caption);
	    }
	});
	
	titleOpts.add("...");
	ComicTitle.setOptionsList(titleOpts);

	ComicTitle.addValueChangeListener(new ValueChangeListener() {

	    @Override
	    public void valueChanged(ValueChangeEvent e) {
		String title = ComicTitle.getValue();
		selectedComic = null;
		if (title != null && comics != null) {
		    for (MarvelComic comic : comics) {
			if (comic.getTitle().equals(title)) {
			    selectedComic = comic;
			}
		    }
		}
		setImage(selectedComic);
	    }
	});

	seriesOpts.add("...");
	Title.setOptionsList(seriesOpts);
	Title.setNewOptionAllowed(true);
	Title.setNewOptionHandler(new LookupField.NewOptionHandler() {
	    @Override
	    public void addNewOption(String caption) {
		Title.setValue(caption);
	    }
	});

	tbltitlesDs.addItemChangeListener(new Datasource.ItemChangeListener<Tbltitle>() {
	    @Override
	    public void itemChanged(ItemChangeEvent<Tbltitle> e) {
		if (e.getItem() != null) {
		    try {
			if (!PersistenceHelper.isNew(e.getItem())) {
			    Tbltitle reloadedItem = Utils.reload(getDsContext(), e.getItem(), tbltitlesDs);
			    tbltitleDs.setItem(reloadedItem);
			} else {
			    tbltitleDs.setItem(e.getItem());
			}
			Integer id = e.getItem().getMarvelSeriesId();
			setComics(id);
		    } catch (Exception ex) {
			ex.printStackTrace();
		    }
		}
	    }
	});

	Title.addValueChangeListener(new ValueChangeListener() {
	    @Override
	    public void valueChanged(ValueChangeEvent e) {
		ComicTitle.setEditable(true);
		String val = Title.getValue();
		if (val == null || "".equals(val))
		    return;
		int selIndex = seriesOpts.indexOf(val);
		if (selIndex >= 0) {
		    Integer id = seriesIds.get(selIndex);
		    tbltitlesDs.getItem().setMarvelSeriesId(id);
		    setComics(id);
		}
	    }
	});

	SeriesName.addTextChangeListener(new TextInputField.TextChangeListener() {
	    @Override
	    public void textChange(TextChangeEvent event) {
		String searchString = event.getText();
		titleLookupString = searchString;
		handleDelayedValueChanges(searchString, (Object o) -> {
		    seriesOpts.clear();
		    seriesIds.clear();
		    List<MarvelSeries> series = marvelService.getSeries(titleLookupString);
		    MarvelSeries[] serArray = new MarvelSeries[series.size()];
		    series.toArray(serArray);
		    Arrays.sort(serArray, new Comparator<MarvelSeries>() {
			@Override
			public int compare(MarvelSeries o1, MarvelSeries o2) {
			    return o1.getTitle().compareTo(o2.getTitle());
			}
		    });
		    for (MarvelSeries ser : serArray) {
			if (ser.getTitle() != null) {
			    seriesOpts.add(ser.getTitle());
			    seriesIds.add(ser.getMarvelid());
			}
		    }
		    if (seriesOpts.size() == 0) {
			seriesOpts.add(" ");
		    } else {
			Publisher.setValue("Marvel");
		    }
		    Title.setOptionsList(seriesOpts);
		});
	    }
	});
	    
	findTitleLookup.setEditable(true);
	findTitleOpts.add(" "); 
	findTitleLookup.setOptionsList(findTitleOpts);
	findTitleLookup.setFilterPredicate(new LookupField.FilterPredicate() {
	    @Override
	    public boolean test(String itemCaption, String searchString) {
		searchString = searchString.trim();
		titleLookupString = searchString;
		if(searchString.equals(""))
		    return true;
		handleDelayedValueChanges(searchString, (Object o) -> {
		    findTitleOpts.clear();
		    tbltitleSearchDs.setQuery("select e from comicbook$Tbltitle e where lower(e.title) like '%" + titleLookupString.toLowerCase() + "%' order by e.title");
		    tbltitleSearchDs.refresh();
		    foundTitles = tbltitleSearchDs.getItems();
		    for (Tbltitle title : foundTitles) {
			if(title.getTitle() != null)
			    findTitleOpts.add(title.getTitle());
		    }
		    findTitleLookup.setOptionsList(findTitleOpts);
		    findTitleLookup.setValue(titleLookupString);
		});
		return itemCaption.toLowerCase().contains(titleLookupString.toLowerCase());
	    }
	} );
	
	findTitleLookup.addValueChangeListener(new ValueChangeListener() {
	    @Override
	    public void valueChanged(ValueChangeEvent e) {
		String title = (String) e.getValue();
		for (Tbltitle ftitle : foundTitles) {
		    if(ftitle.getTitle().equals(title)) {
			tbltitlesDs.setItem(ftitle);
		    }
		}
	    }
	} );
    }

    private void handleDelayedValueChanges(String searchString, Consumer<Object> f) {
	textChanged = true;
	if (timeoutThread == null || !timeoutThread.isAlive()) {
	    timeoutThread = new Thread(new Runnable() {
		@Override
		public void run() {
		    try {
			while (textChanged) {
			    textChanged = false;
			    Thread.sleep(500);
			}
			uiAccessor.access(() -> {
			    f.accept(null);
			});
		    } catch (InterruptedException e) {
		    }
		}
	    });
	    timeoutThread.start();
	}
    }
    
    private void setImage(MarvelComic comic) {
	if (comic != null) {
	    String url = null;
	    try {
		url = comic.getImage();
		image.setSource(UrlResource.class).setUrl(new URL(url));
	    } catch (Exception e1) {
		image.setSource(ClasspathResource.class).setPath("com/xformix/comicbook/forms/frm_comic_entry/NO_IMAGE.png");
		System.out.println("INFO: unable to load image from url: "+url+", error: "+e1);
	    }
	} else {
	    image.setSource(ClasspathResource.class).setPath("com/xformix/comicbook/forms/frm_comic_entry/NO_IMAGE.png");
	}
	image.setScaleMode(ScaleMode.FILL);
    }

    protected void setComics(Integer id) {
	titleOpts.clear();

	if (id != null) {
	    comics = marvelService.getComicsBySeriesId(id);
	    MarvelComic[] array = new MarvelComic[comics.size()];
	    comics.toArray(array);
	    Arrays.sort(array, new Comparator<MarvelComic>() {
		@Override
		public int compare(MarvelComic o1, MarvelComic o2) {
		    if (o1.getIssueNumber() != null && o2.getIssueNumber() != null) {
			return o1.getIssueNumber() - o2.getIssueNumber();
		    } else if(o1.getTitle() != null && o2.getTitle() != null) {
			return o1.getTitle().compareTo(o2.getTitle());
		    } else {
			return 0;
		    }
		}
	    });

	    for (MarvelComic marvelComic : array) {
		if(marvelComic.getTitle() != null)
		    titleOpts.add(marvelComic.getTitle());
	    }
	}
	ComicTitle.setOptionsList(titleOpts);
	if (titleOpts.size() > 0) {
	    ComicTitle.setValue(titleOpts.get(0));
	} else {
	    ComicTitle.setValue(null);
	}
    }

    public void doAddStorage() {
	TblLocation location = Utils.create(TblLocation.class, metadata, getDsContext().getDataSupplier());

	Map<String, Object> map = new HashMap<>();
	AbstractEditor ed = openEditor("comicbook$TblLocation.edit", location, WindowManager.OpenType.DIALOG, map);

    }

    public void showHelp() {
	openWindow("comicbook$helpWindow", OpenType.DIALOG);
    }

    public void setCombo(Frm_comic_entryCombo combo) {
	this.combo = combo;
    }

    public void doAddIssue() {
	Tblcatalog issue = Utils.create(Tblcatalog.class, metadata, getDsContext().getDataSupplier());

	Tbltitle title = tbltitleDs.getItem();
	issue.setTitlenum(title);

	if (selectedComic != null) {
	    issue.setMarvelComicId(selectedComic.getMarvelid());
	    issue.setComicTitle(selectedComic.getTitle());
	    issue.setCost(selectedComic.getPrice());
	    issue.setIssue_date(selectedComic.getIssueDate());
	    issue.setIssuenum((double) selectedComic.getIssueNumber());
	} else {
	    issue.setMarvelComicId(0);
	    issue.setComicTitle(ComicTitle.getValue());
	    issue.setCost(0d);
	    issue.setIssue_date(null);
	    issue.setIssuenum(1d);
	}

	Map<String, Object> map = new HashMap<>();
	TblcatalogEdit ed = (TblcatalogEdit) openEditor("comicbook$Tblcatalog.edit", issue, WindowManager.OpenType.DIALOG, map);

	ed.addBeforeCommitListener(new CloseListener() {
	    @Override
	    public void windowClosed(String actionId) {
		//if we are about to add a catalog, but the title has not yet been saved, save the title first
		Tbltitle title = tbltitleDs.getItem();
		if (!tbltitlesDs.containsItem(title.getId())) {
		    combo.save();
		    ComicTitle.setEditable(true);
		}
		tblcatalogDs.refresh();
	    }
	});

	ed.addCloseWithCommitListener(new CloseWithCommitListener() {
	    @Override
	    public void windowClosedWithCommitAction() {
		tblcatalogDs.refresh();
	    }
	});

    }
    @Override
    protected boolean postCommit(boolean committed, boolean close) {
	    ComicTitle.setEditable(true);
	    return super.postCommit(committed, close);
    }
}