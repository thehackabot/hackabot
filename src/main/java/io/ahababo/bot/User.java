package io.ahababo.bot;

public class User {
    private final int id;
    public User(int id) {
        this.id = id;
    }

    public long getUserId() {
        return id;
    }

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
