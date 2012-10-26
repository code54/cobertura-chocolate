package net.sourceforge.cobertura.reporting.xml;

import net.sourceforge.cobertura.reporting.generic.Node;
import org.simpleframework.xml.ElementList;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This is implemented as a workaround to be able to serialize a Set inside a Map with simple xml
 * See this post for details: http://stackoverflow.com/questions/5027706/simplexml-framework-embedded-collections
 */
public class SetWrapper implements Serializable, Cloneable, Iterable<Node>, Collection<Node>, Set<Node> {

    @ElementList
    private Set<Node> nodes;

    public SetWrapper(){
        nodes = new HashSet<Node>();
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return nodes.contains(o);
    }

    @Override
    public Object[] toArray() {
        return nodes.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return nodes.toArray(a);
    }

    @Override
    public boolean add(Node e) {
        return nodes.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return nodes.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return nodes.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Node> c) {
        return nodes.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return nodes.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return nodes.retainAll(c);
    }

    @Override
    public void clear() {
        nodes.clear();
    }

    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }
}
