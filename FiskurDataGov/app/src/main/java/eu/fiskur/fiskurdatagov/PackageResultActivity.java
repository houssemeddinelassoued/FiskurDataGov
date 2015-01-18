package eu.fiskur.fiskurdatagov;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.fiskur.fiskurdatagov.objects.Organization;
import eu.fiskur.fiskurdatagov.objects.PackageSearchResultObject;
import eu.fiskur.fiskurdatagov.objects.PackageSearchResultObjectResource;
import timber.log.Timber;


public class PackageResultActivity extends ActionBarActivity {

    @InjectView(R.id.results_list_view) ListView resultsListView;
    @InjectView(R.id.resource_title_label) TextView resourceTitleTextView;
    @InjectView(R.id.resource_content_label) TextView resourceContentTextView;
    PackageSearchResultObject resultObj;
    ArrayAdapter<PackageSearchResultObjectResource> resultResourceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_result);
        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras().containsKey("searchresultobj")){
            Timber.d("Has object in extras");
            resultObj = (PackageSearchResultObject) getIntent().getExtras().get("searchresultobj");
            buildScreen();
        }else{
            Timber.e("No extras object");
            resourceTitleTextView.setText("No resource available");
        }
    }

    private void buildScreen(){
        Timber.d("Number of resources: " + resultObj.getResources().size());



        resultResourceAdapter = new ArrayAdapter<PackageSearchResultObjectResource>(this, android.R.layout.simple_list_item_1, resultObj.getResources());
        resultsListView.setAdapter(resultResourceAdapter);

        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PackageSearchResultObjectResource resource = resultResourceAdapter.getItem(position);
                String url = resource.getUrl();
                Timber.d("url: " + url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        Organization org = resultObj.getOrganization();
        String label = "";
        resourceTitleTextView.setText(resultObj.getTitle());
        label = append(label, "Organisation", org.getTitle(), true);
        label = append(label, "Notes", resultObj.getNotes(), false);
        label = append(label, "Contact", resultObj.getContactEmail(), false);

        resourceContentTextView.setText(label);

    }

    public String append(String label, String section, String more, boolean first){
        if(more != null && !more.isEmpty()){
            if(first){
                label += section + ": " + more;
            }else{
                label += "\n\n" + section + ": " + more;
            }

        }
        return label;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
