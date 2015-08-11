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

package com.mylesmcdonnell.pdagtests;

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
}