package net.sourceforge.cobertura.reporting.generic.filter;

import net.sourceforge.cobertura.reporting.generic.Node;
import net.sourceforge.cobertura.reporting.generic.filter.criteria.Criteria;

import java.util.HashSet;
import java.util.Set;

public class TypeFilter implements Filter{

    private Criteria criteria;

    private TypeFilter(){}
    public TypeFilter(Criteria criteria){
        this.criteria = criteria;
    }

    @Override
    public Set<Node> filter(Node node) {
        Set<Node>filteredNodes = new HashSet<Node>();
        filter(node, filteredNodes);
        return filteredNodes;
    }

    @Override
    public Set<Node> filter(Set<? extends Node> nodes) {
        Set<Node>filteredNodes = new HashSet<Node>();
        for(Node node:nodes){
            filter(node, filteredNodes);
        }
        return filteredNodes;
    }

    private void filter(Node node, Set<Node>filteredNodes){
        if(criteria.matches(node.getType())){
            filteredNodes.add(node);
        }
    }
}
