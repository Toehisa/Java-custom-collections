package iterable.collection.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SimpleQueueTest {

    private Queue<String> queue;

    @BeforeEach
    void setUp() {
        // Перед каждым тестом создаем новую пустую очередь с лимитом в 3 элемента
        queue = new Queue<>(3);
    }

    @Test
    void testEmptyQueueBehavior() {
        // Проверяем, что пустая очередь ведет себя безопасно
        assertNull(queue.poll(), "У пустой очереди poll должен возвращать null");
        assertNull(queue.peek(), "У пустой очереди peek должен возвращать null");
        assertEquals(0, queue.size(), "Размер пустой очереди должен быть 0");
    }

    @Test
    void testOfferAndPollFIFO() {
        // Проверяем главный принцип FIFO (First In - First Out)
        assertTrue(queue.offer("Первый"));
        assertTrue(queue.offer("Второй"));

        assertEquals(2, queue.size());

        // Тот, кто пришел первым, должен и уйти первым
        assertEquals("Первый", queue.poll());
        assertEquals(1, queue.size());

        assertEquals("Второй", queue.poll());
        assertEquals(0, queue.size());
    }

    @Test
    void testPeekDoesNotRemoveElement() {
        // Проверяем, что peek только смотрит, но не удаляет
        queue.offer("Данные");

        assertEquals("Данные", queue.peek(), "peek должен вернуть правильный элемент");
        assertEquals(1, queue.size(), "Размер не должен измениться после peek");
        assertEquals("Данные", queue.poll(), "poll должен успешно забрать этот же элемент");
    }

    @Test
    void testCapacityLimit() {
        // Проверяем отработку лимита (у нас лимит 3)
        assertTrue(queue.offer("A"));
        assertTrue(queue.offer("B"));
        assertTrue(queue.offer("C"));

        // Четвертый элемент не должен поместиться
        assertFalse(queue.offer("D"), "offer должен вернуть false при превышении лимита");

        // Размер должен остаться равным лимиту
        assertEquals(3, queue.size());
    }

    @Test
    void testQueueReusabilityAfterEmptying() {
        // Проверяем, не ломаются ли ссылки head/tail после полного опустошения
        queue.offer("A");
        queue.poll(); // Очередь снова пуста, head и tail должны были стать null

        // Пробуем добавить снова
        assertTrue(queue.offer("B"), "Очередь должна принимать элементы после опустошения");
        assertEquals("B", queue.peek(), "Новая голова должна указывать на новый элемент");
    }

    @Test
    void testIteratorStandardBehavior() {
        queue.offer("A");
        queue.offer("B");

        var iterator = queue.iterator();

        assertTrue(iterator.hasNext(), "Должен быть первый элемент");
        assertEquals("A", iterator.next());

        assertTrue(iterator.hasNext(), "Должен быть второй элемент");
        assertEquals("B", iterator.next());

        assertFalse(iterator.hasNext(), "Элементы должны были закончиться");
    }

    @Test
    void testIteratorEmptyQueueThrowsException() {
        var iterator = queue.iterator();

        assertFalse(iterator.hasNext(), "У пустой очереди hasNext должен быть false");
        assertThrows(NoSuchElementException.class, () -> iterator.next(),
                "Вызов next() у пустой очереди должен кидать NoSuchElementException");
    }

    @Test
    void testIteratorThrowsExceptionAfterExhaustion() {
        queue.offer("A");
        var iterator = queue.iterator();
        iterator.next(); // Прошли первый и единственный элемент

        assertThrows(NoSuchElementException.class, () -> iterator.next(),
                "Должен кидать ошибку при попытке взять элемент за пределами хвоста");
    }
}
