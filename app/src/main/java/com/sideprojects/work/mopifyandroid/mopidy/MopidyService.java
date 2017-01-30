package com.sideprojects.work.mopifyandroid.mopidy;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by work on 1/28/17.
 */

public class MopidyService {

    // Urls
    public static final String BASE_URL = "http://192.168.1.114:6680/mopidy/";

    // Paths
    public static final String PATH_RPC = "rpc";

    // Fields
    public static final String FIELD_ID = "id";
    public static final String FIELD_JSON_RPC = "jsonrpc";
    public static final String FIELD_RESULT = "result";
    public static final String FIELD_ERROR = "error";

    // Browse Fields
    public static final String FIELD_MODEL = "__model__";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_URI = "uri";

    private static MopidyApi mInstance;

    public static MopidyApi get(){
        if(mInstance == null){
            mInstance = generate();
        }
        return mInstance;
    }

    public static void cleanUp(){
        mInstance = null;
    }

    private static MopidyApi generate(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory
                        .createWithScheduler(Schedulers.io()))
                .build();
        return retrofit.create(MopidyApi.class);
    }

    public interface MopidyApi{
        @POST(PATH_RPC)
        Observable<VersionResponse> getVersion(@Body RequestBody body);
        @POST(PATH_RPC)
        Observable<BrowseResponse> browse(@Body RequestBody body);
    }

    public static Observable<VersionResponse> getVersion(){
        String content = "{\n" +
                "  \"method\": \"core.get_version\",\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"params\": {},\n" +
                "  \"id\": 1\n" +
                "}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), content);
        return get().getVersion(body);
    }

    public static Observable<BrowseResponse> browse(@Nullable BrowseItem item){
        String content = "{\n" +
                "  \"method\": \"core.library.browse\",\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"params\": {\n" +
                "    \"uri\": " + (item != null ? "\"" + item.uri + "\"" : null) + "\n" +
                "  },\n" +
                "  \"id\": 1\n" +
                "}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), content);
        return get().browse(body);
    }

    /* * * * * * * * * * * * * * * * * * * *
     *
     *      Response Objects
     *
     * * * * * * * * * * * * * * * * * * * */
    public static class VersionResponse{
        @SerializedName(FIELD_JSON_RPC)
        public String jsonRPC;
        @SerializedName(FIELD_ID)
        public int id;
        @SerializedName(FIELD_RESULT)
        public String versionName;
    }

    public static class BrowseResponse{
        @SerializedName(FIELD_ID)
        public int id;
        @SerializedName(FIELD_RESULT)
        public List<BrowseItem> results;
    }

    public static class BrowseItem{
        @SerializedName(FIELD_MODEL)
        public String model;
        @SerializedName(FIELD_TYPE)
        public String type;
        @SerializedName(FIELD_NAME)
        public String name;
        @SerializedName(FIELD_URI)
        public String uri;
    }
}
