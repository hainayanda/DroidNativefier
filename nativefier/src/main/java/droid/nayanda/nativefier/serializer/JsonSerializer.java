package droid.nayanda.nativefier.serializer;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by nayanda on 18/03/18.
 */

public class JsonSerializer<TJsonObj> extends StringSerializer<TJsonObj> {

    private final Class<TJsonObj> jsonObjClass;

    public JsonSerializer(Class<TJsonObj> jsonObjClass) {
        this.jsonObjClass = jsonObjClass;
    }


    @Override
    public String serializeToString(@NonNull TJsonObj obj) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(obj);
    }

    @Override
    public TJsonObj deserializeFromString(@NonNull String string) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(string, jsonObjClass);
    }
}
