package storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
  private static final Map<String, String> store = new ConcurrentHashMap<>();

  public static void set(String key, String value) {
    store.put(key, value);
  }

  public static String get(String key) {
    return store.get(key);
  }
}
