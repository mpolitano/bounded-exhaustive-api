/**
 * Semi-automatically generated files used to measure predicate coverage.
 * Details can be found at http://mir.cs.illinois.edu/coverage
 */
package examples.issta2006.bintree;

import java.io.Serializable;

class Node implements Serializable  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3476384570674504637L;

	public int value;

    public Node left, right;

    public Node(int x) {
        value = x;
        left = null;
        right = null;
    }

    @Override
    public String toString() {
        String res = "{" + value + " {"
                + (left != null ? left.toString() : null) + "}" + " {"
                + (right != null ? right.toString() : null) + "}}";
        return res;
    }
}

public class BinTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1132306315762456971L;

	private Node root;
    private int size;
    
    public BinTree() {
        root = null;
    }
    
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "size: " + size + ", tree: " + (root != null ? root.toString() : "null");
    }

    public void add(int x) {
        Node current = root;
        if (root == null) {
            root = new Node(x);
            size++;
            return;
        } else {
        }

        while (current.value != x) {
            if (x < current.value) {
                if (current.left == null) {
                    current.left = new Node(x);
                    size++;
                } else {
                    current = current.left;
                }
            } else {
                if (current.right == null) {
                    current.right = new Node(x);
                    size++;
                } else {
                    current = current.right;
                }
            }
        }
    }
    
    public boolean find(int x) {
        Node current = root;
        while (current != null) {
            if (current.value == x) {
                return true;
            } else {
            }

            if (x < current.value) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return false;
    }
	public void remove(int key) {
	    root = removeNode(root, key);
	}
	
	private Node removeNode(Node n, int key) {
	    if (n == null) {
	        return null;
	    }
	    
	    if (key ==  n.value) {
	        // n is the node to be removed
	        if (n.left == null && n.right == null) {
	        	size--;
	            return null;
	        }
	        if (n.left == null) {
	        	size--;
	            return n.right;
	        }
	        if (n.right == null) {
	        	size--;
	        	//size--;
	            return n.left;
	        }
	        int smallVal = smallest(n.right);
	        n.value = smallVal;
	        n.right = removeNode(n.right, smallVal);
	        return n; 
	    }
	    else if (key < n.value) {
	        n.left = removeNode(n.left, key);
	        return n;
	    }
	    else {
	        n.right = removeNode(n.right, key);
	        return n;
	    }
	}
	
	private int smallest(Node n){
	    if (n.left == null) {
	        return n.value;
	    } else {
	        return smallest(n.left);
	    }
	}

    private static int NUM_OF_PREDICATES = 7;

    public static int numOfPredicates() {
        return NUM_OF_PREDICATES;
    }

}
