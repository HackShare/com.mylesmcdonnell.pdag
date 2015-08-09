package com.mylesmcdonnell.pdag.tests;

import com.mylesmcdonnell.pdag.CircularDependencyException;
import com.mylesmcdonnell.pdag.Vertex;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by myles on 09/08/2015.
 */
public class VertexTests {

    @Test(expected = CircularDependencyException.class)
    public void CircularDependencyTest() throws CircularDependencyException
    {
        Vertex node =  new Vertex(null);
        Vertex[] nodes = {node};

        node.add_dependencies(nodes);
    }

   @Test(expected = CircularDependencyException.class)
    public void NestedCircularDependencyTest() throws CircularDependencyException
    {
        Vertex node = new Vertex(null);

        Vertex node2 = new Vertex(null);
        Vertex node3 = new Vertex(null);
        Vertex node4 = new Vertex(null);

        node2.add_dependency(node);
        node3.add_dependency(node2);
        node4.add_dependency(node3);

        node.add_dependency(node2);
    }

     /*@Test
    TODO: public void RemoveRedundantDependencies()
    {
        var node = new Vertex(()=>Thread.Sleep(1));

        var node2 = new Vertex(()=>Thread.Sleep(1));
        var node3 = new Vertex(()=>Thread.Sleep(1));

        node3.AddDependencies (node2, node);

        Assert.AreEqual (2, node3.Dependencies.Count());

        node3.RemoveRedundantDependencies ();

        Assert.AreEqual (2, node3.Dependencies.Count());

        node2.AddDependencies (node);

        node3.RemoveRedundantDependencies ();

        Assert.AreEqual (1, node3.Dependencies.Count());
    }*/

    @Test
    public void DoNotAddRedundantDependency() throws CircularDependencyException
    {
        Vertex node = new Vertex(null);

        Vertex node2 = new Vertex(null);
        Vertex node3 = new Vertex(null);

        node2.add_dependency(node);
        node3.add_dependency(node2);

        Assert.assertEquals(1, node3.get_dependencies().size());

        node3.add_dependency(node);

        Assert.assertEquals(1, node3.get_dependencies().size());
    }
}
