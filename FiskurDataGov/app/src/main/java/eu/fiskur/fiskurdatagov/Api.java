package eu.fiskur.fiskurdatagov;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface Api {
    @GET("/tag_list")
    void tagList(Callback<TagListResponse> callback);

    @GET("/tag_show")
    void tagShow(@Query("id") String tag, Callback<TagShowResponse> callback);
}
