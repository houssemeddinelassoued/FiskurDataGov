package eu.fiskur.fiskurdatagov;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.fiskur.fiskurdatagov.objects.Organization;
import eu.fiskur.fiskurdatagov.objects.PackageSearchResultObject;
import eu.fiskur.fiskurdatagov.objects.PackageSearchResultObjectResource;
import eu.fiskur.fiskurdatagov.providers.GoogleApiProvider;
import timber.log.Timber;


public class PackageResultActivity extends ActionBarActivity {
    private static final String FOLDER = "DataGovUKFiles";
    @InjectView(R.id.results_list_view) ListView resultsListView;
    PackageSearchResultObject resultObj;
    ResourceAdapter resourceAdapter;
    DriveId driveId;

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
            initProjectDriveId();
        }else{
            Timber.e("No extras object");
        }
    }

    private void l(String message){
        Timber.d(message);
    }

    private void buildScreen(){
        Timber.d("Number of resources: " + resultObj.getResources().size());

        LinearLayout listHeader = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.list_header, null);
        TextView titleView = TextView.class.cast(listHeader.findViewById(R.id.list_header_title_label));
        titleView.setText(resultObj.getTitle());

        TextView subtitleView = TextView.class.cast(listHeader.findViewById(R.id.list_header_content_label));

        Organization org = resultObj.getOrganization();
        String label = "";
        label = append(label, "Organisation", org.getTitle(), true);
        label = append(label, "Notes", resultObj.getNotes(), false);
        label = append(label, "Contact", resultObj.getContactEmail(), false);

        subtitleView.setText(label);

        resourceAdapter = new ResourceAdapter(this, resultObj.getResources());
        resultsListView.setAdapter(resourceAdapter);

        resultsListView.addHeaderView(listHeader);
        resultsListView.setHeaderDividersEnabled(true);

        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    return;
                }
                PackageSearchResultObjectResource resource = (PackageSearchResultObjectResource)resourceAdapter.getItem(position - 1);
                String url = resource.getUrl();
                Timber.d("url: " + url);

                if(url.toLowerCase().endsWith(".xls") || url.toLowerCase().endsWith("pdf")){
                    if(GoogleApiProvider.client.isConnected()){
                        Timber.d("Sending file to Google Drive");
                        //Send to Google Drive
                    }else{
                        Timber.d("Google Drive not connected, using browser to handle file");
                        launchBrowser(url);
                    }
                    return;
                }else{
                    launchBrowser(url);
                }

            }
        });
    }

    private void launchBrowser(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
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

    public void initProjectDriveId(){
        DriveFolder rootFolder = Drive.DriveApi.getRootFolder(GoogleApiProvider.client);
        rootFolder.listChildren(GoogleApiProvider.client).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult result) {
                if(result.getStatus().isSuccess()){
                    //ODO - replace this with a query...
                    for (Metadata md : result.getMetadataBuffer()) {
                        if(md.isFolder() && md.getTitle().equals(FOLDER)){
                            driveId = md.getDriveId();
                            l("Folder already exists: " + driveId);
                        }
                    }
                    if(driveId == null){
                        l("Creating folder...");
                        createProjectFolder();
                    }
                }
            }
        });
    }

    private void createProjectFolder(){
        DriveFolder rootFolder = Drive.DriveApi.getRootFolder(GoogleApiProvider.client);
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(FOLDER).build();
        rootFolder.createFolder(GoogleApiProvider.client, changeSet).setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
            @Override
            public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                if (!driveFolderResult.getStatus().isSuccess()) {
                    l("Error while trying to create folder");
                    return;
                }
                driveId = driveFolderResult.getDriveFolder().getDriveId();
                l("Created the folder: " + driveId);
            }
        });
    }
}
