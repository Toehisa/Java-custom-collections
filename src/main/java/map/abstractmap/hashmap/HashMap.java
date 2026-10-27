package map.abstractmap.hashmap;

import java.util.AbstractMap;
import java.util.Set;

public class HashMap<K, V> extends AbstractMap<K, V> {
    private static final int extensionMultiplier = 2;
    private static final double loadFactor = 0.75;

    private HashNode<K, V>[] container = (HashNode<K, V>[]) new HashNode[16];

    @Override
    public V put(K key, V value) {
        int backet = getBacket(key);

        if (container[backet] == null) {
            container[backet] = new HashNode<>(key.hashCode(), key, value);
        } else {
            HashNode<K, V> current = container[backet];
            while (true){
                if (isValidContract(current, key)) {
                    return swapAndGet(current, value);
                }

                if (!isNextExist(current)) {
                    current.next = new HashNode<>(key.hashCode(), key, value);
                    break;
                }

                current = current.next;
            }

        }
        return null;
    }

    @Override
    public V get(Object key) {
        return super.get(key);
    }

    private boolean isValidContract(HashNode<K, V> current, K key) {
        return key.hashCode() == current.hash && key.equals(current.key);
    }

    private boolean isNextExist(HashNode<K, V> current) {
        return current.next != null;
    }

    private V swapAndGet(HashNode<K, V> current, V value) {
        V oldVal = current.value;
        current.value = value;
        return oldVal;
    }


    @Override
    public Set<Entry<K, V>> entrySet() {
        return Set.of();
    }


    private int getBacket(K key) {
        return key.hashCode() & (container.length - 1);
    }

    private static class HashNode<K, V> {
        final int hash;
        HashNode<K, V> next;

        K key;
        V value;

        HashNode(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }
    }
}
