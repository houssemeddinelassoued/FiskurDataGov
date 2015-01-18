package eu.fiskur.fiskurdatagov.objects;

import java.util.ArrayList;

/**
 * Created by primer on 18/01/15.
 */
public class PackageSearchResult {
    int count;
    String sort;
    ArrayList<PackageSearchResultObject> results;


    public ArrayList<PackageSearchResultObject> getResults(){
        return results;
    }
}
