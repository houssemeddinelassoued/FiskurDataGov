package eu.fiskur.fiskurdatagov.objects;

import java.io.Serializable;

/**
 * Created by Jonathan Fisher on 18/01/15.
 */
public class PackageSearchResultObjectResource implements Serializable{
    String mimetype;
    String cache_url;
    String hash;
    String description;
    String name;
    String format;
    String url;
    String cache_last_updated;
    String created;
    String cache_filepath;
    String last_modified;
    int position;
    String revision_id;
    String id;
    String resource_type;
    int size;

    public PackageSearchResultObjectResource(){

    }

    public String getCacheUrl(){
        return cache_url;
    }

    public String getUrl(){
        return url;
    }

    public String getFormat(){
        return format;
    }

    public String getTitle(){
        StringBuffer sb = new StringBuffer();
        boolean hasName = false;
        if(name != null && !name.isEmpty()){
            hasName = true;
            sb.append(name);
        }
        if(description != null && !description.isEmpty()){
            if(hasName){
                sb.append(": ");
            }
            sb.append(description);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        boolean hasName = false;
        if(name != null && !name.isEmpty()){
            hasName = true;
            sb.append(name);
        }
        if(description != null && !description.isEmpty()){
            if(hasName){
                sb.append(": ");
            }
            sb.append(description);
        }

        if(format != null &&
                !format.isEmpty() &&
                !format.toLowerCase().equals("html") &&
                !format.toLowerCase().equals("shtml") &&
                !format.toLowerCase().equals("pdf") &&
                !format.toLowerCase().equals("xls")){
            sb.append(" (" + format + ")");
        }

        return sb.toString();
    }
}
