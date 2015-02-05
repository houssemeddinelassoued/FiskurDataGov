package eu.fiskur.fiskurdatagov.responses;

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

    public void setResult(ArrayList<String> result){
        this.result = result;
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
