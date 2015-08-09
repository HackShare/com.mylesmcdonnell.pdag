package com.mylesmcdonnell.pdag.tests;

import com.mylesmcdonnell.pdag.CircularDependencyException;
import com.mylesmcdonnell.pdag.DirectedAcyclicGraph;
import com.mylesmcdonnell.pdag.GraphExecutive;
import com.mylesmcdonnell.pdag.Vertex;
import org.junit.Assert;
import org.junit.Test;

import java.util.Stack;

/**
 * Created by myles on 09/08/2015.
 */
public class GraphExecutiveTests {
    @Test
    public void SimpleGraphExecuteConcurrently() throws
            CircularDependencyException,
            InterruptedException  {
        Stack<Integer> stack = new Stack<Integer>();

        Vertex node0 = new Vertex(new Task(1, stack));
        Vertex node1 = new Vertex(new Task(2, stack));
        Vertex node2 = new Vertex(new Task(3, stack));

        node2.add_dependency(node1);
        node1.add_dependency(node0);

        Vertex[] nodes = {node0, node1, node2};

        GraphExecutive executive = new GraphExecutive(new DirectedAcyclicGraph(nodes), 0);

        executive.execute_and_wait();

        for (int i = 3; i > 0; i--)
            Assert.assertEquals(i, (int)stack.pop());
    }

    private class Task implements Runnable {

        private Integer _i;
        private Stack<Integer> _stack;

        public Task(Integer i, Stack<Integer> stack){

            _i = i;
            _stack = stack;
        }

        @Override
        public void run() {
            _stack.push(_i);
        }
    }

   /*public class MaxCount {
        private readonly object
        _lock=new

        object();

        private int _current;

        public void Inc() {
            lock(_lock)
            {
                _current++;
                Max = Math.Max(Max, _current);
            }
        }

        public void Dec() {
            lock(_lock)
            {
                _current--;
            }
        }

        public int Max

        {
            get;
            private set ;
        }
    }*/

}
