package eu.fiskur.fiskurdatagov.objects;

import java.util.ArrayList;

/**
 * Created by primer on 18/01/15.
 */
public class Result {
    String display_name;
    String id;
    String name;
    ArrayList<Package> packages;

    public String getName(){
        return name;
    }

    public ArrayList<Package> getPackages(){
        return packages;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("name: " + name + "\n");
        sb.append("packages count: " + packages.size() + "\n");

        return sb.toString();
    }
}
