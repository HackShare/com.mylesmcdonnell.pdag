# com.mylesmcdonnell.pdag
A JVM component (written in Java) library for the concurrent execution of vertices in a directed acyclic graph.  A .NET version is also available here: [https://github.com/myles-mcdonnell/MPM.PDAG](https://github.com/myles-mcdonnell/MPM.PDAG)

Imagine a graph like so..

<img src="https://dl.dropboxusercontent.com/u/30149716/github_content/PDAG%20README.png"/>

..we can reason that A and B can be executed concurrently. C can be executed when A & B are complete, F can be executed when only B is complete and D & E can be excuted concurrently when C is complete. F may be executed concurrently with and/or A | C | D | E.

MPM.PDAG provides a mechanism for building directed acyclic graphs to any level of complexity and will automatically determine the maximum level 
of concurrency when executing the graph.  When building a graph an exception will be thrown if a circular reference is attempted and a concurrency throttle may
be provided when executing a graph.  What action is performed when a vertex is executed is specified by passing an action to the vertex constructor.

Although this code has been kicking around in my tool box for a few years only the simple use cases are complete.  There is work to be done around graph execution cancellation and a few other features, PR's are more than welcome.

<pre>
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
</pre>