package com.alick.learnsocketio.gson;

import android.text.TextUtils;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 功能: json解析类
 * 作者: 崔兴旺
 * 日期: 2019/4/9
 */
public class JsonUtils {
    private static Gson gson;
    private static Gson excludeGson;    //被GsonExclude注解的字段被排除(不参与json序列化与反序列化)

    public static class StringTypeAdapter extends TypeAdapter<String> {
        @Override
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }

        @Override
        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null) {
                // 在这里处理null改为空字符串
                writer.value("");
                return;
            }
            writer.value(value);
        }
    }

    public static class DoubleDefault0Adapter implements JsonSerializer<Double>, JsonDeserializer<Double> {
        @Override
        public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为double类型,如果后台返回""或者null,则返回0.00
                    return 0.00;
                }
            } catch (Exception ignore) {
            }
            try {
                return json.getAsDouble();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }

    public static class IntegerDefault0Adapter implements JsonSerializer<Integer>, JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为int类型,如果后台返回""或者null,则返回0
                    return 0;
                }
            } catch (Exception ignore) {
            }
            try {
                return json.getAsInt();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }


    public static class LongDefault0Adapter implements JsonSerializer<Long>, JsonDeserializer<Long> {
        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为long类型,如果后台返回""或者null,则返回0
                    return 0L;
                }
            } catch (Exception ignore) {
            }
            try {
                return json.getAsLong();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }


    public static class ListDefault0Adapter implements JsonSerializer<Collection<?>>, JsonDeserializer<Collection<?>> {
        @Override
        public Collection<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return null;
        }

        @Override
        public JsonElement serialize(Collection<?> objects, Type type, JsonSerializationContext jsonSerializationContext) {
            return null;
        }
    }

    public static class ListDefault0Adapter1 implements JsonSerializer<List<?>>, JsonDeserializer<List<?>> {
        @Override
        public List<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return null;
        }

        @Override
        public JsonElement serialize(List<?> objects, Type type, JsonSerializationContext jsonSerializationContext) {
            return null;
        }
    }

    public static class ListDefault0Adapter2 extends TypeAdapter<Collection<?>>{

        @Override
        public void write(JsonWriter jsonWriter, Collection<?> objects) throws IOException {

        }

        @Override
        public Collection<?> read(JsonReader jsonReader) throws IOException {
            return null;
        }
    }

    public static class ListDefault0Adapter3 extends TypeAdapter<ArrayList<?>>{

        @Override
        public void write(JsonWriter jsonWriter, ArrayList<?> objects) throws IOException {

        }

        @Override
        public ArrayList<?> read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return new ArrayList<>();
            }
            ArrayList list=new ArrayList<>();
            reader.beginArray();
            String code = reader.nextString();
            String description = reader.nextString();

            reader.endArray();

            return null;
        }
    }


    private static class MyTypeAdapterFactory implements TypeAdapterFactory{
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            if(typeToken.getRawType() == ArrayList.class){
                return (TypeAdapter<T>) new ListDefault0Adapter3();
            }
            return null;
        }
    }


    /**
     * @param isEnableGsonExclude 是否启用[被GsonExclude注解的字段不参与序列化、反序列化]功能,默认都参与
     * @return Gson
     */
    public static Gson getGson(boolean isEnableGsonExclude) {
        if (isEnableGsonExclude) {
            return createEnableExcludeGson();
        } else {
            return createNormalGson();
        }
    }

    public static Gson getGson() {
        return getGson(false);
    }

    private static Gson createNormalGson() {
        if (gson == null) {
            gson = newNormalGson(null);
        }
        return gson;
    }

    public static Gson newNormalGson(Map<Type, Object> typeAdapterMap) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();

        if (typeAdapterMap != null) {
            for (Map.Entry<Type, Object> typeObjectEntry : typeAdapterMap.entrySet()) {
                gsonBuilder.registerTypeAdapter(typeObjectEntry.getKey(), typeObjectEntry.getValue());
            }
        }

        //注册自定义String的适配器
        gsonBuilder.registerTypeAdapter(String.class, new StringTypeAdapter())
                .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(Integer.class, new IntegerDefault0Adapter())
                .registerTypeAdapter(int.class, new IntegerDefault0Adapter())
                .registerTypeAdapter(Long.class, new LongDefault0Adapter())
                .registerTypeAdapter(long.class, new LongDefault0Adapter())
                .registerTypeAdapter(List.class, new ListDefault0Adapter())
                .registerTypeAdapter(Collection.class, new ListDefault0Adapter2())
                .registerTypeAdapter(ArrayList.class, new ListDefault0Adapter3())
                .registerTypeAdapterFactory(new MyTypeAdapterFactory())


        ;
        return gsonBuilder.create();
    }

    private static Gson createEnableExcludeGson() {
        if (excludeGson == null) {
            excludeGson = newEnableExcludeGson(null);
        }
        return excludeGson;
    }

    public static Gson newEnableExcludeGson(Map<Type, Object> typeAdapterMap) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        //第一种方案:使用自定义的排除策略
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getAnnotation(GsonExclude.class) != null;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });

        //第二种方案是,采用Expose注解,但想要参与序列化或反序列化的字段,必须用Expose注解,如果不加注解,就相当于放弃序列化和反序列化
