package com.mylesmcdonnell.pdagtests;

import com.mylesmcdonnell.pdag.*;
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
    public void GraphExecute_WithConcurrency_WithThrottle() throws
            CircularDependencyException,
            InterruptedException  {
        MaxCount maxCount = new MaxCount();

        Collection<Vertex> mid = new ArrayList<>();
        Vertex node0 = new Vertex(new MaxCountTask(maxCount));
        for(int i = 0;i<100;i++)
            mid.add(new Vertex(new MaxCountTask(maxCount)));

        for(Vertex v : mid)
            v.add_dependency(node0);

        mid.add(node0);

        int maxConcurrency = 50;

        GraphExecutive executive = new GraphExecutive(new DirectedAcyclicGraph(mid.toArray(new Vertex[0])), maxConcurrency);

        executive.execute_and_wait();

        Assert.assertEquals(0, executive.get_vertices_failed().size());

        Assert.assertEquals(maxConcurrency, maxCount.get_max());
    }

    @Test
    public void GraphExecute_WithConcurrency_NoThrottle_VertexException() throws
            CircularDependencyException,
            InterruptedException  {

        for(int ii = 0 ; ii < 1000 ; ii++) {
            Record record = new Record();

            Vertex node0 = new Vertex(new Task(1, record));
            Vertex node1 = new Vertex(new Task(2, record));
            Vertex node2 = new Vertex(new FailedTask());
            Vertex node3 = new Vertex(new Task(2, record));
            Vertex node4 = new Vertex(new Task(3, record));

            Vertex[] mid = {node1, node2, node3};
            node4.add_dependencies(mid);

            for (Vertex v : mid)
                v.add_dependency(node0);

            Vertex[] nodes = {node0, node1, node2, node3, node4};

            GraphExecutive executive = new GraphExecutive(new DirectedAcyclicGraph(nodes), 0);

            executive.execute_and_wait();

            Assert.assertEquals(2, executive.get_vertices_failed().size());

            Integer[] actual = record.get_ints();
            Integer[] expected = {1, 2, 2};

            Assert.assertEquals(expected.length, actual.length);

            String message = "";
            for (Integer i : actual)
                message += "," + i.toString();

            for (int i = 0; i < actual.length; i++)
                Assert.assertEquals(message, expected[i], actual[i] );
        }
    }

    private class Task implements VertexTask {

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
        public void run() throws Exception  {
            if (_pauseMs>0)
                Thread.sleep(_pauseMs);

            _record.add(_i);
        }
    }

    private class FailedTask implements VertexTask {

        @Override
        public void run() throws Exception {
            throw new Exception();
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

    public class MaxCountTask implements VertexTask {

        private MaxCount _maxCount;

        public MaxCountTask(MaxCount maxCount){

            _maxCount = maxCount;
        }

        @Override
        public void run() throws Exception {
            _maxCount.inc();
            Thread.sleep(250);
            _maxCount.dec();
        }
    }

    public class MaxCount {

        private int _current;
        private int _max;

        public synchronized void inc() {
            _current++;
            _max = Math.max(_max, _current);
        }

        public synchronized void dec() {
            _current--;
        }

        public int get_max() {
            return _max;
        }
    }
}
