package eu.fiskur.fiskurdatagov.objects;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by primer on 18/01/15.
 */
public class PackageSearchResultObject implements Serializable {
    String license_title;
    String maintainer;
    @SerializedName("temporal_coverage-from") String temporal_coverage_from;
    String data_dict;
    //String mandate;
    @SerializedName("private") boolean isPrivate;
    String maintainer_email;
    String revision_timestamp;
    String update_frequency;
    String id;
    String metadata_created;
    String metadata_modified;
    String author;
    String author_email;
    String state;
    String version;
    String license_id;
    ArrayList<PackageSearchResultObjectResource> resources;
    int num_resources;
    @SerializedName("contact-email") String contact_email;
    ArrayList<Tag> tags;
    String creator_user_id;
    int num_tags;
    Organization organization;
    String name;
    boolean isopen;
    String url;
    String type;
    String notes;
    String owner_org;
    String license_url;
    String title;
    String revision_id;
    String date_released;
    @SerializedName("theme-primary")  String theme_primary;


    public String getTitle(){
        return title;
    }

    public Organization getOrganization(){
        return organization;
    }

    public String getContactEmail(){
        return contact_email;
    }


    public String getNotes(){
        return notes;
    }

    public int getResourceCount(){
        return num_resources;
    }

    public ArrayList<PackageSearchResultObjectResource> getResources(){
        return resources;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(title + "\n");
        sb.append("Resources: " + num_resources);
        return sb.toString();
    }
}
