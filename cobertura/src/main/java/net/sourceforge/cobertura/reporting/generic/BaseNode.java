package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.reporting.generic.filter.Filter;

import java.util.*;

public class BaseNode implements Node{

    protected Map<String, Set<Node>> nodes;
    protected String type;
    protected String name;
    protected String content;//TODO see where needed and add to Node interface

    public BaseNode(){
        nodes = new HashMap<String, Set<Node>>();
    }

    @Override
    public void addNode(String relation, Node node) {
        if(nodes.get(relation)==null){
            nodes.put(relation, new HashSet<Node>());
        }
        nodes.get(relation).add(node);
    }

    public Set<String>getRelations(){
        return nodes.keySet();
    }

    @Override
    public Set<? extends Node> getNodes() {
        Set<Node>immediateNodes = new HashSet<Node>();
        Iterator<Set<Node>> iterator = nodes.values().iterator();
        while (iterator.hasNext()){
            Set<Node>nodes = iterator.next();
            for(Node node: nodes){
                immediateNodes.add(node);
            }
        }
        return immediateNodes;
    }

    @Override
    public Set<? extends Node> getNodes(Filter filter) {
        return filter.filter(this);
    }

    @Override
    public Set<? extends Node> getAllNodes(Filter filter) {
        Set<Node>filteredNodes = new HashSet<Node>();
        if(!getNodes().isEmpty()){
            filteredNodes.addAll(filter.filter(this));
            for(Node entry : getNodes()){
                filteredNodes.addAll(entry.getAllNodes(filter));
            }
        }
        return filteredNodes;
    }

    @Override
    public Set<? extends Node> getNodesForRelation(String relation) {
        return nodes.get(relation);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }
}
