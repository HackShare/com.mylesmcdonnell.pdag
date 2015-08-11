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

package com.mylesmcdonnell.pdag_example;

import com.mylesmcdonnell.pdag.*;

public class Program {
    public static void main(String[] args) throws CircularDependencyException, InterruptedException {
        new Program().run();
    }

    private void run() throws CircularDependencyException, InterruptedException {

        //Create six nodes that each do nothing for at least 1 second
        Vertex nodeA = new Vertex(new SleepyTask());
        Vertex nodeB = new Vertex(new SleepyTask());
        Vertex nodeC = new Vertex(new SleepyTask());
        Vertex nodeD = new Vertex(new SleepyTask());
        Vertex nodeE = new Vertex(new SleepyTask());
        Vertex nodeF = new Vertex(new SleepyTask());

        Vertex[] roots = {nodeA, nodeB};
        //Create a DAG from the nodes
        nodeC.add_dependencies(roots);
        nodeD.add_dependency(nodeC);
        nodeE.add_dependency(nodeC);
        nodeF.add_dependency(nodeB);

        Vertex[] all = {nodeA, nodeB, nodeC, nodeD, nodeE, nodeF};
        DirectedAcyclicGraph graph = new DirectedAcyclicGraph(all);

        //Create a graph executive
        GraphExecutive graphExecutive = new GraphExecutive(graph);

        graphExecutive.execute_and_wait();

        //Output result
        System.out.println("Graph Execution Complete");
        System.out.println("nodes failed : " + graphExecutive.get_vertices_failed().size());
    }

    private class SleepyTask implements VertexTask {

        @Override
        public void run() throws Exception {
            Thread.sleep(1000);
        }
    }
}
