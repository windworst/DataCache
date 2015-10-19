package datacache.adapter;

import datacache.ICacheAdapter;
import datacache.Util;
import datacache.disklrucache.DiskLruCache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DiskIruCacheAdapter<KEY,VALUE> implements ICacheAdapter<KEY,VALUE> {
    private final DiskLruCache mDiskIruCache;
    private final ValueDataSaver<VALUE> mValueDataSaver;

    public interface ValueDataSaver<VALUE> {
        void writeTo(OutputStream outputStream, VALUE data);
        VALUE readFrom(InputStream inputStream);
    }

    public DiskIruCacheAdapter(DiskLruCache diskIruCache, ValueDataSaver<VALUE> saver) {
        if( (mDiskIruCache = diskIruCache) == null || (mValueDataSaver = saver) == null ) {
            throw new RuntimeException("DiskIruCacheAdapter(): diskIruCache cannot null");
        }
    }
    @Override
    public void put(KEY key, VALUE data) {
        String s = Util.getMD5(key.toString());
        try {
            DiskLruCache.Editor editor = mDiskIruCache.edit(s);
            if(editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                mValueDataSaver.writeTo(outputStream,data);
            }
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public VALUE get(KEY key) {
        String s = Util.getMD5(key.toString());
        try {
            DiskLruCache.Snapshot snapshot = mDiskIruCache.get(s);
            if(snapshot != null) {
                InputStream inputStream = snapshot.getInputStream(0);
                VALUE value =  mValueDataSaver.readFrom(inputStream);
                snapshot.close();
                return value;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean has(KEY key) {
        String s = Util.getMD5(key.toString());
        try {
            return mDiskIruCache.get(s) != null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
