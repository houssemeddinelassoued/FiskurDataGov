package eu.fiskur.fiskurdatagov.events;

import eu.fiskur.fiskurdatagov.TagListResponse;

/**
 * Created by primer on 17/01/15.
 */
public class TagsLoadedEvent {
    private TagListResponse response;

    public TagsLoadedEvent(TagListResponse response){
        this.response = response;
    }

    public TagListResponse getResponse(){
        return response;
    }
}
