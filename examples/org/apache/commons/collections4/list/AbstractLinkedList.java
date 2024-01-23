/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.AbstractList;
//import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
//import java.util.List;
//import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections4.Collection;
import org.apache.commons.collections4.List;
import org.apache.commons.collections4.ListIterator;
import org.apache.commons.collections4.OrderedIterator;

/**
 * An abstract implementation of a linked list which provides numerous points for
 * subclasses to override.
 * <p>
 * Overridable methods are provided to change the storage node and to change how
 * nodes are added to and removed. Hopefully, all you need for unusual subclasses
 * is here.
 *
 * @since 3.0
 * @version $Id: AbstractLinkedList.java 1494296 2013-06-18 20:54:29Z tn $
 */
public abstract class AbstractLinkedList implements org.apache.commons.collections4.List {

    /*
     * Implementation notes:
     * - a standard circular doubly-linked list
     * - a marker node is stored to mark the start and the end of the list
     * - node creation and removal always occurs through createNode() and
     *   removeNode().
     * - a modification count is kept, with the same semantics as
     * {@link java.util.LinkedList}.
     * - respects {@link AbstractList#modCount}
     */

    /**
     * A {@link Node} which indicates the start and end of the list and does not
     * hold a value. The value of <code>next</code> is the first item in the
     * list. The value of of <code>previous</code> is the last item in the list.
     */
    transient Node header;

    /** The size of the list */
    transient int size;

    /** Modification count for iterators */
    transient int modCount;

    /**
     * Constructor that does nothing intended for deserialization.
     * <p>
     * If this constructor is used by a serializable subclass then the init()
     * method must be called.
     */
    protected AbstractLinkedList() {
        super();
    }

    /**
     * Constructs a list copying data from the specified collection.
     *
     * @param coll  the collection to copy
     */
//    protected AbstractLinkedList(final java.util.Collection coll) {
//        super();
//        init();
//        addAll(coll);
//    }

    /**
     * The equivalent of a default constructor, broken out so it can be called
     * by any constructor and by <code>readObject</code>.
     * Subclasses which override this method should make sure they call super,
     * so the list is initialised properly.
     */
    protected void init() {
        header = createHeaderNode();
    }

    //-----------------------------------------------------------------------

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Integer get(final int index) {
        final Node node = getNode(index, false);
        return node.getValue();
    }

    //-----------------------------------------------------------------------

    public Iterator iterator() {
        return listIterator();
    }

    public ListIterator listIterator() {
        return new LinkedListIterator(this, 0);
    }

    public ListIterator listIterator(final int fromIndex) {
        return new LinkedListIterator(this, fromIndex);
    }

    //-----------------------------------------------------------------------

