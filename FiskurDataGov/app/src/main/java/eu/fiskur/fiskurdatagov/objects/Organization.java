package eu.fiskur.fiskurdatagov.objects;

import java.io.Serializable;

/**
 * Created by Jonathan Fisher on 18/01/15.
 */
public class Organization implements Serializable {
    String description;
    String created;
    String title;
    String name;
    String revision_timestamp;
    boolean is_organization;
    String state;
    String image_url;
    String revision_id;
    String type;
    String id;
    String approval_status;

    public String getTitle(){
        return title;
    }
}
