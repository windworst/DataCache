package datacache;

import java.util.ArrayList;
import java.util.List;

public final class DataCache<KEY,VALUE>{
    private List<ICacheAdapter> mCacheAdapterList = new ArrayList<ICacheAdapter>();

    public DataCache<KEY,VALUE> addCache(ICacheAdapter cacheAdapter) {
        if(cacheAdapter != null) {
            mCacheAdapterList.add(cacheAdapter);
        }
        return this;
    }

    /*
     put value to all cache
     */
    public void put(KEY key, VALUE value) {
        put(key,value, true);
    }
    public void put(KEY key,VALUE value, boolean flush) {
        for(ICacheAdapter<KEY,VALUE> cacheAdapter:mCacheAdapterList) {
            if(flush || (!cacheAdapter.has(key)) ) {
                cacheAdapter.put(key,value);
            }
        }
    }

    /*
     get cache from this and next cache, save cache in this cache( if this cache not found)
     */
    public VALUE get(KEY key) {
        VALUE value = null;
        for(ICacheAdapter<KEY,VALUE> cacheAdapter:mCacheAdapterList) {
            if( null != (value = cacheAdapter.get(key) ) ) {
                break;
            }
        }
        if(value != null) {
            put(key,value);
        }
        return value;
    }

    /*
     return true if found in this cache or next cache
     */
    public boolean has(KEY key) {
        for(ICacheAdapter<KEY,VALUE> cacheAdapter:mCacheAdapterList) {
            if(cacheAdapter.has(key)) {
                return true;
            }
        }
        return false;
    }
}
