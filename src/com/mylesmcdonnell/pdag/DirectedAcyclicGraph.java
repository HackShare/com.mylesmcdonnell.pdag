package com.mylesmcdonnell.pdag;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by myles on 09/08/2015.
 */
public class DirectedAcyclicGraph {

    private Vertex[] _all;
    private Vertex[] _root;
    private Vertex[] _terminal;

    public DirectedAcyclicGraph(Vertex[] vertices)
    {
        _all = Extensions.flatten_and_get_distinct(vertices);

        Collection<Vertex> root = new ArrayList<Vertex>();
        for(Vertex vertex : _all)
            if (vertex.get_dependencies().size()==0)
                root.add(vertex);

        _root = new Vertex[root.size()];
        root.toArray(_root);

        Collection<Vertex> terminal = new ArrayList<Vertex>();
        for(Vertex vertex : _all)
            if (vertex.get_dependents().size()==0)
                terminal.add(vertex);

        _terminal = new Vertex[terminal.size()];
        terminal.toArray(_terminal);
    }

    public Vertex[] get_all_vertices() { return _all; }
    public Vertex[] get_root_vertices() { return _root; }
    public Vertex[] get_terminal_vertices() { return _terminal; }
}
