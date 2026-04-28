import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


public class HuffmanCompression {

    private static Node currentTreeRoot = null;
    private static String currentCompressedStream = null;

    // Count how often each character appears in the input
    public static Map<Character, Integer> countFrequency(String text) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : text.toCharArray())
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        return freq;
    }

    // Build the Huffman tree bottom-up using a min-heap
    public static Node buildHuffmanTree(Map<Character, Integer> freq) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : freq.entrySet())
            pq.add(new Node(entry.getKey(), entry.getValue()));

        // Edge case: only one distinct character in the input
        if (pq.size() == 1) {
            Node only = pq.poll();
            return new Node(only.frequency, only, null);
        }

        while (pq.size() > 1) {
            Node left  = pq.poll();
            Node right = pq.poll();
            pq.add(new Node(left.frequency + right.frequency, left, right));
        }
        return pq.poll();
    }

    // Walk the tree to assign a binary code to every leaf character
    public static void generateCodes(Node node, String path, Map<Character, String> codes) {
        if (node == null) return;

        if (node.isLeaf()) {
            // If the root itself is a leaf (single unique char), give it "0"
            codes.put(node.character, path.isEmpty() ? "0" : path);
            return;
        }

        generateCodes(node.left,  path + "0", codes);
        generateCodes(node.right, path + "1", codes);
    }

    // Replace each character with its Huffman code
    public static String encode(String text, Map<Character, String> codes) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray())
            sb.append(codes.get(c));
        return sb.toString();
    }

    // Walk the tree bit-by-bit to recover the original text
    public static String decode(String bits, Node root) {
        if (bits == null || bits.isEmpty() || root == null) return "";

        StringBuilder result = new StringBuilder();
        Node current = root;

        for (int i = 0; i < bits.length(); i++) {
            // Guard against a corrupted/mismatched bit stream
            if (current == null) {
                System.out.println("[!] Bit stream appears to be corrupted.");
                break;
            }

            current = (bits.charAt(i) == '0') ? current.left : current.right;

            if (current != null && current.isLeaf()) {
                result.append(current.character);
                current = root;
            }
        }
        return result.toString();
    }

    // Run the full compression pipeline and print the stats
    private static void processCompression(String text) {
        if (text == null || text.isEmpty()) {
            System.out.println("[!] Cannot compress empty text.");
            return;
        }

        System.out.println("\nAnalyzing character frequencies...");
        Map<Character, Integer> freq = countFrequency(text);

        System.out.println("Building Huffman tree...");
        currentTreeRoot = buildHuffmanTree(freq);

        Map<Character, String> codes = new HashMap<>();
        generateCodes(currentTreeRoot, "", codes);

        System.out.println("Encoding...");
        currentCompressedStream = encode(text, codes);

        int originalBits   = text.length() * 8;
        int compressedBits = currentCompressedStream.length();
        double saved = 100.0 - ((double) compressedBits / originalBits * 100);

        System.out.println("\n--- Compression complete ---");
        System.out.println("Original size  : " + originalBits   + " bits");
        System.out.println("Compressed size: " + compressedBits + " bits");
        System.out.printf( "Space saved    : %.2f%%\n", saved);
        System.out.println("----------------------------");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n==========================================");
            System.out.println("FILE COMPRESSION AND DECOMPRESSION SYSTEM");
            System.out.println("==========================================");
            System.out.println("  1. Compress a string");
            System.out.println("  2. Compress a text file");
            System.out.println("  3. Decompress last result");
            System.out.println("  4. View current bit stream");
            System.out.println("  0. Exit");
            System.out.println("========================================");
            System.out.print("Option (0-4): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("\nEnter text to compress:\n> ");
                    processCompression(scanner.nextLine());
                    break;

                case "2":
                    System.out.println("\nOpening file picker...");
                    System.out.println("(The window may appear behind VS Code)");

                    JFileChooser picker = new JFileChooser();
                    picker.setDialogTitle("Select a .txt file");
                    picker.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));

                    if (picker.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File chosen = picker.getSelectedFile();
                        try {
                            String content = new String(Files.readAllBytes(chosen.toPath()));
                            // Strip UTF-8 BOM if present
                            if (content.startsWith("\uFEFF"))
                                content = content.substring(1);
                            System.out.println("Loaded: " + chosen.getName());
                            processCompression(content);
                        } catch (IOException e) {
                            System.out.println("[!] Could not read file: " + e.getMessage());
                        }
                    } else {
                        System.out.println("[!] Cancelled.");
                    }
                    break;

                case "3":
                    if (currentCompressedStream == null || currentTreeRoot == null) {
                        System.out.println("[!] Nothing compressed yet.");
                    } else {
                        System.out.println("\nDecompressing...");
                        String decoded = decode(currentCompressedStream, currentTreeRoot);
                        System.out.println("\n--- Decompressed output ---");
                        System.out.println(decoded);
                        System.out.println("---------------------------");
                    }
                    break;

                case "4":
                    if (currentCompressedStream == null) {
                        System.out.println("[!] No bit stream in memory yet.");
                    } else {
                        System.out.println("\n--- Bit stream ---");
                        if (currentCompressedStream.length() > 500)
                            System.out.println(currentCompressedStream.substring(0, 500) + "  ...[truncated]");
                        else
                            System.out.println(currentCompressedStream);
                        System.out.println("------------------");
                    }
                    break;

                case "0":
                    System.out.println("\nGoodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("[!] Enter a number between 0 and 4.");
            }
        }
    }
}
