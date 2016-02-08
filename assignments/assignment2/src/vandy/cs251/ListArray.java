package vandy.cs251;

import java.lang.ArrayIndexOutOfBoundsException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Provides a generic dynamically-(re)sized array abstraction.
 */
public class ListArray<T extends Comparable<T>>
        implements Comparable<ListArray<T>>,
        Iterable<T> {
    /**
     * The underlying list of type T.
     */
    private Node mHead;

    /**
     * The current size of the array.
     */
    private int mSize;

    /**
     * Default value for elements in the array.
     */
    private T mDef;

    /**
     * Constructs an array of the given size.
     * @param size Nonnegative integer size of the desired array.
     * @throws NegativeArraySizeException if the specified size is
     *         negative.
     */
    @SuppressWarnings("unchecked")
    public ListArray(int size) throws NegativeArraySizeException {
        this(size, null);
    }

    /**
     * Constructs an array of the given size, filled with the provided
     * default value.
     * @param size Nonnegative integer size of the desired array.
     * @param defaultValue A default value for the array.
     * @throws NegativeArraySizeException if the specified size is
     *         negative.
     */
    public ListArray(int size,
                     T defaultValue) throws NegativeArraySizeException {
        if(size < 0)
            throw new NegativeArraySizeException("Size is less than zero");

        mHead = new Node();
        mHead.data = defaultValue;
        Node here = mHead;
        for(int i=0; i<size; i++){
	    // @@ Consider building the list backwards.
            new Node(defaultValue, here);
            here = here.next;
        }

        mDef = defaultValue;
        mSize = size;
    }

    /**
     * Copy constructor; creates a deep copy of the provided array.
     * @param s The array to be copied.
     */
    public ListArray(ListArray<T> s) {
        mHead = new Node();
        mHead.data = s.mHead.data;
        mSize = s.size();
        mDef = s.mDef;
        Node here = mHead;
        Node there = s.mHead;
	// @@ consider using an iterator?
        for(int i=1; i<s.size(); i++){
            there = there.next;
            new Node(there.data, here);
            here = here.next;
        }
    }

    /**
     * @return The current size of the array.
     */
    public int size() {
        return mSize;
    }

    /**
     * Resizes the array to the requested size.
     *
     * Changes the size of this ListArray to hold the requested number of elements.
     * @param size Nonnegative requested new size.
     */
    public void resize(int size) {
        if(size < 0)
            throw new NegativeArraySizeException("Size is less than zero");

        Node last;
        if(size() != 0){
            last = seek(size()-1);
        }else{
	    // @@ do you need to allocate a new node here?
            mHead = new Node();
            mHead.data = mDef;
            last = mHead;
        }

        if(size() < size){
            for(int i = size(); i<size; i++){
                new Node(mDef, last);
                last = last.next;
            }
        }else if(size() > size){
            last.prune();
        }
        mSize = size;
    }

    /**
     * @return the element at the requested index.
     * @param index Nonnegative index of the requested element.
     * @throws ArrayIndexOutOfBoundsException If the requested index is outside the
     * current bounds of the array.
     */
    public T get(int index) {
        return seek(index).data;
    }

    /**
     * Sets the element at the requested index with a provided value.
     * @param index Nonnegative index of the requested element.
     * @param value A provided value.
     * @throws ArrayIndexOutOfBoundsException If the requested index is outside the
     * current bounds of the array.
     */
    public void set(int index, T value) {
        rangeCheck(index);

        seek(index).data = value;
    }

    private Node seek(int index) {
        rangeCheck(index);
        Node tmp = mHead;
        for(int i=0; i<index; i++){
            tmp = tmp.next;
        }
        return tmp;
    }

    /**
     * Removes the element at the specified position in this ListArray.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).  Returns the element that was removed from the ListArray.
     *
     * @param index the index of the element to remove
     * @return element that was removed
     * @throws ArrayIndexOutOfBoundsException if the index is out of range.
     */
    public T remove(int index) {
        rangeCheck(index);

        Iterator<T> it = iterator();
        T tmp = mHead.data;

        for(int i=0; i<index; i++){
            tmp = it.next();
        }

        it.remove();
	// @@ Shouldn't remove do this?
        mSize--;
        return tmp;
    }

    /**
     * Compares this array with another array.
     * <p>
     * This is a requirement of the Comparable interface.  It is used to provide
     * an ordering for ListArray elements.
     * @return a negative value if the provided array is "greater than" this array,
     * zero if the arrays are identical, and a positive value if the
     * provided array is "less than" this array.
     */
    @Override
    public int compareTo(ListArray<T> s) {
        Node here = mHead;
        Node there = s.mHead;
        for(int i=0; i<Math.min(size(), s.size()); i++){
	    // @@ Careful, this doesn't do what you expect:
            if(here.data != there.data){
                return here.data.compareTo(there.data);
            }
            here = here.next;
            there = there.next;
        }
        int result = size() - s.size();
        return result;
    }

    /**
     * Throws an exception if the index is out of bound.
     */
    private void rangeCheck(int index) {
        if(index < 0 || index >= mSize){
            throw new ArrayIndexOutOfBoundsException("Index is not within range");
        }
    }

    /**
     * Factory method that returns an Iterator.
     */
    public Iterator<T> iterator() {
        return new ListIterator();
    }

    private class Node implements Iterable<Node> {
	// @@ Please prefix class member variables with 'm'; e.g. mFoo or mBar
        T data;

        Node next;

        /**
         * Default constructor (no op).
         */
        Node() {
        }

        /**
         * Construct a Node from a @a prev Node.
         */
        Node(Node prev) {
	    // @@ This isn't necessary:
            this();
            next = prev.next;
            prev.next = this;
        }

        /**
         * Construct a Node from a @a value and a @a prev Node.
         */
        Node(T value, Node prev) {
            this(prev);
            data = value;
        }

        /**
         * Ensure all subsequent nodes are properly deallocated.
         */
        void prune() {
            Node second = this.next;
            while(second != null){
                Node tmp = second;
                second = second.next;
                tmp.next = null;
            }
        }

        @Override
        public Iterator<Node> iterator() {
            return new NodeIterator();
        }
    }

    private class NodeIterator implements Iterator<Node> {

        private Node current = mHead;

        private Node last;

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return current.next != null;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public Node next() {
            if(!hasNext())
                throw new NoSuchElementException("Node doesn't exist");
            last = current;
            current = current.next;
            return last;
        }

        /**
         * Removes from the underlying collection the last element returned
         * by this iterator (optional operation).  This method can be called
         * only once per call to {@link #next}.  The behavior of an iterator
         * is unspecified if the underlying collection is modified while the
         * iteration is in progress in any way other than by calling this
         * method.
         *
         * @throws UnsupportedOperationException if the {@code remove}
         *                                       operation is not supported by this iterator
         * @throws IllegalStateException         if the {@code next} method has not
         *                                       yet been called, or the {@code remove} method has already
         *                                       been called after the last call to the {@code next}
         *                                       method
         * @implSpec The default implementation throws an instance of
         * {@link UnsupportedOperationException} and performs no other action.
         */
        @Override
        public void remove() {
            if(last == null)
                throw new IllegalStateException("Node was already removed");

            if(current == mHead){
                mHead = current.next;
            }else{
                last.next = current.next;
            }
            mSize--;
        }
    }

    /**
     * @brief This class implements an iterator for the list.
     */
    private class ListIterator implements Iterator<T> {

        private NodeIterator iList = new NodeIterator();

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public T next() {
            return iList.next().data;
        }

        /**
         * Removes from the underlying collection the last element returned
         * by this iterator (optional operation).  This method can be called
         * only once per call to {@link #next}.  The behavior of an iterator
         * is unspecified if the underlying collection is modified while the
         * iteration is in progress in any way other than by calling this
         * method.
         *
         * @throws UnsupportedOperationException if the {@code remove}
         *                                       operation is not supported by this iterator
         * @throws IllegalStateException         if the {@code next} method has not
         *                                       yet been called, or the {@code remove} method has already
         *                                       been called after the last call to the {@code next}
         *                                       method
         * @implSpec The default implementation throws an instance of
         * {@link UnsupportedOperationException} and performs no other action.
         */
        @Override
        public void remove() {
            iList.remove();
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return iList.hasNext();
        }
    }
}
