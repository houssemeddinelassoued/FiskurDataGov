package eu.fiskur.fiskurdatagov.events;

import eu.fiskur.fiskurdatagov.responses.PackageSearchResponse;

/**
 * Created by Jonathan Fisher on 18/01/15.
 */
public class PackageSearchResultsEvent {

    PackageSearchResponse response;

    public PackageSearchResultsEvent(PackageSearchResponse response){
        this.response = response;
    }

    public PackageSearchResponse getResponse(){
        return response;
    }
}
