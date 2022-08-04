package kr.co.sorizava.asrplayerKt.network

import android.net.Uri
import android.os.Bundle
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import kr.co.sorizava.asrplayerKt.network.DateDeserializer
import kr.co.sorizava.asrplayerKt.network.DateSerializer
import kr.co.sorizava.asrplayerKt.network.UriDeserializer
import kr.co.sorizava.asrplayerKt.network.UriSerializer
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*




/*

    public String toJson()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Uri.class, new UriSerializer())
                .registerTypeAdapter(Date.class, new DateSerializer())
                .create();
        return gson.toJson(this);
    }

    public static String toJson(Object T)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Uri.class, new UriSerializer())
                .registerTypeAdapter(Date.class, new DateSerializer())
                .create();
        return gson.toJson(T);
    }

    public static String toArrayJson(ArrayList<?> T)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Uri.class, new UriSerializer())
                .registerTypeAdapter(Date.class, new DateSerializer())
                .create();
        return gson.toJson(T);
    }

    public static Object fromArrayJson(Type type, String json)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Uri.class, new UriDeserializer())
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();

        return gson.fromJson(json, type);
    }

    public static Object fromJSon(Class T, String json)
    {
        try {
            Gson gson = new GsonBuilder().serializeNulls()
                    .registerTypeAdapter(Uri.class, new UriDeserializer())
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .create();
            return gson.fromJson(json, T);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object fromJSon(Type T, String json)
    {
        try {
            Gson gson = new GsonBuilder().serializeNulls()
                    .registerTypeAdapter(Uri.class, new UriDeserializer())
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .create();
            return gson.fromJson(json, T);
        } catch (Exception e) {
            return null;
        }
    }

    public static Bundle toBundle(String json)
    {
        Bundle savedBundle = new Bundle();
        savedBundle.putString("toBundle",json);
        return savedBundle;
    }

    public static Map<String, Object> toMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if(obj == null)
            return map;
        try {
            for(Field f: obj.getClass().getDeclaredFields()) {
                if(f.isAnnotationPresent(Expose.class)) {
                    f.setAccessible(true);
                    if (f.get(obj) != null)
                        map.put(f.getName(), f.get(obj));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static <T> T objectFromHashMap(Object hashMap, Class<T> tClass) {
        String jsonString = toJson(hashMap);
        return (T) fromJSon(tClass, jsonString);
    }

    public static String getJsonString(JSONObject json, String key){
        String outValue = null;
        // key가 존재하는지 체크한다.
        if(json.has(key)){
            try {
                outValue = json.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return outValue;
    }
 */
class GsonManager {

    companion object
    {

        /*

        public String toJson()
        {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                    .registerTypeAdapter(Uri.class, new UriSerializer())
                    .registerTypeAdapter(Date.class, new DateSerializer())
                    .create();
            return gson.toJson(this);
        }

        public static String toJson(Object T)
        {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                    .registerTypeAdapter(Uri.class, new UriSerializer())
                    .registerTypeAdapter(Date.class, new DateSerializer())
                    .create();
            return gson.toJson(T);
        }
         */
        fun toJson(): String? {
            val gson = GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Uri::class.java, UriSerializer())
                .registerTypeAdapter(Date::class.java, DateSerializer())
                .create()
            return gson.toJson(this)
        }

