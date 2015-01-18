package eu.fiskur.fiskurdatagov;

import retrofit.Callback;
import retrofit.http.GET;

public interface Api {
    @GET("/tag_list")
    void tagList(Callback<TagListResponse> callback);
}
