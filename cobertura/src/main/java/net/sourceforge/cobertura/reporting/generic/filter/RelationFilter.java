package net.sourceforge.cobertura.reporting.generic.filter;

import net.sourceforge.cobertura.reporting.generic.Node;
import net.sourceforge.cobertura.reporting.generic.filter.criteria.Criteria;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
public class RelationFilter implements Filter{

    private Criteria criteria;

    private RelationFilter(){}
    public RelationFilter(Criteria criteria){
        this.criteria = criteria;
    }

    @Override
    public Set<? extends Node> filter(Node node) {
        Set<Relation>relations = new HashSet<Relation>();
        for(Relation relation: node.getRelations()){
            if(criteria.matches(relation)){
                relations.add(relation);
            }
        }
        Set<Node>nodes = new HashSet<Node>();
        for(Relation relation : relations){
            nodes.addAll(node.getNodesForRelation(relation));
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
