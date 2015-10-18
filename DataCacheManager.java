package datacache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import datacache.adapter.DiskIruCacheAdapter;
import datacache.adapter.LruCacheAdapter;
import datacache.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataCacheManager {
    private static DataCache<String, Bitmap> getLruBitmapCache(LruCache<String,Bitmap> lruCache) {
        return new DataCache<String,Bitmap>(new LruCacheAdapter<String,Bitmap>(lruCache));
    }
    private static DataCache<String, Bitmap> getDiskLruBitmapCache(DiskLruCache diskLruCache) {
        return new DataCache<String,Bitmap>(new DiskIruCacheAdapter<String,Bitmap>(diskLruCache, new DiskIruCacheAdapter.ValueDataSaver<Bitmap>() {
            @Override
            public void writeTo(OutputStream outputStream, Bitmap data) {
                data.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            }

            @Override
            public Bitmap readFrom(InputStream inputStream) {
                return BitmapFactory.decodeStream(inputStream);
            }
        }));
    }

    public static DataCache<String, Bitmap> getDoubleBitmapCache(LruCache<String,Bitmap> lruCache, DiskLruCache diskLruCache) {
        DataCache<String, Bitmap> lruDataCache = getLruBitmapCache(lruCache);
        DataCache<String, Bitmap> diskLruDataCache = getDiskLruBitmapCache(diskLruCache);
        return lruDataCache.setNextCache(diskLruDataCache);
    }

    public static DataCache<String, Bitmap> getDoubleBitmapCache(File directory, long memoryCacheSize, long diskCacheSize) {
        LruCache<String,Bitmap> lruCache = new LruCache<String,Bitmap>((int) memoryCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        DiskLruCache diskLruCache = null;
        try {
            diskLruCache = DiskLruCache.open(directory, 1, 1, diskCacheSize);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return getDoubleBitmapCache(lruCache, diskLruCache);
    }

    private static DataCache<String, byte[]> getLruCache(LruCache<String,byte[]> lruCache) {
        return new DataCache<String,byte[]>(new LruCacheAdapter<String,byte[]>(lruCache));
    }
    private static DataCache<String, byte[]> getDiskLruCache(DiskLruCache diskLruCache) {
        return new DataCache<String,byte[]>(new DiskIruCacheAdapter<String,byte[]>(diskLruCache, new DiskIruCacheAdapter.ValueDataSaver<byte[]>() {
            @Override
            public void writeTo(OutputStream outputStream, byte[] data) {
                try {
                    outputStream.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public byte[] readFrom(InputStream inputStream) {
                try {
                    byte[] data = new byte[inputStream.available()];
                    inputStream.read(data);
                    return data;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }));
    }

    public static DataCache<String, byte[]> getDoubleCache(LruCache<String,byte[]> lruCache, DiskLruCache diskLruCache) {
        DataCache<String, byte[]> lruDataCache = getLruCache(lruCache);
        DataCache<String, byte[]> diskLruDataCache = getDiskLruCache(diskLruCache);
        return lruDataCache.setNextCache(diskLruDataCache);
    }

    public static DataCache<String, byte[]> getDoubleCache(File directory, long memoryCacheSize, long diskCacheSize) {
        LruCache<String,byte[]> lruCache = new LruCache<String,byte[]>((int) memoryCacheSize) {
            @Override
            protected int sizeOf(String key, byte[] value) {
                return value.length;
            }
        };
        DiskLruCache diskLruCache = null;
        try {
            diskLruCache = DiskLruCache.open(directory, 1, 1, diskCacheSize);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return getDoubleCache(lruCache, diskLruCache);
    }
}
