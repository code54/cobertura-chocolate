package net.sourceforge.cobertura.reporting.generic;

import java.util.List;

/**
 * All implementing subclasses should follow this convention:
 * levels list should contain all NodeTypes for that hierarchy
 * and should be sorted from lowest level to highest level (ex.: LINE ... PROJECT)
 */
public class AbstractNodeTypeHierarchy implements NodeTypeHierarchy{

    protected List<NodeType>levels;

    public NodeType getHigher(NodeType type){
        for(int j=0;j<levels.size();j++){
            if(levels.get(j).equals(type)&&
                    !type.equals(getHighest())){
                return levels.get(j+1);
            }
        }
        return null;
    }

    public NodeType getLower(NodeType type){
        for(int j=0;j<levels.size();j++){
            if(levels.get(j).equals(type)&&
                    !type.equals(getLowest())){
                return levels.get(j-1);
            }
        }
        return null;
    }

    public int compare(NodeType first, NodeType second){
        if(first.equals(second)){
            return 0;
        }

        for(int j=0;j<levels.size();j++){
            //if we first meet the first, then second is higher
            if(levels.get(j).equals(first)){
                return 1;
            }
            //if we first meet the second, then first is higher
            if(levels.get(j).equals(second)){
                return -1;
            }
        }
        throw new RuntimeException("No matching NodeType was found");
    }

    @Override
    public NodeType getHighest() {
        return levels.get(levels.size()-1);
    }

    @Override
    public NodeType getLowest() {
        return levels.get(0);
    }
}
