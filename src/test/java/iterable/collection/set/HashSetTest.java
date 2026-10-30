package iterable.collection.set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HashSetTest {

    private HashSet<String> set;

    @BeforeEach
    void setUp() {
        // Перед каждым тестом создаем чистый сет
        set = new HashSet<>();
    }

    @Test
    void testAddUniqueElements() {
        assertTrue(set.add("Apple"), "Добавление нового элемента должно возвращать true");
        assertTrue(set.add("Banana"), "Добавление нового элемента должно возвращать true");
        assertEquals(2, set.size(), "Размер должен увеличиться до 2");
    }

    @Test
    void testAddDuplicateElements() {
        set.add("Apple");

        assertFalse(set.add("Apple"), "Добавление дубликата должно возвращать false");
        assertEquals(1, set.size(), "Размер не должен меняться при добавлении дубликата");
    }

    @Test
    void testContains() {
        set.add("Apple");

        assertTrue(set.contains("Apple"), "Сет должен содержать добавленный элемент");
        assertFalse(set.contains("Banana"), "Сет не должен содержать элемент, который не добавляли");
    }

    @Test
    void testRemove() {
        set.add("Apple");
        set.add("Banana");

        assertTrue(set.remove("Apple"), "Удаление существующего элемента должно возвращать true");
        assertFalse(set.remove("Cherry"), "Удаление несуществующего элемента должно возвращать false");

        assertEquals(1, set.size(), "Размер должен уменьшиться после удаления");
        assertFalse(set.contains("Apple"), "Удаленного элемента больше не должно быть в сете");
    }

    @Test
    void testIsEmptyAndSize() {
        assertTrue(set.isEmpty(), "Новый сет должен быть пустым");
        assertEquals(0, set.size(), "Размер нового сета должен быть 0");

        set.add("Apple");
        assertFalse(set.isEmpty(), "После добавления сет не должен быть пустым");
        assertEquals(1, set.size());
    }

    @Test
    void testClear() {
        set.add("Apple");
        set.add("Banana");

        set.clear();

        assertTrue(set.isEmpty(), "После clear() сет должен стать пустым");
        assertEquals(0, set.size(), "Размер после clear() должен стать 0");
        assertFalse(set.contains("Apple"), "Элементы должны быть удалены");
    }

    @Test
    void testIterator() {
        set.add("Apple");
        set.add("Banana");
        set.add("Cherry");

        List<String> iteratedElements = new ArrayList<>();

        // Тут под капотом вызовется наш iterator()
        for (String item : set) {
            iteratedElements.add(item);
        }

        assertEquals(3, iteratedElements.size(), "Итератор должен обойти все 3 элемента");
        assertTrue(iteratedElements.contains("Apple"));
        assertTrue(iteratedElements.contains("Banana"));
        assertTrue(iteratedElements.contains("Cherry"));
    }
}