package eu.fiskur.fiskurdatagov;

import java.util.ArrayList;

/**
 * Created by Jonathan Fisher on 17/01/15.
 */
public class TagListResponse {
    private String help;
    private boolean success;
    private ArrayList<String> result;

    public String getHelp() {
        return help;
    }

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<String> getResult() {
        return result;
    }

    @Override
    public String toString() {
        if(result != null){
            StringBuilder sb = new StringBuilder();
            for(String tag : result){
                sb.append(tag + "\n");
            }
            return sb.toString();
        }else{
            return "No result";
        }
    }
}
