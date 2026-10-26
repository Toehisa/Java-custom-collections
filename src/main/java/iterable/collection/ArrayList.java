package iterable.collection;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayList<E> implements Iterable<E>{
    private E[] box;
    private int size;
    private double extensionMultiplier;

    private int modCount;

    {
        size = 0;
        extensionMultiplier = 1.5;
        modCount = 0;
    }

    public ArrayList() {
        box = (E[]) new Object[10];
    }

    public ArrayList(int size) {
        box = (E[]) new Object[size];
    }

    public void setExtensionMultiplier(double extensionMultiplier) {
        this.extensionMultiplier = extensionMultiplier;
    }

    public int size() {
        return size;
    }

    public void add(E dataObject) {
        if (size() >= box.length) {extension();}
        box[size++] = dataObject;
        ++modCount;
    }

    public void add(int index, E element) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("Индекс не совпадает с границами");
        }

        if (size() >= box.length) {
            extension();
        }

        System.arraycopy(box, index, box, index + 1, size - index);
        box[index] = element;
        size++;
        ++modCount;
    }

    public E remove(int index) {
        validateBounds(index);

        E removedElem = box[index];
        System.arraycopy(box, index + 1, box, index, size - index - 1);
        box[--size] = null;

        ++modCount;
        return removedElem;
    }

    public E get(int index) {
        validateBounds(index);
        return box[index];
    }

    public E set(int index, E element) {
        validateBounds(index);

        E oldElem = box[index];
        box[index] = element;

        return oldElem;
    }

    public void trimToSize() {
        if (size == box.length) return;

        E[] oldBox = box;
        box = (E[]) new Object[size()];

        System.arraycopy(oldBox, 0, box, 0, size);
        ++modCount;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayListIterator();
    }

    private void validateBounds(int index) {
        if (!isIndexInBound(index)) {throw new IndexOutOfBoundsException("Индекс указывает на несуществующий елемент");}
    }

    private boolean isIndexInBound(int index) {
        return index >= 0 && index < size();
    }

    private void extension() {
        E[] oldBox = box;
        box = (E[]) new Object[(int)(extensionMultiplier * box.length)];
        System.arraycopy(oldBox, 0, box, 0, oldBox.length);
    }

    private class ArrayListIterator implements Iterator<E> {
        private int cursor = 0;
        private final int expectedModCount;

        ArrayListIterator() {
            expectedModCount = modCount;
        }

        @Override
        public boolean hasNext() {
            return cursor < size();
        }

        @Override
        public E next() {
            if (expectedModCount != modCount) throw new ConcurrentModificationException("Попытка изменения структуры через foreach");
            if (!hasNext()) throw new NoSuchElementException("Последующего элемента не существует");
            return box[cursor++];
        }
    }
}
