package es.hackxcrack.andHxC;

/**
 * Implementa una tupla mutable de dos elementos.
 *
 */
public class Pair<T, S>{

    public T left;
    public S right;

    public Pair(T l, S r) {
        this.left = l;
        this.right = r;
    }
}
