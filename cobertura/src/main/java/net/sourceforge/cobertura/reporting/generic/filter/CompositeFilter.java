package net.sourceforge.cobertura.reporting.generic.filter;

import net.sourceforge.cobertura.reporting.generic.Node;

import java.util.HashSet;
import java.util.Set;

public class CompositeFilter implements Filter{

    private Set<Filter>filters;
    private Filter firstFilter;

    private CompositeFilter(){}

    public CompositeFilter(Set<Filter>filters){
        firstFilter = filters.iterator().next();
        filters.remove(firstFilter);
        this.filters = filters;
    }

    @Override
    public Set<? extends Node> filter(Node node) {
        Set<Node>nodes = new HashSet<Node>();
        return filter(nodes);
    }

    @Override
    public Set<? extends Node> filter(Set<? extends Node> nodes) {
        Set<? extends Node>filtered = nodes;
        for(Filter filter:filters){
            filtered=filter(filter, filtered);
        }
        return filtered;
    }

    private Set<? extends Node>filter(Filter filter, Set<? extends Node>nodes){
        if(nodes.isEmpty()){
            return new HashSet<Node>();
        }else{
            return filter.filter(nodes);
        }
    }
}
