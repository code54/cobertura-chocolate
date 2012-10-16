package net.sourceforge.cobertura.reporting.generic.filter;

import net.sourceforge.cobertura.reporting.generic.Node;
import net.sourceforge.cobertura.reporting.generic.filter.criteria.Criteria;

import java.util.HashSet;
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
