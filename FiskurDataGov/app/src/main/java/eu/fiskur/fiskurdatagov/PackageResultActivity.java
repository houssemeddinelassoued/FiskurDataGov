package eu.fiskur.fiskurdatagov;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.fiskur.fiskurdatagov.objects.Organization;
import eu.fiskur.fiskurdatagov.objects.PackageSearchResultObject;
import eu.fiskur.fiskurdatagov.objects.PackageSearchResultObjectResource;
import timber.log.Timber;


public class PackageResultActivity extends ActionBarActivity {

    @InjectView(R.id.results_list_view) ListView resultsListView;
    PackageSearchResultObject resultObj;
    ArrayAdapter<PackageSearchResultObjectResource> resultResourceAdapter;
    GoogleApiClient mGoogleApiClient;

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
        }

        connectDrive();
    }

    @Override
    protected void onStart() {
        super.onStart();

        l("Attempting to connect to GoogleDrive");
        mGoogleApiClient.connect();
    }

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 9876;

    private void connectDrive(){
        l("Creating GoogleDrive client");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        l("GoogleApiClient onConnected()");
                        createRootFolder();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        l("GoogleApiClient onConnectionSuspended()");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        l("GoogleApiClient onConnectionFailed()");
                        if (connectionResult.hasResolution()) {
                            try {
                                connectionResult.startResolutionForResult(PackageResultActivity.this, RESOLVE_CONNECTION_REQUEST_CODE);
                            } catch (IntentSender.SendIntentException e) {
                                Timber.e(e.toString());
                            }
                        } else {
                            Timber.e(connectionResult.toString());
                            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), PackageResultActivity.this, 0).show();
                        }
                    }
                })
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
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

        resultResourceAdapter = new ArrayAdapter<PackageSearchResultObjectResource>(this, android.R.layout.simple_list_item_1, resultObj.getResources());
        resultsListView.setAdapter(resultResourceAdapter);

        resultsListView.addHeaderView(listHeader);
        resultsListView.setHeaderDividersEnabled(true);

        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    return;
                }
                PackageSearchResultObjectResource resource = resultResourceAdapter.getItem(position - 1);
                String url = resource.getUrl();
                Timber.d("url: " + url);

                if(url.endsWith(".xls")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "application/vnd.ms-excel");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                    try {
                        startActivity(intent);
                    }
                    catch (ActivityNotFoundException e) {
                        Toast.makeText(PackageResultActivity.this, "No Application Available to View Excel", Toast.LENGTH_SHORT).show();
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

    public void createRootFolder(){
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("DataGovUk").build();
        Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(mGoogleApiClient, changeSet).setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
            @Override
            public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                if (!driveFolderResult.getStatus().isSuccess()) {
                    l("Error while trying to create the folder");
                    return;
                }
                l("Created a folder: " + driveFolderResult.getDriveFolder().getDriveId());
            }
        });
    }
}
