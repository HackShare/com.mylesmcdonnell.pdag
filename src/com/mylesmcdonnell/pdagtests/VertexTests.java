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
