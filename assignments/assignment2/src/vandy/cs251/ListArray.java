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
        // TODO - you fill in here.
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


    }

    /**
     * Copy constructor; creates a deep copy of the provided array.
     * @param s The array to be copied.
     */
    public ListArray(ListArray<T> s) {
        mHead = new Node();
        mHead.nData = s.mHead.nData;
        mSize = s.size();
        mDef = s.mDef;
        Node here = mHead;
        Node there = s.mHead;
        for(int i=1; i<s.size(); i++){
            there = there.nNext;
            new Node(there.nData, here);
            here = here.nNext;
        }
    }

    /**
     * @return The current size of the array.
     */
    public int size() {
        return mSize;
    }

    /**
     * @return The current maximum capacity of the array withough
     */
    public int capacity() {
        return mSize;
    }

    /**
     * Resizes the array to the requested size.
     *
     * Changes the capacity of this array to hold the requested number of elements.
     * Note the following optimizations/implementation details:
     * <ul>
     *   <li> If the requests size is smaller than the current maximum capacity, new memory
     *   is not allocated.
     *   <li> If the array was constructed with a default value, it is used to populate
     *   uninitialized fields in the array.
     * </ul>
     * @param size Nonnegative requested new size.
     */
    public void resize(int size) {
        if(size < 0)
            throw new NegativeArraySizeException("Size is less than zero");

//        Node last;
//        if(size() != 0){
//            last = seek(size()-1);
//        }else{
//            mHead = new Node();
//            mHead.data = mDefault;
//            last = mHead;
//        }
//
//        if(size() < size){
//            for(int i = size(); i<size; i++){
//                new Node(mDefault, last);
//                last = last.next;
//            }
//        }else if(size() > size){
//            last.prune();
//        }
//        mSize = size;
    }

    /**
     * @return the element at the requested index.
     * @param index Nonnegative index of the requested element.
     * @throws ArrayIndexOutOfBoundsException If the requested index is outside the
     * current bounds of the array.
     */
    public T get(int index) {
        rangeCheck(index);
        return null;
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

        T tmp = get(index);
        tmp = value;
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

        T tmp = get(index);
        for(int i=index; i<mSize; i++){

        }
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
//        for(int i=0; i<Math.min(size(), s.size()); i++){
//            if(mList[i] != s.mList[i]){
//                return mList[i] - s.mList[i];
//            }
//        }
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

        T nData;

        Node nNext;

        /**
         * Default constructor (no op).
         */
        Node() {
        }

        /**
         * Construct a Node from a @a prev Node.
         */
        Node(Node prev) {
            this();
            nNext = prev.nNext;
            prev.nNext = this;
        }

        /**
         * Construct a Node from a @a value and a @a prev Node.
         */
        Node(T value, Node prev) {
            this(prev);
            nData = value;
        }

        /**
         * Ensure all subsequent nodes are properly deallocated.
         */
        void prune() {
            Node second = this.nNext;
            while(second != null){
                Node tmp = second;
                second = second.nNext;
                tmp.nNext = null;
            }
        }

        @Override
        public Iterator<Node> iterator() {
            return new NodeIterator();
        }
    }

    private class NodeIterator implements Iterator<Node> {

        private Node iHead = new Node();

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return iHead.nNext != null;
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
            iHead = iHead.nNext;
            return iHead;
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
            return iList.next().nData;
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
