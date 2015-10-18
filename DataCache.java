package datacache;

public final class DataCache<KEY,VALUE>{
    private final ICacheAdapter<KEY,VALUE> mCacheAdapter;
    private DataCache<KEY, VALUE> mNextDataCache = null;

    public DataCache(ICacheAdapter<KEY, VALUE> cache) {
        if( (mCacheAdapter = cache) == null) {
            throw new RuntimeException("DataCache(): cache cannot null");
        }
    }

    public DataCache<KEY,VALUE> setNextCache(DataCache<KEY,VALUE> dataCache) {
        mNextDataCache = dataCache;
        return this;
    }

    public DataCache<KEY, VALUE> getNextCache() {
        return mNextDataCache;
    }

    /*
     put value to all cache
     */
    public void put(KEY key,VALUE value) {
        mCacheAdapter.put(key, value);
        if(getNextCache() != null ) {
            getNextCache().put(key, value);
        }
    }

    /*
     get cache from this and next cache, save cache in this cache( if this cache not found)
     */
    public VALUE get(KEY key) {
        VALUE value = mCacheAdapter.get(key);
        if(value == null && getNextCache() != null ) {
            value = getNextCache().get(key);
            if(value != null) {
                mCacheAdapter.put(key,value);
            }
        }
        return value;
    }

    /*
     return true if found in this cache or next cache
     */
    public boolean has(KEY key) {
        return mCacheAdapter.has(key) || ( getNextCache() != null && getNextCache().has(key) );
    }
}
