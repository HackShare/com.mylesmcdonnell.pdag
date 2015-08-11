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
//

package com.mylesmcdonnell.pdagtests;

import com.mylesmcdonnell.pdag.CircularDependencyException;
import com.mylesmcdonnell.pdag.DirectedAcyclicGraph;
import com.mylesmcdonnell.pdag.Vertex;
import com.mylesmcdonnell.pdag.VertexTask;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DirectedAcyclicGraphTest {

    private DirectedAcyclicGraph _graph;
    private Vertex _terminal;
    private Vertex _root;

    @Before
    public void setUp() throws Exception, CircularDependencyException {
        _root = new Vertex (new DoNada());
        Vertex node1 = new Vertex (new DoNada());
        _terminal = new Vertex (new DoNada());

        Vertex[] arr0 = {node1};
        _terminal.add_dependencies(arr0);
        Vertex[] arr1 = {_root};
        node1.add_dependencies(arr1);

        Vertex[] arr2 = {_root, node1, _terminal};
        _graph = new DirectedAcyclicGraph(arr2);
    }

    @Test
    public void testGet_all_vertices() throws Exception {
        Assert.assertEquals(3, _graph.get_all_vertices().length);
    }

    @Test
    public void testGet_root_vertices() throws Exception {
        Assert.assertEquals(1, _graph.get_root_vertices().length);
        Assert.assertEquals(_root, _graph.get_root_vertices()[0]);
    }

    @Test
    public void testGet_terminal_vertices() throws Exception {
        Assert.assertEquals(1, _graph.get_terminal_vertices().length);
        Assert.assertEquals(_terminal, _graph.get_terminal_vertices()[0]);
    }

    private class DoNada implements VertexTask{
        @Override
        public void run() throws Exception {
            Thread.sleep(1);
        }
    }
}