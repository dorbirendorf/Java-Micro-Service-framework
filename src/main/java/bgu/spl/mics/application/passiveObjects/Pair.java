package bgu.spl.mics.application.passiveObjects;

/**
 * Implements pair class which has int tick key, and string bookName value
 */
public class Pair {
    private int tick;
    private String bookName;

    public Pair(int tick, String bookName)
    {
        this.tick = tick;
        this.bookName = bookName;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getTick() {
        return tick;
    }

    public String getBookName() {
        return bookName;
    }
}
