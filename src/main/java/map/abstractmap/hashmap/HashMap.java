package map.abstractmap.hashmap;

import java.util.*;

public class HashMap<K, V> extends AbstractMap<K, V> {
    private static final int extensionMultiplier = 2;
    private static final double loadFactor = 0.75;

    private int size = 0;
    private HashNode<K, V>[] container = (HashNode<K, V>[]) new HashNode[16];

    @Override
    public V put(K key, V value) {
        onEmptyKeyException(key);

        int hash = hash(key);
        int bucket = getBucket(hash, container);

        if (isEmptyBucket(bucket)) {
            incrementSize();
            container[bucket] = new HashNode<>(hash, key, value);
        } else {
            HashNode<K, V> current = container[bucket];
            while (true){
                if (isValidContract(current, key, hash)) {
                    return swapAndGet(current, value);
                }

                if (!isNextExist(current)) {
                    incrementSize();
                    current.next = new HashNode<>(hash, key, value);
                    break;
                }

                current = current.next;
            }
        }
        return null;
    }

    @Override
    public V get(Object key) throws NullPointerException {
        onEmptyKeyException((K)key);

        int hash = hash(key);
        int bucket = getBucket(hash, container);

        HashNode<K, V> current = container[bucket];

        while (current != null) {
            if (isValidContract(current, (K)key, hash)) return current.value;
            current = current.next;
        }

        return null;
    }

    @Override
    public V remove(Object key) {
        if (Objects.isNull(key)) return null;

        int hash = hash(key);
        int bucket = getBucket(hash, container);

        HashNode<K, V> prev = null;
        HashNode<K, V> current = container[bucket];

        V removed = null;

        while (isCurrentExist(current)) {
            if (isValidContract(current, (K)key, hash)) {
                removed = current.value;
                decrementSize();

                if (isPreviousNotExist(prev)) {
                    container[bucket] = current.next;
                } else {
                    prev.next = current.next;
                }
                return removed;
            }
            prev = current;
            current = current.next;
        }

        return removed;
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (Objects.isNull(key)) return false;

        int hash = hash(key);
        int bucket = getBucket(hash, container);

        HashNode<K, V> prev = null;
        HashNode<K, V> current = container[bucket];

        while (isCurrentExist(current)) {
            if (isValidContract(current, (K)key, hash) && isValidValue(current, (V)value)) {
                if (isPreviousNotExist(prev)) {
                    container[bucket] = current.next;
                } else {
                    prev.next = current.next;
                }
                decrementSize();
                return true;
            }
            prev = current;
            current = current.next;
        }

        return false;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new HashMapIterator();
            }

