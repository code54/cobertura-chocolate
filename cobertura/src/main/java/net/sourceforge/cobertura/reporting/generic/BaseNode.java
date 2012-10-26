package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.reporting.generic.filter.Filter;
import net.sourceforge.cobertura.reporting.generic.filter.Relation;
import net.sourceforge.cobertura.reporting.xml.SetWrapper;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

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

    @ElementMap(key = "relation", entry = "nodes")
    protected Map<Relation, SetWrapper> nodes;
    @Attribute
    protected NodeType type;
    @Attribute
    protected String name;
    @Element
    protected Payload payload;

    public BaseNode(){}

    public BaseNode(NodeType type, String name){
        nodes = new HashMap<Relation, SetWrapper>();
        this.type = type;
        this.name = name;
    }

    @Override
    public void addNode(Relation relation, Node node) {
        if(nodes.get(relation)==null){
            nodes.put(relation, new SetWrapper());
        }
        nodes.get(relation).add(node);
    }

    public Set<Relation>getRelations(){
        return nodes.keySet();
    }

    @Override
    public Set<? extends Node> getNodes(boolean thisNodeIncluded) {
        Set<Node>immediateNodes = new HashSet<Node>();
        if(thisNodeIncluded){
            immediateNodes.add(this);
        }
        Iterator<SetWrapper> iterator = nodes.values().iterator();
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
    public Set<? extends Node> getNodesForRelation(Relation relation) {
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

    @Override
    public Payload getPayload() {
        return payload;
    }

    @Override
    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}
