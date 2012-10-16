package net.sourceforge.cobertura.reporting.generic.filter.criteria;

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
public class ORListedCriteria implements Criteria {

    private Set<Criteria> criterias;

    private ORListedCriteria(){}

    public ORListedCriteria(Set<Criteria>criterias){
        this.criterias = criterias;
    }

    @Override
    public boolean matches(Object element) {
        boolean matches = false;
        for(Criteria criteria: criterias){
            matches = matches||criteria.matches(element);
        }
        return matches;
    }
}
