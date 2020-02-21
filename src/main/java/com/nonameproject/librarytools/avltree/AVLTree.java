package com.nonameproject.librarytools.avltree;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AVLTree
{
    boolean acceptDuplicates = false;

    public AVLTree() {
    }

    public AVLTree(boolean pAcceptDuplicates) {
        acceptDuplicates = pAcceptDuplicates;
    }

    private int height(Node node)
    {
        if (node == null)
            return 0;
        return node.height;
    }

    public Node insert(Node node, long pWeight) {
        return insert(node, pWeight, null);
    }

    public Node insert(Node node, long pWeight, Object pObject)
    {
        /* 1. Perform the normal BST rotation */
        if (node == null)
        {
            return (new Node(pWeight, pObject));
        }
        if (pWeight != node.weight || acceptDuplicates)
        {
            if (pWeight < node.weight)
                node.left = insert(node.left, pWeight, pObject);
            else
                node.right = insert(node.right, pWeight, pObject);
        } else return null;
        /* 2. Update height of this ancestor node */
        node.height = Math.max(height(node.left), height(node.right)) + 1;

        /*
         * 3. Get the balance factor of this ancestor node to check whether this
         * node became unbalanced
         */
        int balance = getBalance(node);

        // If this node becomes unbalanced, then there are 4 cases

        // Left Left Case
        if (balance > 1 && pWeight < node.left.weight)
            return rightRotate(node);

        // Right Right Case
        if (balance < -1 && pWeight > node.right.weight)
            return leftRotate(node);

        // Left Right Case
        if (balance > 1 && pWeight > node.left.weight)
        {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && pWeight < node.right.weight)
        {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        /* return the (unchanged) node pointer */
        return node;
    }

    private Node rightRotate(Node y)
    {
        Node x = y.left;
        Node T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        // Return new root
        return x;
    }

    private Node leftRotate(Node x)
    {
        Node y = x.right;
        Node T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        // Return new root
        return y;
    }

    // Get Balance factor of node N
    private int getBalance(Node N)
    {
        if (N == null)
            return 0;
        return height(N.left) - height(N.right);
    }

    private Node minValueNode(Node node)
    {
        Node current = node;
        /* loop down to find the leftmost leaf */
        while (current.left != null)
            current = current.left;
        return current;
    }

    public Node deleteNode(Node root, long pWeigth, String pNodeId)
    {
        // STEP 1: PERFORM STANDARD BST DELETE

        if (root == null)
            return root;

        // If the value to be deleted is smaller than the root's value,
        // then it lies in left subtree
        if (pWeigth < root.weight)
            root.left = deleteNode(root.left, pWeigth, pNodeId);

            // If the value to be deleted is greater than the root's value,
            // then it lies in right subtree
        else if (pWeigth > root.weight)
            root.right = deleteNode(root.right, pWeigth, pNodeId);

            // if value is same as root's value, then check if it is the same node.
            // to be deleted
        else if (!acceptDuplicates || root.id.equals(pNodeId))
        {
            // node with only one child or no child
            if ((root.left == null) || (root.right == null))
            {

                Node temp;
                if (root.left != null)
                    temp = root.left;
                else
                    temp = root.right;

                // No child case
                if (temp == null)
                {
                    temp = root;
                    root = null;
                }
                else
                    // One child case
                    root = temp; // Copy the contents of the non-empty child

                temp = null;
            }
            else
            {
                // node with two children: Get the inorder successor (smallest
                // in the right subtree)
                Node temp = minValueNode(root.right);

                // Copy the inorder successor's data to this node
                root.weight = temp.weight;

                // Delete the inorder successor
                root.right = deleteNode(root.right, temp.weight);
            }
        } else {
            // It's not this node, but it should be one of the childs.
            root.right = deleteNode(root.right, pWeigth, pNodeId);
            root.left = deleteNode(root.left, pWeigth, pNodeId);
        }

        // If the tree had only one node then return
        if (root == null)
            return root;

        // STEP 2: UPDATE HEIGHT OF THE CURRENT NODE
        root.height = Math.max(height(root.left), height(root.right)) + 1;

        // STEP 3: GET THE BALANCE FACTOR OF THIS NODE (to check whether
        // this node became unbalanced)
        int balance = getBalance(root);

        // If this node becomes unbalanced, then there are 4 cases

        // Left Left Case
        if (balance > 1 && getBalance(root.left) >= 0)
            return rightRotate(root);

        // Left Right Case
        if (balance > 1 && getBalance(root.left) < 0)
        {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }

        // Right Right Case
        if (balance < -1 && getBalance(root.right) <= 0)
            return leftRotate(root);

        // Right Left Case
        if (balance < -1 && getBalance(root.right) > 0)
        {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    public Node deleteNode(Node root, long value)
    {
        // STEP 1: PERFORM STANDARD BST DELETE

        if (root == null)
            return root;

        // If the value to be deleted is smaller than the root's value,
        // then it lies in left subtree
        if (value < root.weight)
            root.left = deleteNode(root.left, value);

            // If the value to be deleted is greater than the root's value,
            // then it lies in right subtree
        else if (value > root.weight)
            root.right = deleteNode(root.right, value);

            // if value is same as root's value, then This is the node
            // to be deleted
        else
        {
            // node with only one child or no child
            if ((root.left == null) || (root.right == null))
            {

                Node temp;
                if (root.left != null)
                    temp = root.left;
                else
                    temp = root.right;

                // No child case
                if (temp == null)
                {
                    temp = root;
                    root = null;
                }
                else
                    // One child case
                    root = temp; // Copy the contents of the non-empty child

                temp = null;
            }
            else
            {
                // node with two children: Get the inorder successor (smallest
                // in the right subtree)
                Node temp = minValueNode(root.right);

                // Copy the inorder successor's data to this node
                root.weight = temp.weight;

                // Delete the inorder successor
                root.right = deleteNode(root.right, temp.weight);
            }
        }

        // If the tree had only one node then return
        if (root == null)
            return root;

        // STEP 2: UPDATE HEIGHT OF THE CURRENT NODE
        root.height = Math.max(height(root.left), height(root.right)) + 1;

        // STEP 3: GET THE BALANCE FACTOR OF THIS NODE (to check whether
        // this node became unbalanced)
        int balance = getBalance(root);

        // If this node becomes unbalanced, then there are 4 cases

        // Left Left Case
        if (balance > 1 && getBalance(root.left) >= 0)
            return rightRotate(root);

        // Left Right Case
        if (balance > 1 && getBalance(root.left) < 0)
        {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }

        // Right Right Case
        if (balance < -1 && getBalance(root.right) <= 0)
            return leftRotate(root);

        // Right Left Case
        if (balance < -1 && getBalance(root.right) > 0)
        {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    public void print(Node root)
    {

        if (root == null)
        {
            System.out.println("(XXXXXX)");
            return;
        }

        int height = root.height, width = (int) Math.pow(2, height - 1);

        // Preparing variables for loop.
        List<Node> current = new ArrayList<Node>(1), next = new ArrayList<Node>(
                2);
        current.add(root);

        final int maxHalfLength = 4;
        int elements = 1;

        StringBuilder sb = new StringBuilder(maxHalfLength * width);
        for (int i = 0; i < maxHalfLength * width; i++)
            sb.append(' ');
        String textBuffer;

        // Iterating through height levels.
        for (int i = 0; i < height; i++)
        {

            sb.setLength(maxHalfLength
                    * ((int) Math.pow(2, height - 1 - i) - 1));

            // Creating spacer space indicator.
            textBuffer = sb.toString();

            // Print tree node elements
            for (Node n : current)
            {

                System.out.print(textBuffer);

                if (n == null)
                {

                    System.out.print("        ");
                    next.add(null);
                    next.add(null);

                }
                else
                {

                    System.out.printf("(%6d)", n.weight);
                    next.add(n.left);
                    next.add(n.right);

                }

                System.out.print(textBuffer);

            }

            System.out.println();
            // Print tree node extensions for next level.
            if (i < height - 1)
            {

                for (Node n : current)
                {

                    System.out.print(textBuffer);

                    if (n == null)
                        System.out.print("        ");
                    else
                        System.out.printf("%s      %s", n.left == null ? " "
                                : "/", n.right == null ? " " : "\\");

                    System.out.print(textBuffer);

                }

                System.out.println();

            }

            // Renewing indicators for next run.
            elements *= 2;
            current = next;
            next = new ArrayList<Node>(elements);

        }

    }

    public static void listValues(Node root)
    {
        if (root.left != null)
        {
            listValues(root.left);
            System.out.println(root.weight);
            if (root.right != null)
            {
                listValues(root.right);
            }
            else
            {
                System.out.println(root.weight);
            }
        }
        else
        {
            System.out.println(root.weight);
            if (root.right != null)
            {
                System.out.println(root.right.weight);
            }
        }
    }

    public static Node getMaxValue(Node root){
        if (root.right != null) {
            return getMaxValue(root.right);
        } else {
            return root;
        }
    }

    public static Node getMinValue(Node root){
        if (root.left != null) {
            return getMaxValue(root.left);
        } else {
            return root;
        }
    }

    public static void writeToFile(Node root, BufferedWriter wr) throws IOException
    {

        if (root.left != null)
        {
            writeToFile(root.left, wr);
            wr.write("" + root.weight);
            wr.newLine();
            if (root.right != null)
            {
                writeToFile(root.right, wr);
            }
            else
            {
                wr.write("" + root.weight);
                wr.newLine();
            }
        }
        else
        {
            wr.write("" + root.weight);
            wr.newLine();
            if (root.right != null)
            {
                wr.write("" + root.right.weight);
                wr.newLine();
            }
        }

    }

    public boolean isAcceptDuplicates() {
        return acceptDuplicates;
    }

    public void setAcceptDuplicates(boolean pAcceptDuplicates) {
        acceptDuplicates = pAcceptDuplicates;
    }
}
