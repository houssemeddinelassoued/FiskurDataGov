package eu.fiskur.fiskurdatagov.events;

import eu.fiskur.fiskurdatagov.responses.TagShowResponse;

/**
 * Created by Jonathan Fisher on 18/01/15.
 */
public class TagPackagesLoadedEvent {
    TagShowResponse response;

    public TagPackagesLoadedEvent(TagShowResponse response){
        this.response = response;
    }

    public TagShowResponse getResponse(){
        return response;
    }
}
