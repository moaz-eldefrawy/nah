import javax.management.RuntimeErrorException;

public class RedBlackTree<T extends Comparable<T>, V> implements IRedBlackTree<T, V> {
    private INode root;
     int size;

    @Override
    public INode<T, V> getRoot() {
        return this.root;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public V search(T key) {
        if (key == null) throw new RuntimeErrorException(new Error());
        INode<T, V> cur = getRoot();
        while (cur != null && !cur.isNull()) {
            if (cur.getKey().compareTo(key) > 0)
                cur = cur.getLeftChild();
            else if (cur.getKey().compareTo(key) < 0)
                cur = cur.getRightChild();
            else
                return cur.getValue();
        }
        return null;
    }

    @Override
    public boolean contains(T key) {
        if (key == null) {
            throw new RuntimeErrorException(new Error());
        }
        return search(key) != null;
    }

    @Override
    public void insert(T key, V value) {
        if (key == null || value == null) throw new RuntimeErrorException(new Error());
        if(size > 0 && getRoot().getColor() == INode.RED ) System.out.println("Problem with the root ");
        if (isEmpty()) {
            root = new Node(key, value, INode.BLACK); // Root is always black
            size++;
            return;
        }
        INode<T, V> cur = getRoot();
        INode<T, V> parent = getRoot();
        INode<T, V> newNode = new Node(key, value, INode.RED);
        while (!cur.isNull()) {
            parent = cur;
            if (cur.getKey().compareTo(key) > 0)
                cur = cur.getLeftChild();
            else if (cur.getKey().compareTo(key) < 0)
                cur = cur.getRightChild();
            else {
                cur.setValue(value);
                return;
            }
        }
        size++;
        if (parent.getKey().compareTo(key) > 0)
            parent.setLeftChild(newNode);
        else
            parent.setRightChild(newNode);
        newNode.setParent(parent);
        checkConflict(newNode);


    }

    public void checkConflict(INode leaf) {
        INode parent = leaf.getParent();
        if (leaf.getColor() == INode.RED && parent.getColor() == INode.RED) {
            INode ancestor = getAncestor(leaf);
            INode uncle = getUncle(leaf);
            if (uncle != null && !uncle.isNull() && uncle.getColor() == INode.RED) {
                uncle.setColor(INode.BLACK);
                parent.setColor(INode.BLACK);
                if (ancestor != getRoot()) {
                    ancestor.setColor(INode.RED);
                    checkConflict(ancestor);
                }
            } else {
                /*here where the uncle is black so we have to do the suitable rotation and recoloring and fortunately there are no
                 recursive calls here */
                boolean leftLeftChain = ancestor.getLeftChild() == parent && parent.getLeftChild() == leaf;
                boolean leftRightChain = ancestor.getLeftChild() == parent && parent.getRightChild() == leaf;
                boolean rightRightChain = ancestor.getRightChild() == parent && parent.getRightChild() == leaf;
                boolean rightLeftChain = ancestor.getRightChild() == parent && parent.getLeftChild() == leaf;


                ancestor.setColor(INode.RED);
                if (leftLeftChain) {
                    parent.setColor(INode.BLACK);
                    rightRotate(ancestor);
                } else if (leftRightChain) {
                    leaf.setColor(INode.BLACK);
                    leftRotate(parent);
                    rightRotate(ancestor);
                } else if (rightRightChain) {
                    parent.setColor(INode.BLACK);
                    leftRotate(ancestor);
                } else if (rightLeftChain) {
                    leaf.setColor(INode.BLACK);
                    rightRotate(parent);
                    leftRotate(ancestor);
                } else
                    System.out.println("Something wrong is going on ");

            }
        }
        return;
    }

    @Override
    public boolean delete(T key) {
        if(size > 0 && getRoot().getColor() == INode.RED ) System.out.println("Problem with the root ");
        if (key == null) throw new RuntimeErrorException(new Error());
        INode node = find(key);
        if (node == null) {
            return false;
        } else {
            delete(node);
            size--;
            return true;
        }

    }

    /**
     * @param key the key of the desired node to find
     * @return the node carrying this key otherwise it returns Null
     */
    private INode find(T key) {
        INode cur = getRoot();
        while (cur != null && !cur.isNull()) {
            if (cur.getKey().compareTo(key) > 0) {
                cur = cur.getLeftChild();
            } else if (cur.getKey().compareTo(key) < 0) {
                cur = cur.getRightChild();
            } else
                return cur;

        }
        return null;
    }

    /**
     * @param node the node to be deleted which should satisfy one of  the following conditions
     *             1- To be leaf
     *             2 - to be a node with no right children (hence it will have at most one red left child
     */
    private void delete(INode node) {
        if (size == 1) {
            root = null ;
            return;
        }
        INode inorderSuccessor = getSmallestNode(node.getRightChild());
        if (inorderSuccessor != null) {
            node.setKey(inorderSuccessor.getKey());
            node.setValue(inorderSuccessor.getValue());
            delete(inorderSuccessor);
            return;
        }
        if (!node.getLeftChild().isNull()) {
            // then this node should be colored in red color :)
            node.setKey(node.getLeftChild().getKey());
            node.setValue(node.getLeftChild().getValue());
            node.getLeftChild().setParent(null); // we don't want it so far
            node.setLeftChild(new Node());
            return;
        }
        INode parent = node.getParent();
        if (node.getColor() == INode.RED) {
            node.setParent(null);
            if (parent.getRightChild() == node)
                parent.setRightChild(new Node()); // make it Nil node ;
            else if (parent.getLeftChild() == node)
                parent.setLeftChild(new Node());

            return;
        }
        // here is where the node has no children
        if (parent.getLeftChild() == node) {
            node.setParent(null);
            node = new Node();
            node.setParent(parent);
            parent.setLeftChild(node);
            doubleBlackFix(parent.getLeftChild());
        } else {
            node.setParent(null);
            node = new Node();
            node.setParent(parent);
            parent.setRightChild(node);
            doubleBlackFix(parent.getRightChild());

        }


    }

    /**
     * @param node the node which had a double black property
     *             This method does not delete any thing from the tree
     */
    private void doubleBlackFix(INode node) {
        if (node == root) {
            return;
        }
        INode sibling = getSibling(node); // the sibling can not be null
        INode parent = node.getParent();
        if (sibling.getColor() == INode.RED) {
            sibling.setColor(INode.BLACK);
            parent.setColor(INode.RED); // the parent in this cas was black and can not be red :)
            suitableRotate(parent, sibling);
            doubleBlackFix(node);
        } else if (sibling.getColor() == INode.BLACK) {
            INode furthestSiblingChild = getFurthestSiblingChild(node);
            INode nearestSiblingChild = getNearestSiblingChild(node);

            if (furthestSiblingChild != null && !furthestSiblingChild.isNull() && furthestSiblingChild.getColor() == INode.RED) {
                furthestSiblingChild.setColor(INode.BLACK);
                /*
                Exchange the color of the sibling with its parent
                 */
                boolean siblingColor = sibling.getColor();
                sibling.setColor(parent.getColor());
                parent.setColor(siblingColor);

                suitableRotate(parent, sibling);
                return;
            } else if (nearestSiblingChild != null && !nearestSiblingChild.isNull() && nearestSiblingChild.getColor() == INode.RED) {
                nearestSiblingChild.setColor(INode.BLACK);
                sibling.setColor(INode.RED);
                suitableRotate(sibling, nearestSiblingChild);
                doubleBlackFix(node);
            } else { // BOTH CHILDREN ARE BLACK
                sibling.setColor(INode.RED);
                if (parent.getColor() == INode.RED)
                    parent.setColor(INode.BLACK);
                else
                    doubleBlackFix(parent);
            }
        } else
            throw new IllegalStateException("black Node has no sibling !!");

    }


    private INode getNearestSiblingChild(INode node) {
        INode sibling = getSibling(node);
        if (sibling.isNull()) return null;
        INode siblingLeftChildren = sibling.getLeftChild();
        INode siblingRightChildren = sibling.getRightChild();
        if (node.getParent().getLeftChild() == node)
            return siblingLeftChildren;
        else
            return siblingRightChildren;
    }

    private INode getFurthestSiblingChild(INode node) {
        INode sibling = getSibling(node);
        if (sibling.isNull()) return null;
        INode siblingLeftChildren = sibling.getLeftChild();
        INode siblingRightChildren = sibling.getRightChild();
        if (node.getParent().getLeftChild() == node)
            return siblingRightChildren;
        else
            return siblingLeftChildren;

    }
    /**
     *
     * @param root the root of the subtree (or tree) that we want to get the smallest element in .
     * @returns the node carrying the smallest key and null otherwise
     * */
    /**
     * @param A the parent node
     * @param B the child node
     */


    private void suitableRotate(INode A, INode B) {
        if (A.getLeftChild() == B)
            rightRotate(A);
        else
            leftRotate(A);
    }

    private INode getSmallestNode(INode root) {
        if (root == null || root.isNull())
            return null;
        if (root.getLeftChild() == null || root.getLeftChild().isNull())
            return root;
        else
            return getSmallestNode(root.getLeftChild());
    }

    private INode getAncestor(INode node) {
        if (node.isNull() || node.getParent().isNull())
            return null;
        else if (node == null) {
            System.out.println("Something is wrong");
            return null;
        }
        return node.getParent().getParent();
    }

    private INode getSibling(INode node) {
        if (node == null || node.getParent() == null)
            return null;
        INode parent = node.getParent();
        if (parent.getLeftChild() == node)
            return parent.getRightChild();
        else
            return parent.getLeftChild();
    }

    private INode getUncle(INode node) {
        INode parent = node.getParent();
        if (node.getParent().isNull() || parent.getParent().isNull())
            return null;

        INode ancestor = parent.getParent();
        if (ancestor.getLeftChild() == parent)
            return ancestor.getRightChild();
        else
            return ancestor.getLeftChild();
    }

    private INode rightRotate(INode A) {
        boolean changeRoot = A == root;
        INode parent = A.getParent();
        INode B = A.getLeftChild();
        INode C = B.getRightChild();
        B.setParent(parent);
        A.setParent(B);
        A.setLeftChild(C);
        B.setRightChild(A);
        if (C != null && !C.isNull()) {
            C.setParent(A);
        }

        if (parent != null) {
            if (parent.getKey().compareTo(B.getKey()) > 0)
                parent.setLeftChild(B);
            else
                parent.setRightChild(B);
        }
        if (changeRoot) root = B;

        return B;
    }

    // Node A, B should not be null :)
    private INode leftRotate(INode A) {
        boolean changeRoot = A == root;
        INode parent = A.getParent();
        INode B = A.getRightChild();
        INode C = B.getLeftChild();
        B.setParent(parent);
        B.setLeftChild(A);
        A.setParent(B);
        A.setRightChild(C);

        if (C != null && !C.isNull()) {

            C.setParent(A);
        }


        if (parent != null) {
            if (parent.getKey().compareTo(B.getKey()) > 0)
                parent.setLeftChild(B);
            else
                parent.setRightChild(B);
        }
        if (changeRoot) root = B;
        return B;

    }

    private void print(INode root) {
//        INode node = getRoot() ;
        if (getRoot().getParent() != null) {
            System.out.println("Root has a parent something went wrong ");
        }
        if (root == null || root.isNull()) return;
        print(root.getLeftChild());
        System.out.print(root.getKey() + " ");
        print(root.getRightChild());
    }

    public static void main(String[] args) {

        RedBlackTree<Integer, Integer> redBlackTree = new RedBlackTree<>();
        redBlackTree.insert(5, 5);
        redBlackTree.insert(2, 2);
        redBlackTree.insert(16, 16);
        redBlackTree.insert(13, 13);
        RBTreePrinter.print(redBlackTree.getRoot());
        redBlackTree.delete(2);
        RBTreePrinter.print(redBlackTree.getRoot());


//        System.out.println();
//        redBlackTree.print(redBlackTree.getRoot());
    }


}
