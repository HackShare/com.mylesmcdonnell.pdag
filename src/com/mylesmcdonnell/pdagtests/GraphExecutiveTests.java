package com.mylesmcdonnell.pdagtests;

import com.mylesmcdonnell.pdag.CircularDependencyException;
import com.mylesmcdonnell.pdag.DirectedAcyclicGraph;
import com.mylesmcdonnell.pdag.GraphExecutive;
import com.mylesmcdonnell.pdag.Vertex;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by myles on 09/08/2015.
 */
public class GraphExecutiveTests {
    @Test
    public void SimpleGraphExecute_NoConcurrency() throws
            CircularDependencyException,
            InterruptedException  {
        Record record = new Record();

        Vertex node0 = new Vertex(new Task(1, record));
        Vertex node1 = new Vertex(new Task(2, record));
        Vertex node2 = new Vertex(new Task(3, record));

        node2.add_dependency(node1);
        node1.add_dependency(node0);

        Vertex[] nodes = {node0, node1, node2};

        GraphExecutive executive = new GraphExecutive(new DirectedAcyclicGraph(nodes), 0);

        executive.execute_and_wait();

        Integer[] actual = record.get_ints();
        Integer[] expected = {1,2,3};

        Assert.assertEquals(expected.length, actual.length);

        String message = "";
        for (Integer i : actual)
            message += "," + i.toString();

        for (int i = 0; i < actual.length; i++)
            Assert.assertEquals(message, expected[i], actual[i] );
    }

    @Test
    public void GraphExecute_WithConcurrency_NoThrottle() throws
            CircularDependencyException,
            InterruptedException  {

        for(int ii = 0 ; ii < 1000 ; ii++) {
            Record record = new Record();

            Vertex node0 = new Vertex(new Task(1, record));
            Vertex node1 = new Vertex(new Task(2, record));
            Vertex node2 = new Vertex(new Task(2, record));
            Vertex node3 = new Vertex(new Task(2, record));
            Vertex node4 = new Vertex(new Task(3, record));

            Vertex[] mid = {node1, node2, node3};
            node4.add_dependencies(mid);

            for (Vertex v : mid)
                v.add_dependency(node0);

            Vertex[] nodes = {node0, node1, node2, node3, node4};

            GraphExecutive executive = new GraphExecutive(new DirectedAcyclicGraph(nodes), 0);

            executive.execute_and_wait();

            Assert.assertEquals(0, executive.get_vertices_failed().size());

            Integer[] actual = record.get_ints();
            Integer[] expected = {1, 2, 2, 2, 3};

            Assert.assertEquals(expected.length, actual.length);

            String message = "";
            for (Integer i : actual)
                message += "," + i.toString();

            for (int i = 0; i < actual.length; i++)
                Assert.assertEquals(message, expected[i], actual[i] );
        }
    }

    @Test
    public void GraphExecute_WithConcurrency_NoThrottle_VertexException() throws
            CircularDependencyException,
            InterruptedException  {

        for(int ii = 0 ; ii < 1000 ; ii++) {
            Record record = new Record();

            Vertex node0 = new Vertex(new Task(1, record));
            Vertex node1 = new Vertex(new Task(2, record));
            Vertex node2 = new Vertex(new Task(2, record));
            Vertex node3 = new Vertex(new Task(2, record));
            Vertex node4 = new Vertex(new Task(3, record));

            Vertex[] mid = {node1, node2, node3};
            node4.add_dependencies(mid);

            for (Vertex v : mid)
                v.add_dependency(node0);

            Vertex[] nodes = {node0, node1, node2, node3, node4};

            GraphExecutive executive = new GraphExecutive(new DirectedAcyclicGraph(nodes), 0);

            executive.execute_and_wait();

            Assert.assertEquals(0, executive.get_vertices_failed().size());

            Integer[] actual = record.get_ints();
            Integer[] expected = {1, 2, 2, 2, 3};

            Assert.assertEquals(expected.length, actual.length);

            String message = "";
            for (Integer i : actual)
                message += "," + i.toString();

            for (int i = 0; i < actual.length; i++)
                Assert.assertEquals(message, expected[i], actual[i] );
        }
    }

    private class Task implements Runnable {

        private Integer _i;
        private Record _record;
        private int _pauseMs;

        public Task(Integer i, Record record) {
            this(i, record, 0);
        }

        public Task(Integer i, Record record, int pauseMs){
            _i = i;
            _record = record;
            _pauseMs = pauseMs;
        }

        @Override
        public void run()  {
            if (_pauseMs>0) {
                try {
                    Thread.sleep(_pauseMs);
                }
                catch(Exception ex){}
            }
            _record.add(_i);
        }
    }

    private class FailedTask implements Runnable {

        @Override
        public void run() throws Exception {
            throw Exception();
        }
    }

    public class Record {

        private final Collection<Integer> _ints = new ArrayList<>();

        public synchronized void add(Integer i) {
            _ints.add(i);
        }

        public Integer[] get_ints() {
            return _ints.toArray(new Integer[0]);
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
