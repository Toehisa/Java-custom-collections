package iterable.collection.deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<E> implements Iterable<E> {
    DequeNode<E> head;
    DequeNode<E> tail;

    private int size = 0;
    private final int limit;

    public Deque() {
        this.limit = Integer.MAX_VALUE;
    }

    public Deque(int limit) {
        this.limit = limit;
    }

    public boolean push(E value) {
        if (size >= limit) return false;

        if (head == null) {
            head = tail = new DequeNode<>(value);
        } else {
            var prev = tail;
            tail = tail.next = new DequeNode<>(value);
            tail.prev = prev;
        }

        size++;

        return true;
    }

    public E pop() {
        if (isEmpty()) return null;

        E value = tail.value;

        if (size != 1) {
            tail = tail.prev;
            tail.next = null;
        } else {
            head = tail = null;
        }

        size--;

        return value;
    }

    public E peek() {
        return isEmpty() ? null : tail.value;
    }

    public boolean offer(E o) {
        return push(o);
    }

    public E poll() {
        if (head == null) return null;

        E item = head.value;
        head = head.next;
        size--;

        if (head == null) {
            tail = null;
        } else {
            head.prev = null;
        }

        return item;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<E> {
        DequeNode<E> cursor = head;
        @Override
        public boolean hasNext() {
            return cursor != null;
        }

        @Override
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();

            E value = cursor.value;
            cursor = cursor.next;
            return value;
        }
    }

    private static class DequeNode<E> {
        DequeNode<E> next;
        DequeNode<E> prev;

        E value;

        DequeNode(E value) {this.value = value;}
    }
}
