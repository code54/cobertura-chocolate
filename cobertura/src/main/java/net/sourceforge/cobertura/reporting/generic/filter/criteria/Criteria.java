package net.sourceforge.cobertura.reporting.generic.filter.criteria;

public interface Criteria <T>{

    boolean matches(T element);
}