    public int indexOf(final Integer value) {
        int i = 0;
        for (Node node = header.next; node != header; node = node.next) {
            if (isEqualValue(node.getValue(), value)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public int lastIndexOf(final Integer value) {
        int i = size - 1;
        for (Node node = header.previous; node != header; node = node.previous) {
            if (isEqualValue(node.getValue(), value)) {
                return i;
            }
            i--;
        }
        return -1;
    }

    public boolean contains(final Integer value) {
        return indexOf(value) != -1;
    }

    public boolean containsAll(final java.util.Collection coll) {
		return false;
    }
//        for (final Object o : coll) {
//            if (!contains((Integer)o)) {
//                return false;
//            }
//        }
//        return true;
//    }

    //-----------------------------------------------------------------------

    public Integer[] toArray() {
        return toArray(new Integer[size]);
    }

    @SuppressWarnings("unchecked")
    public  Integer[] toArray(Integer[] array) {
        // Extend the array if needed
        if (array.length < size) {
            final Class<?> componentType = array.getClass().getComponentType();
            array = (Integer[]) Array.newInstance(componentType, size);
        }
        // Copy the values into the array
        int i = 0;
        for (Node node = header.next; node != header; node = node.next, i++) {
            array[i] =  node.getValue();
        }
        // Set the value after the last value to null
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }

    /**
     * Gets a sublist of the main list.
     *
     * @param fromIndexInclusive  the index to start from
     * @param toIndexExclusive  the index to end at
     * @return the new sublist
     */
    public org.apache.commons.collections4.List subList(final int fromIndexInclusive, final int toIndexExclusive) {
        return new LinkedSubList(this, fromIndexInclusive, toIndexExclusive);
    }

    //-----------------------------------------------------------------------

    public boolean add(final Integer value) {
        addLast(value);
        return true;
    }

    public void add(final int index, final Integer value) {
        final Node node = getNode(index, true);
        addNodeBefore(node, value);
    }

    public boolean addAll(final java.util.Collection coll) {
        return addAll(size, coll);
    }

    public boolean addAll(final int index, final java.util.Collection coll) {
        final Node node = getNode(index, true);
        for (final Object e : coll) {
            addNodeBefore(node, (Integer)e);
        }
        return true;
    }
   
    
    //-----------------------------------------------------------------------

    public Integer remove(final int index) {
        final Node node = getNode(index, false);
        final Integer oldValue = node.getValue();
        removeNode(node);
        return oldValue;
    }

    public boolean remove(final Integer value) {
        for (Node node = header.next; node != header; node = node.next) {
            if (isEqualValue(node.getValue(), value)) {
                removeNode(node);
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation iterates over the elements of this list, checking each element in
     * turn to see if it's contained in <code>coll</code>. If it's contained, it's removed
     * from this list. As a consequence, it is advised to use a collection type for
     * <code>coll</code> that provides a fast (e.g. O(1)) implementation of
     * {@link Collection#contains(Object)}.
     */
    public boolean removeAll(final java.util.Collection coll) {
        boolean modified = false;
        final Iterator it = iterator();
        while (it.hasNext()) {
            if (coll.contains((Integer)it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    //-----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * <p>
     * This implementation iterates over the elements of this list, checking each element in
     * turn to see if it's contained in <code>coll</code>. If it's not contained, it's removed
     * from this list. As a consequence, it is advised to use a collection type for
     * <code>coll</code> that provides a fast (e.g. O(1)) implementation of
     * {@link Collection#contains(Object)}.
     */
    public boolean retainAll(final java.util.Collection coll) {
        boolean modified = false;
        final Iterator it = iterator();
        while (it.hasNext()) {
            if (coll.contains((Integer)it.next()) == false) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    public Integer set(final int index, final Integer value) {
        final Node node = getNode(index, false);
        final Integer oldValue = node.getValue();
        updateNode(node, value);
        return oldValue;
    }

    public void clear() {
        removeAllNodes();
    }

    //-----------------------------------------------------------------------

    public Integer getFirst() {
        final Node node = header.next;
        if (node == header) {
            throw new NoSuchElementException();
        }
        return node.getValue();
    }

    public Integer getLast() {
        final Node node = header.previous;
        if (node == header) {
            throw new NoSuchElementException();
        }
        return node.getValue();
    }

    public boolean addFirst(final Integer o) {
        addNodeAfter(header, o);
        return true;
    }

    public boolean addLast(final Integer o) {
        addNodeBefore(header, o);
        return true;
    }

    public Integer removeFirst() {
        final Node node = header.next;
        if (node == header) {
            throw new NoSuchElementException();
        }
        final Integer oldValue = node.getValue();
        removeNode(node);
        return oldValue;
    }

    public Integer removeLast() {
        final Node node = header.previous;
        if (node == header) {
            throw new NoSuchElementException();
        }
        final Integer oldValue = node.getValue();
        removeNode(node);
        return oldValue;
    }

    //-----------------------------------------------------------------------
  /*  @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof List == false) {
            return false;
        }
        final List other = (List) obj;
        if (other.size() != size()) {
            return false;
        }
        final ListIterator it1 = listIterator();
        final ListIterator it2 = other.listIterator();
        while (it1.hasNext() && it2.hasNext()) {
            final Object o1 = it1.next();
            final Object o2 = it2.next();
            if (!(o1 == null ? o2 == null : o1.equals(o2))) {
                return false;
            }
        }
        return !(it1.hasNext() || it2.hasNext());
    }
*/
    //@Override
    //public int hashCode() {
      //  int hashCode = 1;
        //for (final Object e : this) {
          //  hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        //}
        //return hashCode;
    //}

//    @Override
//    public String toString() {
//        if (size() == 0) {
//            return "[]";
//        }
//        final StringBuilder buf = new StringBuilder(16 * size());
//        buf.append('[');
//
//        final Iterator it = iterator();
//        boolean hasNext = it.hasNext();
//        while (hasNext) {
//            final Object value = it.next();
//            buf.append(value == this ? "(this Collection)" : value);
//            hasNext = it.hasNext();
//            if (hasNext) {
//                buf.append(", ");
//            }
//        }
//        buf.append(']');
//        return buf.toString();
//    }

    //-----------------------------------------------------------------------
    /**
     * Compares two values for equals.
     * This implementation uses the equals method.
     * Subclasses can override this to match differently.
     *
     * @param value1  the first value to compare, may be null
     * @param value2  the second value to compare, may be null
     * @return true if equal
     */
    protected boolean isEqualValue(final Integer value1, final Integer value2) {
        return value1 == value2 || (value1 == null ? false : value1.equals(value2));
    }

    /**
     * Updates the node with a new value.
     * This implementation sets the value on the node.
     * Subclasses can override this to record the change.
     *
     * @param node  node to update
     * @param value  new value of the node
     */
    protected void updateNode(final Node node, final Integer value) {
        node.setValue(value);
    }

    /**
     * Creates a new node with previous, next and element all set to null.
     * This implementation creates a new empty Node.
     * Subclasses can override this to create a different class.
     *
     * @return  newly created node
     */
    protected Node createHeaderNode() {
        return new Node();
    }

    /**
     * Creates a new node with the specified properties.
     * This implementation creates a new Node with data.
     * Subclasses can override this to create a different class.
     *
     * @param value  value of the new node
     * @return a new node containing the value
     */
    protected Node createNode(final Integer value) {
        return new Node(value);
    }

    /**
     * Creates a new node with the specified object as its
     * <code>value</code> and inserts it before <code>node</code>.
     * <p>
     * This implementation uses {@link #createNode(Object)} and
     * {@link #addNode(AbstractLinkedList.Node,AbstractLinkedList.Node)}.
     *
     * @param node  node to insert before
     * @param value  value of the newly added node
     * @throws NullPointerException if <code>node</code> is null
     */
    protected void addNodeBefore(final Node node, final Integer value) {
        final Node newNode = createNode(value);
        addNode(newNode, node);
    }

    /**
     * Creates a new node with the specified object as its
     * <code>value</code> and inserts it after <code>node</code>.
     * <p>
     * This implementation uses {@link #createNode(Object)} and
     * {@link #addNode(AbstractLinkedList.Node,AbstractLinkedList.Node)}.
     *
     * @param node  node to insert after
     * @param value  value of the newly added node
     * @throws NullPointerException if <code>node</code> is null
     */
    protected void addNodeAfter(final Node node, final Integer value) {
        final Node newNode = createNode(value);
        addNode(newNode, node.next);
    }

    /**
     * Inserts a new node into the list.
     *
     * @param nodeToInsert  new node to insert
     * @param insertBeforeNode  node to insert before
     * @throws NullPointerException if either node is null
     */
    protected void addNode(final Node nodeToInsert, final Node insertBeforeNode) {
        nodeToInsert.next = insertBeforeNode;
        nodeToInsert.previous = insertBeforeNode.previous;
        insertBeforeNode.previous.next = nodeToInsert;
        insertBeforeNode.previous = nodeToInsert;
        size++;
        modCount++;
    }

    /**
     * Removes the specified node from the list.
     *
     * @param node  the node to remove
     * @throws NullPointerException if <code>node</code> is null
     */
    protected void removeNode(final Node node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
        size--;
        modCount++;
    }

    /**
     * Removes all nodes by resetting the circular list marker.
     */
    protected void removeAllNodes() {
        header.next = header;
        header.previous = header;
        size = 0;
        modCount++;
    }

    /**
     * Gets the node at a particular index.
     *
     * @param index  the index, starting from 0
     * @param endMarkerAllowed  whether or not the end marker can be returned if
     * startIndex is set to the list's size
     * @return the node at the given index
     * @throws IndexOutOfBoundsException if the index is less than 0; equal to
     * the size of the list and endMakerAllowed is false; or greater than the
     * size of the list
     */
    protected Node getNode(final int index, final boolean endMarkerAllowed) throws IndexOutOfBoundsException {
        // Check the index is within the bounds
        if (index < 0) {
            throw new IndexOutOfBoundsException("Couldn't get the node: " +
                    "index (" + index + ") less than zero.");
        }
        if (!endMarkerAllowed && index == size) {
            throw new IndexOutOfBoundsException("Couldn't get the node: " +
                    "index (" + index + ") is the size of the list.");
        }
        if (index > size) {
            throw new IndexOutOfBoundsException("Couldn't get the node: " +
                    "index (" + index + ") greater than the size of the " +
                    "list (" + size + ").");
        }
        // Search the list and get the node
        Node node;
        if (index < size / 2) {
            // Search forwards
            node = header.next;
            for (int currentIndex = 0; currentIndex < index; currentIndex++) {
                node = node.next;
            }
        } else {
            // Search backwards
            node = header;
            for (int currentIndex = size; currentIndex > index; currentIndex--) {
                node = node.previous;
            }
        }
        return node;
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an iterator for the sublist.
     *
     * @param subList  the sublist to get an iterator for
     * @return a new iterator on the given sublist
     */
    protected Iterator createSubListIterator(final LinkedSubList subList) {
        return createSubListListIterator(subList, 0);
    }

    /**
     * Creates a list iterator for the sublist.
     *
     * @param subList  the sublist to get an iterator for
     * @param fromIndex  the index to start from, relative to the sublist
     * @return a new list iterator on the given sublist
     */
    protected ListIterator createSubListListIterator(final LinkedSubList subList, final int fromIndex) {
        return new LinkedSubListIterator(subList, fromIndex);
    }

    //-----------------------------------------------------------------------
    /**
     * Serializes the data held in this object to the stream specified.
     * <p>
     * The first serializable subclass must call this method from
     * <code>writeObject</code>.
     *
     * @param outputStream  the stream to write the object to
     * @throws IOException  if anything goes wrong
     */
    protected void doWriteObject(final ObjectOutputStream outputStream) throws IOException {
        // Write the size so we know how many nodes to read back
        outputStream.writeInt(size());
        for (Node node = header.next; node != header; node = node.next) {

        //for (final Object e : this) {
            outputStream.writeObject(node.value);
        }
    }

    /**
     * Deserializes the data held in this object to the stream specified.
     * <p>
     * The first serializable subclass must call this method from
     * <code>readObject</code>.
     *
     * @param inputStream  the stream to read the object from
     * @throws IOException  if any error occurs while reading from the stream
     * @throws ClassNotFoundException  if a class read from the stream can not be loaded
     */
    @SuppressWarnings("unchecked")
    protected void doReadObject(final ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        init();
        final int size = inputStream.readInt();
        for (int i = 0; i < size; i++) {
            add((Integer) inputStream.readObject());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * A node within the linked list.
     * <p>
     * From Commons Collections 3.1, all access to the <code>value</code> property
     * is via the methods on this class.
     */
    protected static class Node  implements java.io.Serializable{

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/** A pointer to the node before this node */
        protected Node previous;
        /** A pointer to the node after this node */
        protected Node next;
        /** The object contained within this node */
        protected Integer value;

        /**
         * Constructs a new header node.
         */
        protected Node() {
            super();
            previous = this;
            next = this;
        }

        /**
         * Constructs a new node.
         *
         * @param value  the value to store
         */
        protected Node(final Integer value) {
            super();
            this.value = value;
        }

        /**
         * Constructs a new node.
         *
         * @param previous  the previous node in the list
         * @param next  the next node in the list
         * @param value  the value to store
         */
        protected Node(final Node previous, final Node next, final Integer value) {
            super();
            this.previous = previous;
            this.next = next;
            this.value = value;
        }

        /**
         * Gets the value of the node.
         *
         * @return the value
         * @since 3.1
         */
        protected Integer getValue() {
            return value;
        }

        /**
         * Sets the value of the node.
         *
         * @param value  the value
         * @since 3.1
         */
        protected void setValue(final Integer value) {
            this.value = value;
        }

        /**
         * Gets the previous node.
         *
         * @return the previous node
         * @since 3.1
         */
        protected Node getPreviousNode() {
            return previous;
        }

        /**
         * Sets the previous node.
         *
         * @param previous  the previous node
         * @since 3.1
         */
        protected void setPreviousNode(final Node previous) {
            this.previous = previous;
        }

        /**
         * Gets the next node.
         *
         * @return the next node
         * @since 3.1
         */
        protected Node getNextNode() {
            return next;
        }

        /**
         * Sets the next node.
         *
         * @param next  the next node
         * @since 3.1
         */
        protected void setNextNode(final Node next) {
            this.next = next;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * A list iterator over the linked list.
     */
    
    protected static class LinkedListIterator implements org.apache.commons.collections4.ListIterator, OrderedIterator {

        /** The parent list */
        protected final AbstractLinkedList parent;

        /**
         * The node that will be returned by {@link #next()}. If this is equal
         * to {@link AbstractLinkedList#header} then there are no more values to return.
         */
        protected Node next;

        /**
         * The index of {@link #next}.
         */
        protected int nextIndex;

        /**
         * The last node that was returned by {@link #next()} or {@link
         * #previous()}. Set to <code>null</code> if {@link #next()} or {@link
         * #previous()} haven't been called, or if the node has been removed
         * with {@link #remove()} or a new node added with {@link #add(Object)}.
         * Should be accessed through {@link #getLastNodeReturned()} to enforce
         * this behaviour.
         */
        protected Node current;

        /**
         * The modification count that the list is expected to have. If the list
         * doesn't have this count, then a
         * {@link java.util.ConcurrentModificationException} may be thrown by
         * the operations.
         */
        protected int expectedModCount;

        /**
         * Create a ListIterator for a list.
         *
         * @param parent  the parent list
         * @param fromIndex  the index to start at
         * @throws IndexOutOfBoundsException if fromIndex is less than 0 or greater than the size of the list
         */
        protected LinkedListIterator(final AbstractLinkedList parent, final int fromIndex)
                throws IndexOutOfBoundsException {
            super();
            this.parent = parent;
            this.expectedModCount = parent.modCount;
            this.next = parent.getNode(fromIndex, true);
            this.nextIndex = fromIndex;
        }

        /**
         * Checks the modification count of the list is the value that this
         * object expects.
         *
         * @throws ConcurrentModificationException If the list's modification
         * count isn't the value that was expected.
         */
        protected void checkModCount() {
            if (parent.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        /**
         * Gets the last node returned.
         *
         * @return the last node returned
         * @throws IllegalStateException If {@link #next()} or {@link #previous()} haven't been called,
         * or if the node has been removed with {@link #remove()} or a new node added with {@link #add(Object)}.
         */
        protected Node getLastNodeReturned() throws IllegalStateException {
            if (current == null) {
                throw new IllegalStateException();
            }
            return current;
        }

        public boolean hasNext() {
            return next != parent.header;
        }

        public Object next() {
            checkModCount();
            if (!hasNext()) {
                throw new NoSuchElementException("No element at index " + nextIndex + ".");
            }
            final Object value = next.getValue();
            current = next;
            next = next.next;
            nextIndex++;
            return value;
        }

        public boolean hasPrevious() {
            return next.previous != parent.header;
        }

        public Object previous() {
            checkModCount();
            if (!hasPrevious()) {
                throw new NoSuchElementException("Already at start of list.");
            }
            next = next.previous;
            final Object value = next.getValue();
            current = next;
            nextIndex--;
            return value;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            // not normally overridden, as relative to nextIndex()
            return nextIndex() - 1;
        }

        public void remove() {
            checkModCount();
            if (current == next) {
                // remove() following previous()
                next = next.next;
                parent.removeNode(getLastNodeReturned());
            } else {
                // remove() following next()
                parent.removeNode(getLastNodeReturned());
                nextIndex--;
            }
            current = null;
            expectedModCount++;
        }

        public void set(final Integer obj) {
            checkModCount();
            getLastNodeReturned().setValue(obj);
        }

        public void add(final Integer obj) {
            checkModCount();
            parent.addNodeBefore(next, obj);
            current = null;
            nextIndex++;
            expectedModCount++;
        }

    }

    //-----------------------------------------------------------------------
    /**
     * A list iterator over the linked sub list.
     */
    protected static class LinkedSubListIterator extends LinkedListIterator {

        /** The parent list */
        protected final LinkedSubList sub;

        protected LinkedSubListIterator(final LinkedSubList sub, final int startIndex) {
            super(sub.parent, startIndex + sub.offset);
            this.sub = sub;
        }

        @Override
        public boolean hasNext() {
            return nextIndex() < sub.size;
        }

        @Override
        public boolean hasPrevious() {
            return previousIndex() >= 0;
        }

        @Override
        public int nextIndex() {
            return super.nextIndex() - sub.offset;
        }

        @Override
        public void add(final Integer obj) {
            super.add(obj);
            sub.expectedModCount = parent.modCount;
            sub.size++;
        }

        @Override
        public void remove() {
            super.remove();
            sub.expectedModCount = parent.modCount;
            sub.size--;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * The sublist implementation for AbstractLinkedList.
     */
    protected static class LinkedSubList extends org.apache.commons.collections4.AbstractList {
        /** The main list */
        AbstractLinkedList parent;
        /** Offset from the main list */
        int offset;
        /** Sublist size */
        int size;
        /** Sublist modCount */
        int expectedModCount;

        protected LinkedSubList(final AbstractLinkedList parent, final int fromIndex, final int toIndex) {
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
            }
            if (toIndex > parent.size()) {
                throw new IndexOutOfBoundsException("toIndex = " + toIndex);
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
            }
            this.parent = parent;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
            this.expectedModCount = parent.modCount;
        }

        @Override
        public int size() {
            checkModCount();
            return size;
        }

        @Override
        public Integer get(final int index) {
            rangeCheck(index, size);
            checkModCount();
            return parent.get(index + offset);
        }

        @Override
        public void add(final int index, final Integer obj) {
            rangeCheck(index, size + 1);
            checkModCount();
            parent.add(index + offset, obj);
            expectedModCount = parent.modCount;
            size++;
            LinkedSubList.this.modCount++;
        }

        @Override
        public Integer remove(final int index) {
            rangeCheck(index, size);
            checkModCount();
            final Integer result = parent.remove(index + offset);
            expectedModCount = parent.modCount;
            size--;
            LinkedSubList.this.modCount++;
            return result;
        }

        @Override
        public boolean addAll(final java.util.Collection coll) {
            return addAll(size, coll);
        }

        @Override
        public boolean addAll(final int index, final java.util.Collection coll) {
            rangeCheck(index, size + 1);
            final int cSize = coll.size();
            if (cSize == 0) {
                return false;
            }

            checkModCount();
            parent.addAll(offset + index, coll);
            expectedModCount = parent.modCount;
            size += cSize;
            LinkedSubList.this.modCount++;
            return true;
        }

        @Override
        public Integer set(final int index, final Integer obj) {
            rangeCheck(index, size);
            checkModCount();
            return parent.set(index + offset, obj);
        }

        @Override
        public void clear() {
            checkModCount();
            final Iterator it = iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }

        @Override
        public Iterator iterator() {
            checkModCount();
            return parent.createSubListIterator(this);
        }

        @Override
        public ListIterator listIterator(final int index) {
            rangeCheck(index, size + 1);
            checkModCount();
            return parent.createSubListListIterator(this, index);
        }

        //@Override
       // public List subList(final int fromIndexInclusive, final int toIndexExclusive) {
        //    return new LinkedSubList(parent, fromIndexInclusive + offset, toIndexExclusive + offset);
        //}

        protected void rangeCheck(final int index, final int beyond) {
            if (index < 0 || index >= beyond) {
                throw new IndexOutOfBoundsException("Index '" + index + "' out of bounds for size '" + size + "'");
            }
        }

        protected void checkModCount() {
            if (parent.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

		@Override
		public Integer[] toArray(Integer[] a) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean containsAll(java.util.Collection c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean removeAll(java.util.Collection c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean retainAll(java.util.Collection c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public List subList(int fromIndex, int toIndex) {
			// TODO Auto-generated method stub
			return null;
		}
    }

}