package com.example.umatchapp.api;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface UMatchApiService {
    @POST("ui/projects/{project_id}/locations/us-central1/endpoints/{endpoint_id}:predict")
    Call<UMatchApiResponse> predict(
            @Header("Authorization") String authHeader,
            @Path("project_id") String projectId,
            @Path("endpoint_id") String endpointId,
            @Body RequestBody requestBody
    );
}
