package datacache.adapter;

import android.util.LruCache;

import datacache.ICacheAdapter;

public class LruCacheAdapter<KEY,VALUE> implements ICacheAdapter<KEY,VALUE> {

    private final LruCache<KEY, VALUE> mLruCache;

    public LruCacheAdapter(LruCache<KEY,VALUE> lruCache) {
        if( ( mLruCache = lruCache) == null ) {
            throw new RuntimeException("LruCacheAdapter(): lruCache cannot null");
        }
    }
    @Override
    public void put(KEY key, VALUE value) {
        mLruCache.put(key,value);
    }

    @Override
    public VALUE get(KEY key) {
        return mLruCache.get(key);
    }

    @Override
    public boolean has(KEY key) {
        return mLruCache.get(key) != null;
    }
}
