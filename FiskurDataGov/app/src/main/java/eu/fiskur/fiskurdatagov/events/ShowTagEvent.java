package eu.fiskur.fiskurdatagov.events;

/**
 * Created by Jonathan Fisher on 18/01/15.
 */
public class ShowTagEvent {

    String tag;

    public ShowTagEvent(String tag){
        this.tag = tag;
    }

    public String getTag(){
        return tag;
    }
}
