package net.sourceforge.cobertura.reporting.generic.filter;

import net.sourceforge.cobertura.reporting.generic.Node;

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

/**
 * Defines a condition to filter nodes.
 */
public interface Filter {
    /**
     * Filters given node and returns a Set with that node if not filtered
     * or an empty node if does not match given criteria.
     * @param node
     * @return
     */
    Set<? extends Node> filter(Node node);

    /**
     * Filters given nodes and returns a new Set with the ones that match a given criteria.
     * @param nodes
     * @return
     */
    Set<? extends Node> filter(Set<? extends Node> nodes);
}
