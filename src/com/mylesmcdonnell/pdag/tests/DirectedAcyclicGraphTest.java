package com.mylesmcdonnell.pdag.tests;

import com.mylesmcdonnell.pdag.CircularDependencyException;
import com.mylesmcdonnell.pdag.DirectedAcyclicGraph;
import com.mylesmcdonnell.pdag.Vertex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by myles on 09/08/2015.
 */
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

    private class DoNada implements Runnable{
        @Override
        public void run() {
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException ex) {}
        }
    }
}