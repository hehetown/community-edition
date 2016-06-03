package org.alfresco.util.schemacomp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alfresco.util.schemacomp.Difference.Where;

/**
 * Collects differences so that tools can report on or respond to differences between database schemas.
 * 
 * @author Matt Ward
 */
public class Results implements Iterable<Result>
{
    private final List<Result> items = new ArrayList<Result>();
    /** Temporary step during refactor - Where.IN_BOTH_NO_DIFFERENCE will be going altogether */
    private boolean reportNonDifferences = false;
    

    /**
     * Record a difference between two objects, or specify that an object only appears in either the
     * 'left' or 'right' schemas.
     * 
     * @param where The type of difference, see {@link Where}
     * @param left Left value, or null if the item appears in the right, but not left schema.
     * @param right Right value, or null if the item appears in the left, but not right schema.
     */
    public void add(Where where, DbProperty left, DbProperty right)
    {
        if (where != Where.IN_BOTH_NO_DIFFERENCE || reportNonDifferences)
        {
            Difference result = new Difference(where, left, right);
            items.add(result);
        }
    }
    
    public void add(Result result)
    {
        items.add(result);
    }
    
    /**
     * Obtain an iterator for the top-level items held in this schema - since this is a hierarchical model,
     * deeper items are obtained by navigating through the top-level items.
     */
    @Override
    public Iterator<Result> iterator()
    {
        return items.iterator();
    }

    /**
     * Get the ith result.
     * 
     * @param i int
     * @return Result
     */
    public Result get(int i)
    {
        return items.get(i);
    }
    
    /**
     * @return How many top-level items are in the schema.
     */
    public int size()
    {
        return items.size();
    }
}