            @Override
            public int size() {
                return HashMap.this.size;
            }
        };
    }

    private void incrementSize() {
        size++;
    }

    private void decrementSize() {
        size--;
    }

    private int hash(Object key) {
        //Короче суть в том что пока размер массива маленький у нас обрезается часть хеша, условно как если бы мы инту присваивали дабл.
        //Ну а данный шизометод нужен чтобы при маленьком размере размазать элементы по бакетам эффективнее.
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    private int getBucket(int hash, HashNode<K, V>[] container) {
        return hash & (container.length - 1);
    }

    private boolean isEmptyBucket(int bucket) {
        return Objects.isNull(container[bucket]);
    }

    private void onEmptyKeyException(K key) throws NullPointerException {
        //Нулл разрешен в стандартной мапе, но по моей логике это архитектурная ошибка.
        if (Objects.isNull(key)) throw new NullPointerException("Ключ не может быть пустым");
    }

    private boolean isPreviousNotExist(HashNode<K, V> prev) {
        return Objects.isNull(prev);
    }

    private boolean isCurrentExist(HashNode<K, V> current) {
        return Objects.nonNull(current);
    }

    private boolean isNextExist(HashNode<K, V> current) {
        return Objects.nonNull(current.next);
    }

    private boolean isValidContract(HashNode<K, V> current, K key, int hash) {
        return hash == current.hash && key.equals(current.key);
    }

    private boolean isValidValue(HashNode<K, V> current, V value) {
        return Objects.equals(current.value, value);
    }

    private V swapAndGet(HashNode<K, V> current, V value) {
        V oldVal = current.value;
        current.value = value;
        return oldVal;
    }

    /**
     * Пока в этих ссылках разобрался и воевал с нейросетью с ее "прагматичным" решением которого не понял,
     * решил что написать лучше так, чтобы была понятная логика переключения ссылок.
     * по крайней мере как я думаю лучше написать так, чтобы другие люди понимали что к чему,
     * чем производительно и потом хрен разберешься, да и производительности там не особо прибавится.
     * короче мысль в том что нейросеть помогает учиться, но пару десятков раз послать ее на три буквы будет правильным
     * решением, чем потом фиксить нечитаемый баг.
     */
    private void resize() {
        HashNode<K, V>[] oldContainer = container;
        HashNode<K, V>[] newContainer = (HashNode<K, V>[]) new HashNode[extensionMultiplier * oldContainer.length];

        for (int oldBucket = 0; oldBucket < oldContainer.length; oldBucket++) {
            var oldCurrent = oldContainer[oldBucket];

            while (oldCurrent != null) {
                int newBucket = getBucket(oldCurrent.hash(), newContainer);
                var newCurrent = newContainer[newBucket];

                if (newCurrent != null) {
                    var wasHead = newCurrent;//Берем из нового контейнера первую ноду.
                    var willHead = oldCurrent;//Берем из старого контейнера первую ноду

                    oldCurrent = oldCurrent.next;//Смещаем голову в старом контейнере на +1.

                    willHead.next = wasHead;//Линкуем новое начало со старым.
                    newContainer[newBucket] = willHead;//Обновляем бакет новой ссылкой на головную ноду.
                } else {
                    newCurrent = oldCurrent;//Присваиваем ссылку пока в бакете пусто на бакет старого контейнера
                    oldCurrent = oldCurrent.next;//Смещаем голову в старом контейнере на +1.
                    newCurrent.next = null;//Затираем некст чтобы не утянуть за собой линкованые ноды из старого контейнера.

                    newContainer[newBucket] = newCurrent;//Обновляем бакет новой головной нодой.
                }
            }
        }

        container = newContainer;
    }

    private static class HashNode<K, V> implements Entry<K, V> {
        private final int hash;
        private HashNode<K, V> next;

        private final K key;
        private V value;

        HashNode(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }

        public int hash() {
            return hash;
        }

        public HashNode<K, V> next() {
            return next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            this.value = value;
            return value;
        }
    }

    private class HashMapIterator implements Iterator<Map.Entry<K, V>> {
        private final HashNode<K, V>[] container = HashMap.this.container;
        private int bucketCursor = 0;
        private HashNode<K, V> nextNode = null;

        HashMapIterator() {
            cursorGreedySearch();
        }

        @Override
        public boolean hasNext() {
            return Objects.nonNull(nextNode);
        }

        @Override
        public Entry<K, V> next() {
            if (!hasNext()) throw new NoSuchElementException();

            var currentNode = nextNode;

            if (isNextExist()) {
                moveNextNode();
            } else {
                findNextCandidate();
            }

            return currentNode;
        }

        private void cursorGreedySearch() {
            nextNode = null;

            while (isCursorInPresence()) {
                if (isBucketNotEmpty()) {
                    nextNode = container[bucketCursor];
                    return;
                }
                bucketCursor++;
            }
        }

        private void moveNextNode() {
            nextNode = nextNode.next;
        }

        private void findNextCandidate() {
            bucketCursor++;
            cursorGreedySearch();
        }

        private boolean isNextExist() {
            return nextNode.next != null;
        }

        private boolean isCursorInPresence() {
            return bucketCursor < container.length;
        }

        private boolean isBucketNotEmpty() {
            return Objects.nonNull(container[bucketCursor]);
        }
    }
}
