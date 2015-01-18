package eu.fiskur.fiskurdatagov;

import eu.fiskur.fiskurdatagov.objects.Result;

/**
 * Created by Jonathan Fisher on 18/01/15.
 */
public class TagShowResponse {
    String help;
    boolean success;
    Result result;


    public String getHelp() {
        return help;
    }

    public boolean isSuccess() {
        return success;
    }

    public Result getResult() {
        return result;
    }

}