//            gsonBuilder.excludeFieldsWithoutExposeAnnotation();

        if (typeAdapterMap != null) {
            for (Map.Entry<Type, Object> typeObjectEntry : typeAdapterMap.entrySet()) {
                gsonBuilder.registerTypeAdapter(typeObjectEntry.getKey(), typeObjectEntry.getValue());
            }
        }

        //注册自定义String的适配器
        gsonBuilder.registerTypeAdapter(String.class, new StringTypeAdapter())
                .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(double.class, new DoubleDefault0Adapter())
                .registerTypeAdapter(Integer.class, new IntegerDefault0Adapter())
                .registerTypeAdapter(int.class, new IntegerDefault0Adapter())
                .registerTypeAdapter(Long.class, new LongDefault0Adapter())
                .registerTypeAdapter(long.class, new LongDefault0Adapter())
                .registerTypeAdapter(List.class, new ListDefault0Adapter())
                .registerTypeAdapter(Collection.class, new ListDefault0Adapter2())
                .registerTypeAdapter(ArrayList.class, new ListDefault0Adapter3())
        ;
        return gsonBuilder.create();
    }

    /**
     * 深拷贝对象
     *
     * @param obj 要深拷贝的对象
     * @param <T> 泛型
     * @return T
     * @throws JSONException 非法json格式异常
     */
    public static <T> T deepCopy(T obj, boolean isEnableGsonExclude) throws JSONException {
        return (T) (JsonUtils.parseJson2Bean(JsonUtils.parseBean2json(obj, isEnableGsonExclude), obj.getClass(), isEnableGsonExclude));
    }

    public static Object parseMapOrIterable2JSON(Object object) throws JSONException {
        if (object instanceof Map) {
            JSONObject json = new JSONObject();
            Map map = (Map) object;
            for (Object key : map.keySet()) {
                json.put(key.toString(), parseMapOrIterable2JSON(map.get(key)));
            }
            return json;
        } else if (object instanceof Iterable) {
            JSONArray json = new JSONArray();
            for (Object value : ((Iterable) object)) {
                json.put(value);
            }
            return json;
        } else {
            return object;
        }
    }

    public static String parseBean2json(Object obj) {
        return parseBean2json(obj, false);
    }

    /**
     * @param obj                 要序列化的实例
     * @param isEnableGsonExclude 是否启用[被GsonExclude注解的字段不参与序列化、反序列化]功能,默认都参与
     * @return 序列化结果
     */
    public static String parseBean2json(Object obj, boolean isEnableGsonExclude) {
        return getGson(isEnableGsonExclude).toJson(obj);
    }

    public static boolean isEmptyObject(JSONObject object) {
        return object.names() == null;
    }

    public static Map<String, Object> getMap(JSONObject object, String key) throws
            JSONException {
        return toMap(object.getJSONObject(key));
    }

    public static Map<String, Object> toMap(JSONObject object)
            throws JSONException {

        Map<String, Object> map = new HashMap<>();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)));
        }
        return map;
    }

    public static Map<String, String> parseMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap<>();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)).toString());
        }
        return map;
    }

    public static List toList(JSONArray array) throws JSONException {
        List list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    private static Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return toMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }

    public static <
            T> List<T> parseRootJson2List(String json, Class<T> clazz, String listKey) throws
            JSONException {
        return parseJson2List(new JSONObject(json).getJSONObject("data").getJSONArray(listKey).toString(), clazz);
    }

    public static <T> List<T> parseRootJson2List(String json, Class<T> clazz) throws
            JSONException {
        return parseJson2List(new JSONObject(json).getJSONArray("data").toString(), clazz);
    }

    public static <T> List<T> parseJson2List(String json, Class<T> clazz) throws JSONException {
        return parseJson2List(json, clazz, false);
    }

    /**
     * @param isEnableGsonExclude 是否启用[被GsonExclude注解的字段不参与序列化、反序列化]功能,默认都参与
     * @return 反序列化结果
     */
    public static <T> List<T> parseJson2List(String json, Class<T> clazz, boolean isEnableGsonExclude) throws JSONException {
        List<T> list;
        try {
            list = new ArrayList<>();
            if (TextUtils.isEmpty(json)) {
                return list;
            }

            JsonArray array = JsonParser.parseString(json).getAsJsonArray();
            for (JsonElement element : array) {
                list.add(getGson(isEnableGsonExclude).fromJson(element, clazz));
            }
        } catch (JsonSyntaxException e) {
            throw new JSONException(e.getMessage());
        }
        return list;
    }

    /**
     * @param isEnableGsonExclude 是否启用[被GsonExclude注解的字段不参与序列化、反序列化]功能,默认都参与
     * @return 反序列化结果
     */
    public static <T> List<T> parseJson2List(String json, Type type, boolean isEnableGsonExclude) throws JSONException {
        try {
            List<T> list = new ArrayList<>();
            if (TextUtils.isEmpty(json)) {
                return list;
            }

            JsonArray array = JsonParser.parseString(json).getAsJsonArray();
            for (JsonElement element : array) {
                T t = getGson(isEnableGsonExclude).fromJson(element, type);
                list.add(t);
            }
            return list;
        } catch (JsonSyntaxException e) {
            throw new JSONException(e.getMessage());
        }
    }

    public static <T> List<T> parseJson2List(String json, Type type) throws JSONException {
        return parseJson2List(json, type, false);
    }

    /**
     * @param isEnableGsonExclude 是否启用[被GsonExclude注解的字段不参与序列化、反序列化]功能,默认都参与
     * @return 反序列化结果
     */
    public static <T> T parseJson2Bean(String json, Class<T> clazz, boolean isEnableGsonExclude) throws JSONException {
        try {
            return getGson(isEnableGsonExclude).fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            throw new JSONException(e.getMessage());
        }
    }

    public static <T> T parseJson2Bean(String json, Class<T> clazz) throws JSONException {
        return parseJson2Bean(json, clazz, false);
    }

    /**
     * @param isEnableGsonExclude 是否启用[被GsonExclude注解的字段不参与序列化、反序列化]功能,默认都参与
     * @return 反序列化结果
     */
    public static <T> T parseRootJson2Bean(String json, Class<T> clazz, boolean isEnableGsonExclude) throws JSONException {
        try {
            return getGson(isEnableGsonExclude).fromJson(new JSONObject(json).getJSONObject("data").toString(), clazz);
        } catch (JsonSyntaxException | JSONException e) {
            throw new JSONException(e.getMessage());
        }
    }

    public static <T> T parseRootJson2Bean(String json, Class<T> clazz) throws JSONException {
        return parseRootJson2Bean(json, clazz, false);
    }

    /**
     * @param isEnableGsonExclude 是否启用[被GsonExclude注解的字段不参与序列化、反序列化]功能,默认都参与
     * @return 反序列化结果
     */
    public static <T> T parseRootJson2Bean(String json, Class<T> clazz, String obj_key, boolean isEnableGsonExclude) throws JSONException {
        try {
            return getGson(isEnableGsonExclude).fromJson(new JSONObject(json).getJSONObject("data").getJSONObject(obj_key).toString(), clazz);
        } catch (JsonSyntaxException | JSONException e) {
            throw new JSONException(e.getMessage());
        }
    }

    public static <T> T parseRootJson2Bean(String json, Class<T> clazz, String obj_key) throws JSONException {
        return parseRootJson2Bean(json, clazz, obj_key, false);
    }

    /**
     * @param isEnableGsonExclude 是否启用[被GsonExclude注解的字段不参与序列化、反序列化]功能,默认都参与
     * @return 反序列化结果
     */
    public static <T> T parseJson2Bean(String json, Type type, boolean isEnableGsonExclude) throws JSONException {
        try {
            return getGson(isEnableGsonExclude).fromJson(json, type);
        } catch (JsonSyntaxException e) {
            throw new JSONException(e.getMessage());
        }
    }

    public static <T> T parseJson2Bean(String json, Type type) throws JSONException {
        return parseJson2Bean(json, type, false);
    }

    /**
     * @param isEnableGsonExclude 是否启用[被GsonExclude注解的字段不参与序列化、反序列化]功能,默认都参与
     * @return json字符串
     */
    public static <W> String parseMap2String(Map<String, W> map, boolean isEnableGsonExclude) throws JSONException {
        try {
            return getGson(isEnableGsonExclude).toJson(map);
        } catch (Exception e) {
            throw new JSONException(e.getMessage());
        }
    }

    public static <W> String parseMap2String(Map<String, W> map) throws JSONException {
        return parseMap2String(map, false);
    }

    /**
     * @param isEnableGsonExclude 是否启用[被GsonExclude注解的字段不参与序列化、反序列化]功能,默认都参与
     * @return T
     */
    public static <T> T parseMap2Bean(Map<String, ?> map, Class<T> clazz, boolean isEnableGsonExclude) throws JSONException {
        try {
            return getGson().fromJson(getGson().toJson(map), clazz);
        } catch (JsonSyntaxException e) {
            throw new JSONException(e.getMessage());
        }
    }

    public static <T> T parseMap2Bean(Map<String, ?> map, Class<T> clazz) throws JSONException {
        return parseMap2Bean(map, clazz, false);
    }

    /**
     * 用来兼容Android4.4以下版本的remove方法
     *
     * @param jsonArray JSONArray
     * @param index     index
     * @return JSONArray
     */
    public static JSONArray removeCompatibilityKITKAT(JSONArray jsonArray, int index) throws
            JSONException {
        JSONArray mJsonArray;
        try {
            mJsonArray = new JSONArray();
            if (index < 0)
                return mJsonArray;
            if (index > jsonArray.length())
                return mJsonArray;
            for (int i = 0; i < jsonArray.length(); i++) {
                if (i != index) {
                    mJsonArray.put(jsonArray.getJSONObject(i));
                }
            }
        } catch (JSONException e) {
            throw new JSONException(e.getMessage());
        }
        return mJsonArray;
    }
}
