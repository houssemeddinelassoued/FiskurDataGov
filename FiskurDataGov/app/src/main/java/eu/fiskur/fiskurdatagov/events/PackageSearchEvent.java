package eu.fiskur.fiskurdatagov.events;

/**
 * Created by Jonathan Fisher on 18/01/15.
 */
public class PackageSearchEvent {

    String query;

    public PackageSearchEvent(String query){
        this.query = query;
    }

    public String getQuery(){
        return query;
    }

}
