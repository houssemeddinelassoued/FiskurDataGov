package eu.fiskur.fiskurdatagov.objects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import eu.fiskur.fiskurdatagov.objects.Resource;

/**
 * Created by Jonathan Fisher on 18/01/15.
 */
public class Package {
    String owner_org;
    String maintainer;
    @SerializedName("private") boolean isPrivate;
    String maintainer_email;
    String revision_timestamp;
    String id;
    String metadata_created;
    String metadata_modified;
    String author;
    String author_email;
    String state;
    String version;
    String license_id;
    String type;
    ArrayList<Resource> resources;
    int num_resources;
    ArrayList<Tag> tags;
    String title;
    String creator_user_id;
    int num_tags;
    String name;
    boolean isopen;
    String url;
    String notes;
    String license_title;
    String license_url;
    Organization organization;
    String revision_id;

    @Override
    public String toString() {
        String label = name.replace("-",  " ");
        label = label.replace("_", " ");
        return label;
    }
}
