package com.mylesmcdonnell.pdag;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by myles on 09/08/2015.
 */
public class Vertex implements VertexTask{

    private VertexTask _task;
    private Collection<Vertex> _dependencies = new ArrayList<Vertex>();
    private Collection<Vertex> _dependents = new ArrayList<Vertex>();

    public Vertex(VertexTask task) {

        _task = task;
    }

    public boolean is_dependency(Vertex vertex)
    {
        return Extensions.is_dependency(_dependencies, vertex);
    }

    public Collection<Vertex> get_dependencies()
    {
        return _dependencies;
    }

    public Vertex add_dependency(Vertex vertex) throws CircularDependencyException {
        Vertex[] arr = {vertex};
        return add_dependencies(arr);
    }

    public synchronized Vertex add_dependencies(Vertex[] dependencies) throws CircularDependencyException {
        for (Vertex dependency : dependencies) {

            if (dependency == this)
                throw new CircularDependencyException(dependency);

            CheckForCircularDependency(dependency.get_dependencies());

            if (Extensions.is_dependency(_dependencies, dependency))
                return this;

             _dependencies.add(dependency);
            dependency._dependents.add(this);
        }

        return this;
    }

    public Collection<Vertex> get_dependents()
    {
        return _dependents;
    }

    private void CheckForCircularDependency(Collection<Vertex> dependencies) throws CircularDependencyException
    {
        if (dependencies.contains(this))
            throw new CircularDependencyException(this);

        for (Vertex dependency : dependencies)
            CheckForCircularDependency(dependency.get_dependencies());
    }

    public void run() throws Exception
    {
        _task.run();
    }
}