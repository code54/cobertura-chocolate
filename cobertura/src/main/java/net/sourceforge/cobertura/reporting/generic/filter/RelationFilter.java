package net.sourceforge.cobertura.reporting.generic.filter;

import net.sourceforge.cobertura.reporting.generic.Node;
import net.sourceforge.cobertura.reporting.generic.filter.criteria.Criteria;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RelationFilter implements Filter{

    private Criteria criteria;

    private RelationFilter(){}
    public RelationFilter(Criteria criteria){
        this.criteria = criteria;
    }

    @Override
    public Set<? extends Node> filter(Node node) {
        Set<String>relations = new HashSet<String>();
        for(String relation: node.getRelations()){
            if(criteria.matches(relation)){
                relations.add(relation);
            }
        }
        Iterator<String> iterator = relations.iterator();
        Set<Node>nodes = new HashSet<Node>();
        while(iterator.hasNext()){
            nodes.addAll(node.getNodesForRelation(iterator.next()));
        }
        return nodes;
    }

    @Override
    public Set<Node> filter(Set<? extends Node> nodes) {
        Set<Node>filteredNodes = new HashSet<Node>();
        for(Node node:nodes){
            filteredNodes.addAll(filter(node));
        }
        return filteredNodes;
    }
}
