package eu.fiskur.fiskurdatagov;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import eu.fiskur.fiskurdatagov.events.ErrorEvent;
import eu.fiskur.fiskurdatagov.events.LoadTagsEvent;
import eu.fiskur.fiskurdatagov.events.TagsLoadedEvent;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by Jonathan Fisher on 17/01/15.
 */
public class DataGovRepository {
    private Api api;
    private Bus bus;

    public DataGovRepository(Api api, Bus bus) {
        this.api = api;
        this.bus = bus;
    }

    @Subscribe
    public void onLoadTags(LoadTagsEvent event) {
        Timber.d("onLoadTags(LoadTagsEvent event)");
        api.tagList(new Callback<TagListResponse>() {
            @Override
            public void success(TagListResponse tagListResponse, Response response) {
                bus.post(new TagsLoadedEvent(tagListResponse));
            }

            @Override
            public void failure(RetrofitError error) {
                bus.post(new ErrorEvent(error.getMessage()));
            }
        });
    }
}
