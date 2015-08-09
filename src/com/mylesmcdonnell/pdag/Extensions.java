package com.mylesmcdonnell.pdag;

import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

/**
 * Created by myles on 09/08/2015.
 */
public class Extensions {

    public static boolean is_dependency(Collection<Vertex> vertices, Vertex vertex){

        if (vertices.contains(vertex))
            return true;

        for(Vertex v : vertices)
            if (v.is_dependency(vertex))
                return true;

        return false;
    }

    public static Vertex[] flatten_and_get_distinct(Vertex[] vertices)
    {
        HashSet<Vertex> set = new HashSet<Vertex>();
        Stack<Vertex> stack = new Stack<Vertex>();
        int count = 0;
        for (Vertex vertex : vertices)
        {
            stack.push(vertex);
            while (stack.size() != 0)
            {
                Vertex current = stack.pop();
                if (set.contains(current)) continue;
                set.add(current);
                count++;
                vertex.get_dependents().forEach(stack::push);
            }
        }

        Vertex[] a = new Vertex[count];
        set.toArray(a);
        return a;
    }
}
