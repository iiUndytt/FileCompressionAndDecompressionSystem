/**
 * A node in the Huffman tree. Leaf nodes hold an actual character;
 * internal nodes use '\0' as a placeholder and just carry a combined frequency.
 */
class Node implements Comparable<Node> {

    char character;
    int frequency;
    Node left, right;

    // Leaf node
    Node(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
    }

    // Internal node
    Node(int frequency, Node left, Node right) {
        this.character = '\0';
        this.frequency = frequency;
        this.left  = left;
        this.right = right;
    }

    boolean isLeaf() {
        return left == null && right == null;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.frequency, other.frequency);
    }
}