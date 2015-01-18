package eu.fiskur.fiskurdatagov.events;

/**
 * Created by Jonathan Fisher on 17/01/15.
 */
public class ErrorEvent {

    private String error;

    public ErrorEvent(String error){
        this.error = error;
    }

    public String getError(){
        return error;
    }
}
