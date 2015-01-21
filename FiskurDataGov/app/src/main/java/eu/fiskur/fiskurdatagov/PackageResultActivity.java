package eu.fiskur.fiskurdatagov;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
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
import java.io.IOException;

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
        if(u.endsWith(".xls")
                || u.endsWith(".xlsx")
                || u.endsWith(".pdf")
                || u.endsWith(".doc")
                || u.endsWith(".docx")
                || u.endsWith(".zip")
                || u.endsWith(".txt")
                || u.endsWith(".ppt")
                || u.endsWith(".pptx")
                || u.endsWith(".csv")){
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

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(onDownloadComplete);
    }

    private void downloadFile(PackageSearchResultObjectResource resource){

        String url = resource.getUrl();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
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
                            try {
                                openFile(mFile, mFile.getName());
                            } catch(ActivityNotFoundException e){
                                Timber.e(e.toString());
                            } catch (IOException e) {
                                Timber.e(e.toString());
                            }
                        }
                    } else {
                        Timber.e("File did not download successfully");
                    }
                }
            }
        }
    };

    private void openFile(File file, String url) throws IOException, ActivityNotFoundException {

        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if(url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if(url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if(url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if(url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
