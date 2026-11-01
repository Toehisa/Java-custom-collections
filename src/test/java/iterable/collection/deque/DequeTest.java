package iterable.collection.deque;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class DequeTest {

    private Deque<String> deque;

    @BeforeEach
    void setUp() {
        // Перед каждым тестом создаем новую деку без строгого лимита
        deque = new Deque<>();
    }

    // --- Тесты для isEmpty() и size() ---

    @Test
    @DisplayName("Методы size() и isEmpty() должны корректно отражать состояние коллекции")
    void testSizeAndIsEmpty() {
        assertTrue(deque.isEmpty(), "Новая дека должна быть пустой");
        assertEquals(0, deque.size(), "Размер новой деки должен быть 0");

        deque.push("A");
        assertFalse(deque.isEmpty(), "После добавления дека не пустая");
        assertEquals(1, deque.size(), "Размер должен увеличиться до 1");

        deque.pop();
        assertTrue(deque.isEmpty(), "После извлечения всех элементов дека снова пустая");
        assertEquals(0, deque.size());
    }

    // --- Тесты для push() и конструктора с лимитом ---

    @Test
    @DisplayName("push() должен добавлять элементы и возвращать false при превышении лимита")
    void testPushWithLimit() {
        Deque<Integer> limitedDeque = new Deque<>(2); // Лимит 2 элемента

        assertTrue(limitedDeque.push(1), "Первый элемент должен добавиться успешно");
        assertTrue(limitedDeque.push(2), "Второй элемент должен добавиться успешно");

        assertFalse(limitedDeque.push(3), "Третий элемент не должен добавиться из-за лимита");
        assertEquals(2, limitedDeque.size(), "Размер не должен превышать лимит");
    }

    // --- Тесты для peek() ---

    @Test
    @DisplayName("peek() должен возвращать последний добавленный элемент без его удаления")
    void testPeek() {
        assertNull(deque.peek(), "peek() на пустой коллекции должен возвращать null");

        deque.push("First");
        deque.push("Second");

        assertEquals("Second", deque.peek(), "peek() должен вернуть элемент на вершине (tail)");
        assertEquals(2, deque.size(), "Размер не должен уменьшаться после вызова peek()");
    }

    // --- Тесты для pop() ---

    @Test
    @DisplayName("pop() должен извлекать элементы в порядке LIFO и возвращать null, если пусто")
    void testPop() {
        assertNull(deque.pop(), "pop() на пустой коллекции должен возвращать null");

        deque.push("A");
        deque.push("B");
        deque.push("C");

        assertEquals("C", deque.pop(), "Должен извлечься последний добавленный элемент");
        assertEquals("B", deque.pop());
        assertEquals("A", deque.pop());

        assertNull(deque.pop(), "После полного опустошения должен возвращать null");
        assertTrue(deque.isEmpty(), "Коллекция должна стать пустой");
    }

    // --- Тесты для iterator() ---

    @Test
    @DisplayName("Итератор должен обходить коллекцию от head к tail (в порядке добавления)")
    void testIteratorTraversal() {
        deque.push("First");
        deque.push("Second");
        deque.push("Third");

        Iterator<String> iterator = deque.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("First", iterator.next()); // Твоя реализация итератора идет с head

        assertTrue(iterator.hasNext());
        assertEquals("Second", iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals("Third", iterator.next());

        assertFalse(iterator.hasNext(), "Итератор должен закончиться");
    }

    @Test
    @DisplayName("Итератор должен выбрасывать NoSuchElementException при вызове next(), если элементов больше нет")
    void testIteratorThrowsException() {
        Iterator<String> iterator = deque.iterator();

        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next, "Ожидается исключение на пустом итераторе");

        deque.push("A");
        Iterator<String> singleElementIterator = deque.iterator();
        singleElementIterator.next(); // Проходим единственный элемент

        assertThrows(NoSuchElementException.class, singleElementIterator::next, "Ожидается исключение при выходе за границы коллекции");
    }

    @Test
    @DisplayName("offer() и poll() должны работать как классическая очередь (FIFO)")
    void testOfferAndPoll_FifoBehavior() {
        // Добавляем элементы в очередь
        assertTrue(deque.offer("First"));
        assertTrue(deque.offer("Second"));
        assertTrue(deque.offer("Third"));

        assertEquals(3, deque.size());

        // Извлекаем элементы. В отличие от pop(), poll() должен забирать с начала (head)
        assertEquals("First", deque.poll());
        assertEquals("Second", deque.poll());
        assertEquals("Third", deque.poll());

        assertNull(deque.poll(), "Очередь пуста, должен вернуться null");
        assertTrue(deque.isEmpty());
    }

    @Test
    @DisplayName("poll() не должен падать на пустой коллекции")
    void testPollOnEmptyCollection() {
        assertNull(deque.poll());
        assertEquals(0, deque.size());
    }

    @Test
    @DisplayName("Смешанное использование методов стека и очереди должно работать корректно")
    void testMixedDequeOperations() {
        deque.offer("A"); // Стал head и tail
        deque.push("B");  // Стал новым tail

        assertEquals(2, deque.size());

        assertEquals("A", deque.poll()); // Забрали head (A)
        assertEquals("B", deque.pop());  // Забрали tail (B)

        assertTrue(deque.isEmpty());
        assertNull(deque.head, "Голова должна занулиться");
        assertNull(deque.tail, "Хвост должен занулиться");
    }
}