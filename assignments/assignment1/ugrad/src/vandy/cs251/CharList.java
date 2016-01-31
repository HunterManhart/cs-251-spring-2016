package vandy.cs251;

import java.lang.IndexOutOfBoundsException;

/**
 * Provides a wrapper facade around primitive char lists, allowing
 * for dynamic resizing.
 */
public class CharList 
             implements Comparable<CharList>, 
                        Cloneable {
    /**
     * The head of the list.
     */
    private Node mHead;

    /**
     * The current size of the list.
     */
    private int mSize;

    /**
     * Default value for elements in the list.
     */
    // @@ Please prefix class member variables with 'm'; e.g. mFoo or mBar
    private char mDefault;

    /**
     * Constructs an list of the given size.
     *
     * @param size Non-negative integer size of the desired list.
     */
    public CharList(int size) {
        this(size, '\u0000');
    }

    /**
     * Constructs an list of the given size, filled with the provided
     * default value.
     *
     * @param size Nonnegative integer size of the desired list.
     * @param defaultValue A default value for the list.
     * @throw IndexOutOfBoundsException If size < 0.
     */
    public CharList(int size, char defaultValue) {
        if(size<0){
            throw new IndexOutOfBoundsException("Size is less than zero");
        }else if(size > 0){
            mHead = new Node();
            mHead.data = defaultValue;
            Node here = mHead;
            for(int i=0; i<size; i++){
                new Node(defaultValue, here);
                here = here.next;
            }
        }

        mDefault = defaultValue;
        mSize = size;
    }

    /**
     * Copy constructor; creates a deep copy of the provided CharList.
     *
     * @param s The CharList to be copied.
     */
    public CharList(CharList s) {
        mHead = new Node();
        mHead.data = s.mHead.data;
        mSize = s.size();
        mDefault = s.mDefault;
        Node here = mHead;
        Node there = s.mHead;
        for(int i=1; i<s.size(); i++){
            there = there.next;
            new Node(there.data, here);
            here = here.next;
        }
    }

    /**
     * Creates a deep copy of this CharList.  Implements the
     * Prototype pattern.
     */
    @Override
    public Object clone() {
	// @@ This is incorrect:
        return new CharList(this);
    }

    /**
     * @return The current size of the list.
     */
    public int size() {
    	return mSize;
    }

    /**
     * Resizes the list to the requested size.
     *
     * Changes the capacity of this list to hold the requested number of elements.
     * Note the following optimizations/implementation details:
     * <ul>
     *   <li> If the requests size is smaller than the current maximum capacity, new memory
     *   is not allocated.
     *   <li> If the list was constructed with a default value, it is used to populate
     *   uninitialized fields in the list.
     * </ul>
     * @param size Nonnegative requested new size.
     */
    public void resize(int size) {
        if(size < 0){
            throw new NegativeArraySizeException("Size is less than zero");
        }

        Node last;
        if(size() != 0){
            last = seek(size()-1);
        }else{
            mHead = new Node();
            mHead.data = mDefault;
            last = mHead;
        }

        if(size() < size){
            for(int i = size(); i<size; i++){
                new Node(mDefault, last);
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
     * @throws IndexOutOfBoundsException If the requested index is outside the
     * current bounds of the list.
     */
    public char get(int index) {
        return seek(index).data;
    }

    /**
     * Sets the element at the requested index with a provided value.
     * @param index Nonnegative index of the requested element.
     * @param value A provided value.
     * @throws IndexOutOfBoundsException If the requested index is outside the
     * current bounds of the list.
     */
    public void set(int index, char value) {
        seek(index).data = value;
    }

    /**
     * Locate and return the @a Node at the @a index location.
     */
    private Node seek(int index) {
        rangeCheck(index);
        Node tmp = mHead;
        for(int i=0; i<index; i++){
            tmp = tmp.next;
        }
        return tmp;
    }

    /**
     * Compares this list with another list.
     * <p>
     * This is a requirement of the Comparable interface.  It is used to provide
     * an ordering for CharList elements.
     * @return a negative value if the provided list is "greater than" this list,
     * zero if the lists are identical, and a positive value if the
     * provided list is "less than" this list. These lists should be compred
     * lexicographically.
     */
    @Override
    public int compareTo(CharList s) {
        Node here = mHead;
        Node there = s.mHead;
        for(int i=0; i<Math.min(size(), s.size()); i++){
            if(here.data != there.data){
                return here.data - there.data;
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
        if(index >= mSize || index < 0)
            throw new IndexOutOfBoundsException("Index isn't with range");
    }

    /**
     * A Node in the Linked List.
     */
    private class Node {
        /**
         * Value stored in the Node.
         */
	    char data;

        /**
         * Reference to the next node in the list.
         */
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
            this();
            next = prev.next;
            prev.next = this;
        }

        /**
         * Construct a Node from a @a value and a @a prev Node.
         */
        Node(char value, Node prev) {
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
    }
}
