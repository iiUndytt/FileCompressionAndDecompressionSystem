# 🗜️ Huffman Compression

> Lossless text compression in pure Java — compress strings or `.txt` files and see exactly how many bits you save.

---

## What is this?

This is a command-line implementation of **Huffman coding** — a classic lossless compression algorithm that assigns shorter binary codes to frequent characters and longer codes to rare ones. Feed it any text and it will show you the original vs compressed size, the savings percentage, and let you decode it right back.

---

## Quick Start

**Requirements:** Java 8+

```bash
# 1. Compile
javac Node.java HuffmanCompression.java

# 2. Run
java HuffmanCompression
```

That's it. The interactive menu will guide you from there.

> **Note:** Option 2 (file picker) requires a graphical display. It won't work over SSH or in headless environments — use option 1 instead.

---

## Usage

```
==========================================
FILE COMPRESSION AND DECOMPRESSION SYSTEM
==========================================
  1. Compress a string
  2. Compress a text file
  3. Decompress last result
  4. View current bit stream
  0. Exit
==========================================
```

| Option | What it does |
|--------|-------------|
| `1` | Type or paste any text to compress it instantly |
| `2` | Opens a file picker to load a `.txt` file |
| `3` | Decodes the last compressed result back to original text |
| `4` | Prints the raw `0`/`1` bit stream (truncated at 500 chars) |
| `0` | Exit |

### Example

```
> Enter text to compress:
  hello huffman

Analyzing character frequencies...
Building Huffman tree...
Encoding...

--- Compression complete ---
Original size  : 112 bits
Compressed size: 37 bits
Space saved    : 66.96%
----------------------------
```

---

## How Huffman Coding Works

```
Input text  →  Count frequencies  →  Build min-heap  →  Merge nodes into tree
                                                               ↓
Decoded text  ←  Walk tree bit-by-bit  ←  Encoded bit stream  ←  Assign codes
```

1. **Frequency count** — tallies how often each character appears.
2. **Tree construction** — a min-heap repeatedly merges the two lowest-frequency nodes upward until a single root remains.
3. **Code generation** — every left edge adds a `0`, every right edge adds a `1`. Each leaf character ends up with a unique binary prefix code.
4. **Encode / Decode** — compression substitutes characters for their codes; decompression replays the bit stream through the tree.

---

## Project Structure

```
HuffmanCompression/
├── HuffmanCompression.java   # Main app — menu, compression pipeline, decode logic
└── Node.java                 # Tree node (leaf and internal), implements Comparable
```

---

## Limitations

- **In-memory only** — the compressed bit stream is a `String` of `'0'`/`'1'` characters, not packed bytes. No binary file is written to disk.
- **No persistence** — the tree and bit stream are lost when the program exits; there's no way to decode a session you've already closed.
- **GUI required for file picker** — headless/SSH environments must use option 1 (compress a string) instead.

---

## Acknowledgements

Built as a demonstration of the Huffman coding algorithm originally described by David A. Huffman in *"A Method for the Construction of Minimum-Redundancy Codes"* (1952).
