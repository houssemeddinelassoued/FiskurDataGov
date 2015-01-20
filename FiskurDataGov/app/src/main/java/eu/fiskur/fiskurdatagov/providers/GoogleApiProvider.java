package eu.fiskur.fiskurdatagov.providers;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by jonathan.fisher on 20/01/2015.
 */
public class GoogleApiProvider {

    public static GoogleApiClient client = null;

    public static void connect(){
        if(client != null){
            client.connect();
        }
    }

}
