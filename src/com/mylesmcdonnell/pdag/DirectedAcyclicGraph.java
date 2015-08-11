//   Copyright 2015 Myles McDonnell (mcdonnell.myles@gmail.com)

//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at

//     http://www.apache.org/licenses/LICENSE-2.0

//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.mylesmcdonnell.pdag;

import java.util.ArrayList;
import java.util.Collection;

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
