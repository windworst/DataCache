package datacache;

public interface ICacheAdapter<KEY,VALUE> {
    void put(KEY key, VALUE value);
    VALUE get(KEY key);
    boolean has(KEY key);
}
