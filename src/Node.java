import java.util.ArrayList;

public class Node {
    private int level; // The level of the tree that this Node is located
    private long blockId; // The unique ID of the file block that this Node refers to
    private ArrayList<Entry> entries; // The ArrayList with the Entries of the Node


    // Root constructor with it's level as a parameter which makes a new empty ArrayList for the Node
    Node(int level) {
        this.level = level;
        this.entries = new ArrayList<>();
        this.blockId = RStarTree.getRootNodeBlockId();
    }

    // Node constructor with level and entries parameters
    Node(int level, ArrayList<Entry> entries) {
        this.level = level;
        this.entries = entries;
    }

    long getBlockId() {
        return blockId;
    }

    int getLevel() {
        return level;
    }

    void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    void setEntries(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    // Adds the given entry to the entries ArrayList of the Node
    void insertEntry(Entry entry)
    {
        entries.add(entry);
    }
}
