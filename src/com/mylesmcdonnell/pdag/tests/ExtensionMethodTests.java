package com.mylesmcdonnell.pdag.tests;

import com.mylesmcdonnell.pdag.CircularDependencyException;
import com.mylesmcdonnell.pdag.Extensions;
import com.mylesmcdonnell.pdag.Vertex;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by myles on 09/08/2015.
 */
public class ExtensionMethodTests
{
    @Test
    public void FlattenAndGetDistinctDownwards() throws CircularDependencyException
    {
        Vertex vertex00 = new Vertex(null);
        Vertex vertex01 = new Vertex(null);
        Vertex vertex10 = new Vertex(null);
        Vertex vertex11 = new Vertex(null);
        Vertex[] vertices = {vertex10, vertex11};
        vertex00.add_dependencies(vertices);
        vertex01.add_dependencies(vertices);

        Collection<Vertex> verticesList = new ArrayList<Vertex>();
        for(Vertex vertex : Extensions.flatten_and_get_distinct(vertices))
            verticesList.add(vertex);

        Assert.assertEquals(4, verticesList.size());
        Assert.assertTrue(verticesList.contains(vertex00));
        Assert.assertTrue(verticesList.contains(vertex01));
        Assert.assertTrue(verticesList.contains(vertex10));
        Assert.assertTrue(verticesList.contains(vertex11));
    }

    /*TODO:@Test
    public void FlattenAndGetDistinctUpwards() throws CircularDependencyException
    {
        Vertex vertex00 = new Vertex(null);
        Vertex vertex01 = new Vertex(null);
        Vertex vertex10 = new Vertex(null);
        Vertex vertex11 = new Vertex(null);
        Vertex[] vertices = {vertex10, vertex11};
        vertex00.add_dependencies(vertices);
        vertex01.add_dependencies(vertices);

        Collection<Vertex> verticesList = new ArrayList<Vertex>();
        for(Vertex vertex : Extensions.flatten_and_get_distinct(vertices))
            verticesList.add(vertex);

        Assert.assertEquals(4, vertices.length);
        Assert.assertTrue(verticesList.contains(vertex00));
        Assert.assertTrue(verticesList.contains(vertex01));
        Assert.assertTrue(verticesList.contains(vertex10));
        Assert.assertTrue(verticesList.contains(vertex11));
    }*/
}