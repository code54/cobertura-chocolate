package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.reporting.generic.filter.Filter;

import java.util.*;

/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2012 Jose M. Rozanec
 *
 * Cobertura is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * Cobertura is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cobertura; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
public class BaseNode implements Node{

    protected Map<String, Set<Node>> nodes;
    protected NodeType type;
    protected String name;
    protected String content;//TODO see where needed and add to Node interface

    public BaseNode(){}

    public BaseNode(NodeType type, String name){
        nodes = new HashMap<String, Set<Node>>();
        this.type = type;
        this.name = name;
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
    public Set<? extends Node> getNodes(boolean thisNodeIncluded) {
        Set<Node>immediateNodes = new HashSet<Node>();
        if(thisNodeIncluded){
            immediateNodes.add(this);
        }
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
    public Set<? extends Node> getNodes(boolean thisNodeIncluded, Filter filter) {
        return filter.filter(getNodes(thisNodeIncluded));
    }

    @Override
    public Set<? extends Node> getAllNodes(boolean thisNodeIncluded, Filter filter) {
        Set<Node>filteredNodes = new HashSet<Node>();
        if(thisNodeIncluded || getType().equals(NodeType.LINE)){
            filteredNodes.addAll(filter.filter(this));
        }
        for(Node entry : getNodes(false)){
            filteredNodes.addAll(entry.getAllNodes(false, filter));
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
    public NodeType getType() {
        return type;
    }
}