        fun toJson(T: Any?): String {
            val gson = GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Uri::class.java, UriSerializer())
                .registerTypeAdapter(Date::class.java, DateSerializer())
                .create()
            return gson.toJson(T)
        }
        /*
            public static String toArrayJson(ArrayList<?> T)
            {
                Gson gson = new GsonBuilder().setPrettyPrinting()
                        .registerTypeAdapter(Uri.class, new UriSerializer())
                        .registerTypeAdapter(Date.class, new DateSerializer())
                        .create();
                return gson.toJson(T);
            }

            public static Object fromArrayJson(Type type, String json)
            {
                Gson gson = new GsonBuilder().setPrettyPrinting()
                        .registerTypeAdapter(Uri.class, new UriDeserializer())
                        .registerTypeAdapter(Date.class, new DateDeserializer())
                        .create();

                return gson.fromJson(json, type);
            }
         */
        fun toArrayJson(T: ArrayList<*>?): String? {
            val gson = GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Uri::class.java, UriSerializer())
                .registerTypeAdapter(Date::class.java, DateSerializer())
                .create()
            return gson.toJson(T)
        }

        fun fromArrayJson(type: Type?, json: String?): Any? {
            val gson = GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Uri::class.java, UriDeserializer())
                .registerTypeAdapter(Date::class.java, DateDeserializer())
                .create()
            return gson.fromJson(json, type)
        }

        /*

        public static Object fromJSon(Class T, String json)
        {
            try {
                Gson gson = new GsonBuilder().serializeNulls()
                        .registerTypeAdapter(Uri.class, new UriDeserializer())
                        .registerTypeAdapter(Date.class, new DateDeserializer())
                        .create();
                return gson.fromJson(json, T);
            } catch (Exception e) {
                return null;
            }
        }

        public static Object fromJSon(Type T, String json)
        {
            try {
                Gson gson = new GsonBuilder().serializeNulls()
                        .registerTypeAdapter(Uri.class, new UriDeserializer())
                        .registerTypeAdapter(Date.class, new DateDeserializer())
                        .create();
                return gson.fromJson(json, T);
            } catch (Exception e) {
                return null;
            }
        }
         */
        fun fromJSon(T: Class<*>?, json: String?): Any? {
            return try {
                val gson = GsonBuilder().serializeNulls()
                    .registerTypeAdapter(Uri::class.java, UriDeserializer())
                    .registerTypeAdapter(Date::class.java, DateDeserializer())
                    .create()
                gson.fromJson<Any>(json, T)
            } catch (e: Exception) {
                null
            }
        }

        fun fromJSon(T: Type?, json: String?): Any? {
            return try {
                val gson = GsonBuilder().serializeNulls()
                    .registerTypeAdapter(Uri::class.java, UriDeserializer())
                    .registerTypeAdapter(Date::class.java, DateDeserializer())
                    .create()
                gson.fromJson<Any>(json, T)
            } catch (e: Exception) {
                null
            }
        }


        /*

        public static Bundle toBundle(String json)
        {
            Bundle savedBundle = new Bundle();
            savedBundle.putString("toBundle",json);
            return savedBundle;
        }

        public static Map<String, Object> toMap(Object obj) {
            Map<String, Object> map = new HashMap<>();
            if(obj == null)
                return map;
            try {
                for(Field f: obj.getClass().getDeclaredFields()) {
                    if(f.isAnnotationPresent(Expose.class)) {
                        f.setAccessible(true);
                        if (f.get(obj) != null)
                            map.put(f.getName(), f.get(obj));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return map;
        }

         */
        fun toBundle(json: String?): Bundle? {
            val savedBundle = Bundle()
            savedBundle.putString("toBundle", json)
            return savedBundle
        }

        fun toMap(obj: Any?): Map<String, Any>? {
            val map: MutableMap<String, Any> = HashMap()
            if (obj == null) return map
            try {
                for (f in obj.javaClass.declaredFields) {
                    if (f.isAnnotationPresent(Expose::class.java)) {
                        f.isAccessible = true
                        if (f[obj] != null) map[f.name] = f[obj]
                    }
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
            return map
        }

        /*

        public static <T> T objectFromHashMap(Object hashMap, Class<T> tClass) {
            String jsonString = toJson(hashMap);
            return (T) fromJSon(tClass, jsonString);
        }

        public static String getJsonString(JSONObject json, String key){
            String outValue = null;
            // key가 존재하는지 체크한다.
            if(json.has(key)){
                try {
                    outValue = json.getString(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return outValue;
        }
         */
        fun <T> objectFromHashMap(hashMap: Any?, tClass: Class<T>?): T? {
            val jsonString = toJson(hashMap)
            return fromJSon(tClass, jsonString) as T?
        }

        fun getJsonString(json: JSONObject, key: String?): String? {
            var outValue: String? = null
            // key가 존재하는지 체크한다.
            if (json.has(key)) {
                try {
                    outValue = json.getString(key)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return outValue
        }
    }
}