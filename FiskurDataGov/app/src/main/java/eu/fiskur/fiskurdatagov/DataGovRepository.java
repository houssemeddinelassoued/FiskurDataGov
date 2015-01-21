package eu.fiskur.fiskurdatagov;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import eu.fiskur.fiskurdatagov.events.ErrorEvent;
import eu.fiskur.fiskurdatagov.events.LoadTagsEvent;
import eu.fiskur.fiskurdatagov.events.PackageSearchEvent;
import eu.fiskur.fiskurdatagov.events.PackageSearchResultsEvent;
import eu.fiskur.fiskurdatagov.events.ShowTagEvent;
import eu.fiskur.fiskurdatagov.events.TagPackagesLoadedEvent;
import eu.fiskur.fiskurdatagov.events.TagsLoadedEvent;
import eu.fiskur.fiskurdatagov.responses.PackageSearchResponse;
import eu.fiskur.fiskurdatagov.responses.TagListResponse;
import eu.fiskur.fiskurdatagov.responses.TagShowResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by Jonathan Fisher on 17/01/15.
 */
public class DataGovRepository {
    static final long WEEK = 604800000;
    static final String PREFS = "eu.fiskur.fiskurdatagov.DataGovRepository.PREFS";
    Api api;
    Bus bus;
    Context context;

    public DataGovRepository(Api api, Bus bus, Context context) {
        this.api = api;
        this.bus = bus;
        this.context = context;
    }

    @Subscribe
    public void onLoadTags(LoadTagsEvent event) {
        Timber.d("onLoadTags(LoadTagsEvent event)");

        SharedPreferences prefs = context.getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
        if(prefs.contains("tags") && prefs.contains("timstamp")){
            Set<String> set = prefs.getStringSet("tags", null);
            long timestamp = prefs.getLong("timestamp", 0);
            if(set == null || updateTagsRequired(timestamp)){
                loadTags();
            }else{
                ArrayList<String> tagsList = new ArrayList<String>(set);
                TagListResponse tagListResponse = new TagListResponse();
                tagListResponse.setResult(tagsList);
                bus.post(new TagsLoadedEvent(tagListResponse));
            }
        }else{
            loadTags();
        }
    }

    private boolean updateTagsRequired(long timestamp){
        boolean update = false;
        long period = System.currentTimeMillis() - timestamp;

        if(period > WEEK){
            update = true;
        }

        return update;
    }

    private void saveTags(TagListResponse tagListResponse){
        Set<String> set = new HashSet<String>();
        for(String string : tagListResponse.getResult()){
            set.add(string);
        }

        SharedPreferences prefs = context.getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
        prefs.edit().putStringSet("tags", set).commit();
        prefs.edit().putLong("timestamp", System.currentTimeMillis()).commit();
    }

    private void loadTags(){
        api.tagList(new Callback<TagListResponse>() {
            @Override
            public void success(TagListResponse tagListResponse, Response response) {
                saveTags(tagListResponse);
                bus.post(new TagsLoadedEvent(tagListResponse));
            }

            @Override
            public void failure(RetrofitError error) {
                bus.post(new ErrorEvent(error.getMessage()));
            }
        });
    }

    @Subscribe
    public void onShowTag(ShowTagEvent event){
        api.tagShow(event.getTag(), new Callback<TagShowResponse>() {
            @Override
            public void success(TagShowResponse tagShowResponse, Response response) {
                bus.post(new TagPackagesLoadedEvent(tagShowResponse));
            }

            @Override
            public void failure(RetrofitError error) {
                bus.post(new ErrorEvent(error.getMessage()));
            }
        });
    }

    @Subscribe
    public void onSearchPackages(PackageSearchEvent event){
        api.searchPackages(event.getQuery(), new Callback<PackageSearchResponse>() {
            @Override
            public void success(PackageSearchResponse packageSearchResponse, Response response) {
                bus.post(new PackageSearchResultsEvent(packageSearchResponse));
            }

            @Override
            public void failure(RetrofitError error) {
                bus.post(new ErrorEvent(error.getMessage()));
            }
        });
    }
}
