package iterable.collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ArrayListTest {

    private ArrayList<String> list;

    @BeforeEach
    void setUp() {
        // Вызывается перед каждым тестом, чтобы у нас всегда был чистый список
        list = new ArrayList<>();
    }

    // ==========================================
    // 1. БАЗОВЫЕ ОПЕРАЦИИ И ДИНАМИЧЕСКИЙ РОСТ
    // ==========================================

    @Test
    void testAddAndGet() {
        list.add("A");
        list.add("B");

        assertEquals(2, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
    }

    @Test
    void testDynamicExtension() {
        // Добавляем 100 элементов (дефолтная вместимость 10).
        // Массив должен расшириться несколько раз без потери данных.
        for (int i = 0; i < 100; i++) {
            list.add("Element " + i);
        }

        assertEquals(100, list.size());
        assertEquals("Element 0", list.get(0));
        assertEquals("Element 50", list.get(50));
        assertEquals("Element 99", list.get(99));
    }

    // ==========================================
    // 2. ВСТАВКА В СЕРЕДИНУ И СДВИГИ
    // ==========================================

    @Test
    void testAddByIndex() {
        list.add("A");
        list.add("C");

        // Вставка в середину
        list.add(1, "B");
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2)); // C должен сдвинуться вправо
        assertEquals(3, list.size());

        // Вставка в начало
        list.add(0, "Start");
        assertEquals("Start", list.get(0));

        // Вставка в конец (эквивалент обычного add)
        list.add(list.size(), "End");
        assertEquals("End", list.get(list.size() - 1));
    }

    // ==========================================
    // 3. УДАЛЕНИЕ И ЗАМЕНА
    // ==========================================

    @Test
    void testRemove() {
        list.add("A");
        list.add("B");
        list.add("C");

        String removed = list.remove(1); // Удаляем "B"

        assertEquals("B", removed);
        assertEquals(2, list.size());
        assertEquals("C", list.get(1)); // C сдвинулся влево
    }

    @Test
    void testRemoveLastElement() {
        list.add("A");
        // При удалении последнего элемента размер хвоста = 0, arraycopy не сдвигает
        String removed = list.remove(0);

        assertEquals("A", removed);
        assertEquals(0, list.size());
    }

    @Test
    void testSet() {
        list.add("A");
        String oldElement = list.set(0, "New A");

        assertEquals("A", oldElement);
        assertEquals("New A", list.get(0));
        assertEquals(1, list.size()); // Размер не изменился
    }

    // ==========================================
    // 4. ТЕСТИРОВАНИЕ ОШИБОК (EXCEPTIONS)
    // ==========================================

    @Test
    void testIndexOutOfBounds() {
        list.add("A");

        // get() за пределами
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));

        // set() за пределами
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(1, "X"));

        // remove() за пределами
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));

        // add(index) с дыркой (индекс > size)
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, "X"));
    }

    // ==========================================
    // 5. РАБОТА С ПАМЯТЬЮ (Reflection для проверки private)
    // ==========================================

    @Test
    void testTrimToSize() throws NoSuchFieldException, IllegalAccessException {
        // Добавляем 15 элементов (вызовет расширение до 15: 10 * 1.5)
        for (int i = 0; i < 15; i++) {
            list.add("X");
        }

        // Удаляем 10 элементов, size станет 5, но вместимость останется 15
        for (int i = 0; i < 10; i++) {
            list.remove(0);
        }

        list.trimToSize();

        // Используем Reflection API, чтобы залезть в private поле 'box' и проверить его length
        Field boxField = ArrayList.class.getDeclaredField("box");
        boxField.setAccessible(true);
        Object[] internalArray = (Object[]) boxField.get(list);

        assertEquals(5, list.size()); // Логический размер 5
        assertEquals(5, internalArray.length); // Физический массив тоже сжался до 5!
    }

    // ==========================================
    // 6. FAIL-FAST И ИТЕРАТОРЫ
    // ==========================================

    @Test
    void testIteratorHappyPath() {
        list.add("A");
        list.add("B");

        Iterator<String> iterator = list.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("A", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("B", iterator.next());
        assertFalse(iterator.hasNext());

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void testFailFastOnAdd() {
        list.add("A");
        Iterator<String> iterator = list.iterator();

        list.add("B"); // Структурное изменение после создания итератора!

        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    void testFailFastOnRemove() {
        list.add("A");
        list.add("B");
        Iterator<String> iterator = list.iterator();

        list.remove(0); // Структурное изменение!

        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    void testNoFailFastOnSet() {
        list.add("A");
        Iterator<String> iterator = list.iterator();

        list.set(0, "B"); // Перезапись НЕ является структурным изменением

        assertDoesNotThrow(() -> {
            String value = iterator.next();
            assertEquals("B", value); // Итератор должен спокойно отдать новое значение
        });
    }
}