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
    // dummy node
    // positive: simplifies iterators, constructors, and copy constructors
    // negative: need if statements to get mHead in resize
    private Node mHead = new Node();

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

        Node here = mHead;
        for(int i=0; i<size; i++){
	    // @@ Consider building the list backwards.
            // Why? then I can't use this constructor and have to fencepost the mHead being connected at the end.
            // This would appear to only complicate code
            new Node(defaultValue, here);
            here = here.mNext;
        }

        mDef = defaultValue;
        mSize = size;
    }

    /**
     * Copy constructor; creates a deep copy of the provided array.
     * @param s The array to be copied.
     */
    public ListArray(ListArray<T> s) {
        mSize = s.size();
        mDef = s.mDef;

        Iterator<T> it = s.iterator();
        T there;
        Node here = mHead;

        for(int i=0; i<s.size(); i++){
            there = it.next();
            new Node(there, here);
            here = here.mNext;
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

        if(size() < size){
            last = size() != 0 ? seek(size()-1) : mHead; // complicated bc dummy node

            for(int i = size(); i<size; i++){
                new Node(mDef, last);
                last = last.mNext;
            }
        }
        else if(size() > size){
            last = size != 0 ? seek(size-1) : mHead;

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
        return seek(index).mData;
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

        seek(index).mData = value;
    }

    private Node seek(int index) {
        rangeCheck(index);
        Node tmp = mHead;
        for(int i=0; i<=index; i++){
            tmp = tmp.mNext;
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
        T tmp = mDef;

        for(int i=0; i<=index; i++){  // for loop has to run if range check doesn't throw an exception
            tmp = it.next();
        }

        it.remove();
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
            here = here.mNext;
            there = there.mNext;

            int comparison = here.mData.compareTo(there.mData);
            if(comparison != 0){
                return comparison;
            }
        }
        int result = size() - s.size();
        return result;
    }

    /**
     * Throws an exception if the index is out of bound.
     */
    private void rangeCheck(int index) {
        if(index < 0 || index >= mSize)
            throw new ArrayIndexOutOfBoundsException("Index is not within range");
    }

    /**
     * Factory method that returns an Iterator.
     */
    public Iterator<T> iterator() {
        return new ListIterator();
    }

    private class Node implements Iterable<Node> {
        T mData;

        Node mNext;

        /**
         * Default constructor (no op).
         */
        Node() {
        }

        /**
         * Construct a Node from a @a prev Node.
         */
        Node(Node prev) {
            mNext = prev.mNext;
            prev.mNext = this;
        }

        /**
         * Construct a Node from a @a value and a @a prev Node.
         */
        Node(T value, Node prev) {
            this(prev);
            mData = value;
        }

        /**
         * Ensure all subsequent nodes are properly deallocated.
         */
        void prune() {
            Node second = this.mNext;
            while(second != null){
                Node tmp = second;
                second = second.mNext;
                tmp.mNext = null;
            }
        }

        @Override
        public Iterator<Node> iterator() {
            return new NodeIterator();
        }
    }

    private class NodeIterator implements Iterator<Node> {

        private Node nCurrent = mHead;

        private Node nLast;

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return nCurrent.mNext != null;
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
            nLast = nCurrent;
            nCurrent = nCurrent.mNext;
            return nCurrent;
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
            if(nLast == null)
                throw new IllegalStateException("Node was already removed");

            nLast.mNext = nCurrent.mNext;
            nLast = null;
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
            return iList.next().mData;
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
