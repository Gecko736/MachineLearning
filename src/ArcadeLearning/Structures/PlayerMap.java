package ArcadeLearning.Structures;

import ArcadeLearning.Neurd.Player;

import java.util.*;

public class PlayerMap implements Map<String, Player> {
    private final String alphabet = "0123456789abcdef-";
    private int size = 0;
    private Letter first = new Letter(null);

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(Player player) {
        return containsKey(player.getName());
    }

    @Override
    public boolean containsKey(Object key) {
        try {
            return first.search((String) key, (String) key).value != null;
        } catch (ClassCastException | IllegalArgumentException | NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public boolean containsValue(Object value) {
        try {
            return containsKey(((Player) value).getName());
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public Player get(Object key) {
        if (!(key instanceof String))
            throw new IllegalArgumentException();
        Letter output = first.search((String) key, (String) key);
        return output.value;
    }

    public void add(Player player) {
        put(player.getName(), player);
    }

    @Override
    public Player put(String key, Player value) {
        return null;
    }

    @Override
    public Player remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Player> m) {

    }

    @Override
    public void clear() {
        size = 0;
        first.next = new Letter[17];
    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<Player> values() {
        return null;
    }

    @Override
    public Set<Entry<String, Player>> entrySet() {
        return null;
    }

    private class Letter implements Map.Entry<String, Player> {
        private final String key;
        private Player value;
        private Letter[] next = new Letter[17];

        private Letter(String key) {
            this.key = key;
        }

        private Letter search(String key, String currentKey)
                throws IllegalArgumentException, NoSuchElementException {
            if (!currentKey.isEmpty()) {
                if (alphabet.contains(currentKey.charAt(0) + ""))
                    return next[alphabet.indexOf(currentKey.charAt(0))]
                            .search(key, currentKey.substring(1));
                throw new IllegalArgumentException();
            }
            if (key.equals(this.key))
                return this;
            throw new NoSuchElementException();
        }

        private void add(String key, Player value) {

        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Player getValue() {
            return value;
        }

        @Override
        public Player setValue(Player value) {
            Player output = this.value;
            this.value = value;
            return output;
        }
    }
}
