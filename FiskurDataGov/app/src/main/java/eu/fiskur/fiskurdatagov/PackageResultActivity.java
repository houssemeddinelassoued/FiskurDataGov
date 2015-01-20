package eu.fiskur.fiskurdatagov;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.fiskur.fiskurdatagov.objects.Organization;
import eu.fiskur.fiskurdatagov.objects.PackageSearchResultObject;
import eu.fiskur.fiskurdatagov.objects.PackageSearchResultObjectResource;
import timber.log.Timber;


public class PackageResultActivity extends ActionBarActivity {
    private static final String FOLDER = "DataGovUKFiles";
    private static final int CREATE_FILE = 234;
    @InjectView(R.id.results_list_view) ListView resultsListView;
    PackageSearchResultObject resultObj;
    ResourceAdapter resourceAdapter;
    private long downloadId;

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

                if(isFile(url)){
                    Timber.d("Downloading file");
                    downloadFile(resource);
                }else{
                    launchBrowser(url);
                }

            }
        });
    }

    private boolean isFile(String url){
        String u = url.toLowerCase();
        boolean isFile = false;
        if(u.endsWith(".xls") || u.endsWith(".pdf") || u.endsWith(".doc") || u.endsWith("zip")){
            isFile = true;
        }

        return isFile;
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

    private void downloadFile(PackageSearchResultObjectResource resource){
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        String url = resource.getUrl();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloaded using Fiskur Data.Gov app");
        request.setTitle(resource.getTitle());
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String filename = url.substring(url.lastIndexOf("/") + 1, url.length());
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = manager.enqueue(request);
    }

    BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Timber.d("onDownloadComplete BroadcastReceiver...");

            Long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);

            if(downloadId == id){
                Timber.d("Gov.uk file is ready...");
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor cur = manager.query(query);

                if (cur.moveToFirst()) {
                    int columnIndex = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
                        String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                        File mFile = new File(Uri.parse(uriString).getPath());
                        if(mFile.exists()){
                            Timber.d("File Exists!");
                        }

                    } else {
                        Timber.e("File did not download successfully");
                    }
                }
            }
        }
    };

}
