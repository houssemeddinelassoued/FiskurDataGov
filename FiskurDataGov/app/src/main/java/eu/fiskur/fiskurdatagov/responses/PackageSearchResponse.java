package eu.fiskur.fiskurdatagov.responses;

import eu.fiskur.fiskurdatagov.objects.PackageSearchResult;

/**
 * Created by primer on 18/01/15.
 */
public class PackageSearchResponse {
    String help;
    boolean success;
    PackageSearchResult result;

    public PackageSearchResult getResult(){
        return result;
    }
}
