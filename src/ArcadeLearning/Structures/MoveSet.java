package ArcadeLearning.Structures;

import ArcadeLearning.Move;
import ArcadeLearning.State;

import java.util.Collection;
import java.util.Iterator;

public class MoveSet implements Collection<Move> {
    private final Link first = new Link(null, null, null);
    private final Link last = new Link(null, null, null);
    private int size = 0;

    // sort the link from lesser to greater by State::compareTo

    public MoveSet() {
        first.next = last;
        last.prev = first;
    }

    private Link find(Object o) {
        Link cursor = first.next;
        if (o instanceof State) {
            while (cursor != last) {
                int compare = State.compare(((State) o).ints, cursor.move.state);
                if (compare == 0)
                    return cursor;
                if (compare > 0)
                    return null;
                cursor = cursor.next;
            }
        } else if (o instanceof Move) {
            while (cursor != last) {
                int compare = State.compare(((Move) o).ints, cursor.move.state);
                if (compare == 0)
                    return cursor;
                if (compare > 0)
                    return null;
                cursor = cursor.next;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return find(o) != null;
    }

    @Override
    public Iterator<Move> iterator() {
        return new MoveIterator();
    }

    private class MoveIterator implements Iterator<Move> {
        private Link cursor = first;

        @Override
        public boolean hasNext() {
            return cursor.next != last;
        }

        @Override
        public Move next() {
            cursor = cursor.next;
            return cursor.move;
        }
    }

    @Override
    public Move[] toArray() {
        Move[] output = new Move[size];
        int i = 0;
        for (Move move : this)
            output[i++] = move;
        return output;
    }

    @Override
    public <T> T[] toArray(T[] a) throws NullPointerException {
        try {
            T[] output = (T[]) new Move[Math.max(a.length, size)];
            int i = 0;
            for (Move move : this)
                output[i++] = (T) move;
            if (i > output.length)
                System.arraycopy(a, i + 1, output, i + 1, a.length - i - 1);
            return output;
        } catch (ClassCastException e) {
            throw new ArrayStoreException();
        }
    }

    @Override
    public boolean add(Move move) {
        Link cursor = first.next;
        while (cursor != last) {
            int compare = State.compare(move.state, cursor.move.state);
            if (compare == 0) {
                if (move.equals(cursor.move))
                    return false;
                cursor.move = move;
                return true;
            } if (compare < 0) {
                new Link(cursor.prev, move, cursor);
                return true;
            }
            cursor = cursor.next;
        }
        new Link(last.prev, move, last);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Move) && !(o instanceof State))
            throw new ClassCastException();
        Link l = find(o);
        if (l == null)
            return false;
        l.delete();
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        boolean output = false;
        for (Object o : c)
            output |= find(o) != null;
        return output;
    }

    @Override
    public boolean addAll(Collection<? extends Move> c) {
        boolean output = false;
        for (Move m : c)
            output |= add(m);
        return output;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean output = false;
        for (Object o : c)
            output |= remove(o);
        return output;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean output = false;
        Link cursor = first.next;
        while (cursor != last) {
            if (!c.contains(cursor.move)) {
                cursor.delete();
                output = true;
            }
            cursor = cursor.next;
        }
        return output;
    }

    @Override
    public void clear() {
        first.next = last;
        last.prev = first;
    }

    private class Link {
        private Link prev;
        private Move move;
        private Link next;

        private Link(Link prev, Move move, Link next) {
            this.prev = prev;
            if (prev != null)
                prev.next = this;

            this.move = move;

            this.next = next;
            if (next != null)
                next.prev = this;
        }

        private void delete() {
            prev.next = next;
            next.prev = prev;
        }
    }
}
