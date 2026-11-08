package storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
  private static final Map<String, Entry> store = new ConcurrentHashMap<>();

  private static class Entry {
    final String value;
    final long expiresAt;

    Entry(String value, long expiresAt) {
      this.value = value;
      this.expiresAt = expiresAt;
    }
  }

  public static void set(String key, String value) {
    store.put(key, new Entry(value, 0));
  }

  public static void set(String key, String value, long ttMillis) {
    long expiresAt = System.currentTimeMillis() + ttMillis;
    store.put(key, new Entry(value, expiresAt));
  }

  public static String get(String key) {
    Entry entry = store.get(key);

    if (entry == null) {
      return null;
    }

    if (entry.expiresAt != 0 && System.currentTimeMillis() >= entry.expiresAt) {
      store.remove(key);
      return null;
    }
    return entry.value;
  }
}
