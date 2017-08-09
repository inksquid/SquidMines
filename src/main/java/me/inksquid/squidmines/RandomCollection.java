package me.inksquid.squidmines;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {

    private final NavigableMap<Double, E> map = new TreeMap<>();
    private Random random = new Random();
    private double total = 0;

    public void add(double weight, E result) {
        if (weight > 0) {
            total += weight;
            map.put(total, result);
        }
    }

    public void clear() {
        map.clear();
        total = 0;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public E next() {
        return map.ceilingEntry(random.nextDouble() * total).getValue();
    }
}
