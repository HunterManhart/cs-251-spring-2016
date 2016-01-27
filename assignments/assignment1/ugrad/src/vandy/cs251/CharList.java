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
    private Node head = new Node();

    /**
     * The current size of the list.
     */
    private int size;

    /**
     * Default value for elements in the list.
     */
    // @@ Please prefix class member variables with 'm'; e.g. mFoo or mBar
    private char defValue;

    /**
     * Constructs an list of the given size.
     *
     * @param size Non-negative integer size of the desired list.
     */
    public CharList(int size) {
        if(size < 0){
            throw new IndexOutOfBoundsException("Size is less than zero");
        }
	// @@ Delegate to another constructor:
        Node here = head;
        for(int i=0; i<size; i++){
            new Node(here);
            here = here.next;
        }
        this.size = size;
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
        }else{
            this.defValue = defaultValue;
            this.size = size;
            head.data = defaultValue;
            Node here = head;
            for(int i=0; i<size; i++){
                new Node(defaultValue, here);
                here = here.next;
            }
        }
    }

    /**
     * Copy constructor; creates a deep copy of the provided CharList.
     *
     * @param s The CharList to be copied.
     */
    public CharList(CharList s) {
        head.data = s.head.data;
        this.size = s.size();
        this.defValue = s.defValue;
        Node here = head;
        Node there = s.head;
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
    	return this.size;
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
            throw new IndexOutOfBoundsException("Size is less than zero");
        }
        else if(size() < size){
            Node last;

	    // @@ This is *way* too complicated - too many special cases.
	    // @@ Use a dummy node to simplify:
            if(size() != 0){
                last = seek(size()-1);
            }else{
                head = new Node();
                head.data = defValue;
                last = head;
            }
            for(int i = size(); i<size; i++){
                new Node(defValue, last);
                last = last.next;
            }
        }else{
            Node last;
            if(size() != 0){
                if(size !=0){
                    last = seek(size-1);
                    last.prune();
                }else{
                    head.prune();
                }

            }
        }
        this.size = size;
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
        Node tmp = head;
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
        for(int i=0; i<Math.min(size(), s.size()); i++){
	    // @@ Very inefficient:  How many times do you visit each node?
            char here = get(i);
            char there = s.get(i);
            if(here != there){
                return here - there;
            }
        }
        int result = size() - s.size();
	return result;
    }

    /**
     * Throws an exception if the index is out of bound.
     */
    private void rangeCheck(int index) {
        if(index >= size || index < 0)
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
            Node there = prev.next;
	    // @@ You should not be allocating a new node here:
            Node here = new Node();
            here.next = there;
            prev.next = here;
        }

        /**
         * Construct a Node from a @a value and a @a prev Node.
         */
        Node(char value, Node prev) {
	    // @@ Sam
            new Node(prev);
            Node here = prev.next;
            here.data = value;
        }

        /**
         * Ensure all subsequent nodes are properly deallocated.
         */
        void prune() {
            Node second = this.next;
            while(second.next != null){
                Node tmp = second;
                second = second.next;
                tmp.next = null;
            }
            // Leaving the list fully linked could *potentially* cause
            // a pathological performance issue for the garbage
            // collector.
        }
    }
}
