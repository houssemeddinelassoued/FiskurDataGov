package eu.fiskur.fiskurdatagov;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.fiskur.fiskurdatagov.events.ErrorEvent;
import eu.fiskur.fiskurdatagov.events.LoadTagsEvent;
import eu.fiskur.fiskurdatagov.events.ShowTagEvent;
import eu.fiskur.fiskurdatagov.events.TagPackagesLoadedEvent;
import eu.fiskur.fiskurdatagov.events.TagsLoadedEvent;
import eu.fiskur.fiskurdatagov.objects.*;
import eu.fiskur.fiskurdatagov.objects.Package;
import timber.log.Timber;


public class MainActivity extends ActionBarActivity {

    Bus bus = BusProvider.getInstance();
    ArrayAdapter<String> tagsAdapter;
    ArrayAdapter<Package> packagesAdapter;
    @InjectView(R.id.search_label_text_view) TextView searchLabelTextView;
    @InjectView(R.id.autocomplete_text_view) AutoCompleteTextView autoCompleteTextView;
    @InjectView(R.id.progress_bar) ProgressBar progressBar;
    @InjectView(R.id.packages_list_view) ListView packagesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        autoCompleteTextView.setActivated(false);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tag = tagsAdapter.getItem(position);
                progressBar.setVisibility(View.VISIBLE);
                bus.post(new ShowTagEvent(tag));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        bus.register(this);
        bus.post(new LoadTagsEvent());
    }

    @Override
    protected void onPause() {
        super.onPause();

        bus.unregister(this);
    }

    @Subscribe
    public void onTagsLoaded(TagsLoadedEvent tagsLoadedEvent){
        searchLabelTextView.setText("Search tag (" + tagsLoadedEvent.getResponse().getResult().size() + " entries)");
        tagsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, tagsLoadedEvent.getResponse().getResult());
        autoCompleteTextView.setAdapter(tagsAdapter);
        autoCompleteTextView.setActivated(true);
        progressBar.setVisibility(View.GONE);
    }

    @Subscribe
    public void onTagPackagesLoaded(TagPackagesLoadedEvent tagPackagesLoadedEvent){
        Timber.d(tagPackagesLoadedEvent.getResponse().getResult().toString());
        progressBar.setVisibility(View.GONE);

        packagesAdapter = new ArrayAdapter<Package>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, tagPackagesLoadedEvent.getResponse().getResult().getPackages());
        packagesListView.setAdapter(packagesAdapter);
        packagesListView.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onApiError(ErrorEvent error) {
        Toast.makeText(MainActivity.this, error.getError(), Toast.LENGTH_LONG).show();
        Timber.e(error.getError());
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}