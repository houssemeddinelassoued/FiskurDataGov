package eu.fiskur.fiskurdatagov;

import eu.fiskur.fiskurdatagov.responses.PackageSearchResponse;
import eu.fiskur.fiskurdatagov.responses.TagListResponse;
import eu.fiskur.fiskurdatagov.responses.TagShowResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface Api {
    //Lists all tags for content, over 15000 entries
    @GET("/tag_list")
    void tagList(Callback<TagListResponse> callback);

    //Get all resources for a given tag
    @GET("/tag_show")
    void tagShow(@Query("id") String tag, Callback<TagShowResponse> callback);

    //Search for all packages matching a keyword
    @GET("/package_search")
    void searchPackages(@Query("q") String query, Callback<PackageSearchResponse> callback);
}
