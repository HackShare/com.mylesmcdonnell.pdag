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

import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

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
