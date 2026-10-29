package map.abstractmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class HashMapTest {

    private HashMap<String, String> map;

    @BeforeEach
    void setUp() {
        map = new HashMap<>();
    }

    @Test
    void testBasicPutAndGet() {
        map.put("User1", "Alice");
        map.put("User2", "Bob");

        assertEquals(2, map.size());
        assertEquals("Alice", map.get("User1"));
        assertEquals("Bob", map.get("User2"));
        assertNull(map.get("User3"), "Несуществующий ключ должен возвращать null");
    }

    @Test
    void testValueOverride() {
        map.put("Key", "OldValue");
        String replacedValue = map.put("Key", "NewValue");

        assertEquals("OldValue", replacedValue, "Метод put должен вернуть старое значение");
        assertEquals("NewValue", map.get("Key"), "Значение по ключу должно обновиться");
        assertEquals(1, map.size(), "Размер мапы не должен увеличиться при замене");
    }

    @Test
    void testRemoveByKey() {
        map.put("Config", "Enabled");
        map.put("Theme", "Dark");

        String removed = map.remove("Config");

        assertEquals("Enabled", removed);
        assertNull(map.get("Config"));
        assertEquals(1, map.size());
    }

    @Test
    void testClearAndIsEmpty() {
        assertTrue(map.isEmpty());

        map.put("A", "1");
        map.put("B", "2");
        assertFalse(map.isEmpty());

        map.clear();
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertNull(map.get("A"));
    }

    @Test
    void testContainsMethods() {
        map.put("Admin", "Root");

        assertTrue(map.containsKey("Admin"));
        assertFalse(map.containsKey("Guest"));

        assertTrue(map.containsValue("Root"));
        assertFalse(map.containsValue("User"));
    }

    @Test
    void testNullKeyThrowsException() {
        assertThrows(NullPointerException.class, () -> map.put(null, "Value"), "Ожидается NPE при null ключе");
        assertThrows(NullPointerException.class, () -> map.get(null));
    }

    @Test
    void testResizeUnderLoad() {
        for (int i = 0; i < 100; i++) {
            map.put("Key" + i, "Value" + i);
        }

        assertEquals(100, map.size());

        for (int i = 0; i < 100; i++) {
            assertEquals("Value" + i, map.get("Key" + i));
        }
    }

    @Test
    void testCollisions() {
        HashMap<CollisionKey, String> collisionMap = new HashMap<>();

        CollisionKey key1 = new CollisionKey("A");
        CollisionKey key2 = new CollisionKey("B");
        CollisionKey key3 = new CollisionKey("C");

        collisionMap.put(key1, "Val1");
        collisionMap.put(key2, "Val2");
        collisionMap.put(key3, "Val3");

        assertEquals(3, collisionMap.size());
        assertEquals("Val2", collisionMap.get(key2));

        collisionMap.remove(key2);

        assertNull(collisionMap.get(key2));
        assertEquals("Val1", collisionMap.get(key1));
        assertEquals("Val3", collisionMap.get(key3));
    }

    @Test
    void testFailFastIteratorOnPut() {
        map.put("1", "A");
        map.put("2", "B");

        var iterator = map.entrySet().iterator();

        assertTrue(iterator.hasNext());
        iterator.next();

        map.put("3", "C");

        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    void testFailFastIteratorOnRemove() {
        map.put("1", "A");
        map.put("2", "B");

        var iterator = map.entrySet().iterator();

        map.remove("2");

        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    private static class CollisionKey {
        private final String name;

        CollisionKey(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CollisionKey that = (CollisionKey) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }
}
