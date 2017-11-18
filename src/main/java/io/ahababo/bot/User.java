package io.ahababo.bot;

public class User {
    private final int id;
    private final long chatId;

    public User(int id, long chatId) {
        this.id = id;
        this.chatId = chatId;
    }

    public long getUserId() {
        return id;
    }
    public long getChatId() { return chatId; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) return false;
        return ((User) o).id == id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
