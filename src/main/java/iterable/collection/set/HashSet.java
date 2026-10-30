package iterable.collection.set;

import map.abstractmap.HashMap;

import java.util.AbstractSet;
import java.util.Iterator;

public class HashSet<K> extends AbstractSet<K> {
    private static final Object CHOPIK = new Object();

    HashMap<K, Object> hashMap = new HashMap<>();

    @Override
    public boolean add(K k) {
        return hashMap.put(k, CHOPIK) == null;
    }

    @Override
    public boolean remove(Object o) {
        return hashMap.remove(o) != null;
    }

    @Override
    public boolean contains(Object o) {
        return hashMap.containsKey(o);
    }

    @Override
    public Iterator<K> iterator() {
        return hashMap.keySet().iterator();
    }

    @Override
    public int size() {
        return hashMap.size();
    }

    @Override
    public void clear() {
        hashMap.clear();
    }
}
