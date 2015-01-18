package eu.fiskur.fiskurdatagov;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.fiskur.fiskurdatagov.events.ErrorEvent;
import eu.fiskur.fiskurdatagov.events.LoadTagsEvent;
import eu.fiskur.fiskurdatagov.events.TagsLoadedEvent;
import timber.log.Timber;


public class MainActivity extends ActionBarActivity {

    private Bus bus = BusProvider.getInstance();
    private ArrayAdapter<String> tagsAdapter;
    @InjectView(R.id.autocomplete_text_view) AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tag = tagsAdapter.getItem(position);
                Toast.makeText(MainActivity.this, tag, Toast.LENGTH_SHORT).show();
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
        int tagCount = tagsLoadedEvent.getResponse().getResult().size();
        Toast.makeText(MainActivity.this, "Loaded tags: " + tagCount, Toast.LENGTH_LONG).show();
        Timber.d("onTagsLoaded, tags count: " + tagCount);

        tagsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, tagsLoadedEvent.getResponse().getResult());
        autoCompleteTextView.setAdapter(tagsAdapter);
    }

    @Subscribe
    public void onApiError(ErrorEvent error) {
        Toast.makeText(MainActivity.this, error.getError(), Toast.LENGTH_LONG).show();
        Timber.e(error.getError());
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
