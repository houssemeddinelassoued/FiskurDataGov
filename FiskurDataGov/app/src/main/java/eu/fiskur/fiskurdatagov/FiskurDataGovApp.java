package eu.fiskur.fiskurdatagov;

import android.app.Application;

import com.squareup.otto.Bus;

import eu.fiskur.fiskurdatagov.providers.BusProvider;
import retrofit.RestAdapter;
import timber.log.Timber;

import static timber.log.Timber.DebugTree;

/**
 * Created by Jonathan Fisher on 17/01/15.
 */
public class FiskurDataGovApp extends Application{

    private Bus bus = BusProvider.getInstance();
    private DataGovRepository dataGovRepo;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new DebugTree());

        Api api = new RestAdapter.Builder()
                .setEndpoint("http://data.gov.uk/api/action")
                .build().create(Api.class);

        dataGovRepo = new DataGovRepository(api, bus);
        bus.register(dataGovRepo);
        bus.register(this);
    }
}
