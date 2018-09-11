package ArcadeLearning.Structures;

import ArcadeLearning.Neurd.Player;

import java.util.*;

public class Population implements Map<String, Player>, Iterable<Player> {
    private final String alphabet = "0123456789abcdef-";
    private int size = 0;
    private Letter first = new Letter('*', null);

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(Object key) {
        try {
            return (key instanceof String && first.search((String) key).value != null) ||
                   (key instanceof Player && first.search(((Player) key).getName()).value != null);
        } catch (ClassCastException | IllegalArgumentException | NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        try {
            return first.search((String) key).value != null;
        } catch (ClassCastException | IllegalArgumentException | NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public boolean containsValue(Object value) {
        try {
            return first.search(((Player) value).getName()).value != null;
        } catch (ClassCastException | IllegalArgumentException | NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public Player get(Object key) {
        return find(key).value;
    }

    public void add(Player player) {
        first.add(player.getName(), player);
        size++;
    }

    @Override
    public Player put(String key, Player value) {
        if (!key.equals(value.getName()))
            throw new IllegalArgumentException("key and player name must match: " + key + " != " + value.getName() +
            "\nWhy are you even using this method. Just use the add method. This method just calls that one anyways.");
        add(value);
        return null;
    }

    @Override
    public Player remove(Object key) {
        return find(key).remove();
    }

    @Override
    public void putAll(Map<? extends String, ? extends Player> m) {
        for (String key : m.keySet())
            add(m.get(key));
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
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Player> iterator() {
        return new TopIterator();
    }

    private class TopIterator implements Iterator<Player> {
        private int i = 0;
        private Stack<PopIterator> stack = new Stack<>();
        private Letter nextLetter;

        @Override
        public boolean hasNext() {
            if (nextLetter == null)
                nextLetter = findNext();
            return nextLetter == null;
        }

        @Override
        public Player next() {
            if (hasNext()) {
                Player output = nextLetter.value;
                nextLetter = null;
                return output;
            }
            return null;
        }

        private Letter findNext() {
            if (stack.isEmpty()) {
                for (; i < first.next.length; i++) {
                    if (first.next[i] != null) {
                        if (first.next[i].value != null) {
                            if (first.next[i].next != null)
                                stack.push(new PopIterator(first.next[i]));
                            return first.next[i];
                        }
                        if (first.next[i].next != null) {
                            stack.push(new PopIterator(first.next[i]));
                            return stack.peek().next();
                        }
                        throw new NullPointerException("Development error. " +
                                "Letter.value and Letter.next should not both be null");
                    }
                }
            } else {
                Letter output = stack.peek().next();
                while (output == null) {
                    stack.pop();
                    output = stack.peek().next();
                }
                return output;
            }
            return null;
        }

        private class PopIterator {
            private final Letter root;
            private int j = 0;

            private PopIterator(Letter root) {
                this.root = root;
            }

            private Letter next() {
                for (; j < root.next.length; j++) {
                    if (root.next[j] != null) {
                        if (root.next[j].value != null) {
                            if (root.next[j].next != null)
                                stack.push(new PopIterator(root.next[j]));
                            return root.next[j];
                        }
                        if (root.next[j].next == null)
                            throw new NullPointerException("Development error. " +
                                    "Letter.value and Letter.next should not both be null");
                        stack.push(new PopIterator(root.next[j]));
                        return stack.peek().next();
                    }
                }
                return null;
            }
        }
    }

    private Letter find(Object key) {
        if (key instanceof String)
            return first.search((String) key);
        else if (key instanceof Player)
            return first.search(((Player) key).getName());
        else
            throw new IllegalArgumentException("Argument must be of type String or Player");
    }

    private class Letter implements Map.Entry<Character, Player> {
        private final char key;
        private Player value;
        private Letter[] next = new Letter[17];
        private Letter prev;

        private Letter(char key, Letter prev) {
            this.key = key;
            this.prev = prev;
        }

        private Letter search(String currentKey) throws IllegalArgumentException {
            if (currentKey.equals("" + key))
                return this;
            if (!currentKey.isEmpty()) {
                if (alphabet.contains(currentKey.charAt(0) + ""))
                    return next[alphabet.indexOf(currentKey.charAt(0))]
                            .search(currentKey.substring(1));
                throw new IllegalArgumentException("Invalid character: " + currentKey.charAt(0));
            }
            return null;
        }

        private void add(String currentKey, Player value) {
            if (currentKey.equals("" + key))
                this.value = value;
            else if (!currentKey.isEmpty()) {
                if (next == null)
                    next = new Letter[alphabet.length()];
                char c = currentKey.charAt(0);
                int i = alphabet.indexOf(c);
                if (next[i] == null)
                    next[i] = new Letter(c, this);
                next[i].add(currentKey.substring(1), value);
            }
        }

        private Player remove() {
            if (next == null) {
                prev.next[alphabet.indexOf(key)] = null;
                prev.delete();
            }
            Player output = value;
            value = null;
            return output;
        }

        private boolean delete() {
            if (prev != null) {
                for (int i = 0; i < next.length; i++)
                    if (next[i] != null)
                        return false;
                prev.next[alphabet.indexOf(key)] = null;
                prev.delete();
            }
            return true;
        }

        @Override
        public Character getKey() {
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
