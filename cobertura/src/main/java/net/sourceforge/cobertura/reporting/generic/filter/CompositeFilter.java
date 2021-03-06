package net.sourceforge.cobertura.reporting.generic.filter;

import net.sourceforge.cobertura.reporting.generic.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
public class CompositeFilter implements Filter{

    private List<Filter>filters;

    private CompositeFilter(){}

    public CompositeFilter(List<Filter> filters){
        this.filters = filters;
    }

    public CompositeFilter(Filter filter){
        this.filters = new ArrayList<Filter>();
        filters.add(filter);
    }

    //TODO check this! is not a correct composite!
    public CompositeFilter addFilter(Filter filter){
        ArrayList filters = new ArrayList(this.filters);
        filters.add(filter);
        return new CompositeFilter(filters);
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
