package iterable.collection.queue;

import java.util.AbstractQueue;
import java.util.Iterator;

public class Queue<E> extends AbstractQueue<E> {
    QueueNode<E> head;
    QueueNode<E> tail;

    private int size;
    private int limit;

    Queue(int limit) {
        this.limit = limit;
        this.size = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean offer(E o) {
        if (size >= limit) {return false;}

        if (head == null) {
            head = tail = new QueueNode<>(o);
        } else {
            tail = tail.next = new QueueNode<>(o);
        }

        size++;

        return true;
    }

    @Override
    public E poll() {
        if (head == null) return null;

        E item = head.value;
        head = head.next;
        size--;

        if (head == null) tail = null;
        return item;
    }

    @Override
    public E peek() {
        return head != null ? head.value : null;
    }

    private static class QueueNode<E> {
        E value;
        QueueNode<E> next;

        QueueNode(E value) {
            this.value = value;
        }
    }
}
