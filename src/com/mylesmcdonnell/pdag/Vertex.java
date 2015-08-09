package com.mylesmcdonnell.pdag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by myles on 09/08/2015.
 */
public class Vertex implements Runnable{

    private final ReadWriteLock _readWriteLock = new ReentrantReadWriteLock();
    private final Lock _readLock = _readWriteLock.readLock();
    private final Lock _writeLock = _readWriteLock.writeLock();

    private Runnable _doWorkAction;
    private Collection<Vertex> _dependencies = new ArrayList<Vertex>();
    private Collection<Vertex> _dependents = new ArrayList<Vertex>();

    public Vertex(Runnable doWorkAction) {

        _doWorkAction = doWorkAction;
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

    public Vertex add_dependencies(Vertex[] dependencies) throws CircularDependencyException {
        _writeLock.lock();
        try {
            for (Vertex dependency : dependencies) {

                if (dependency == this)
                    throw new CircularDependencyException(dependency);

                CheckForCircularDependency(dependency.get_dependencies());

                if (Extensions.is_dependency(_dependencies, dependency))
                    return this;

                 _dependencies.add(dependency);
                dependency._dependents.add(this);
            }
        }
        finally
        {
            _writeLock.unlock();
        }

        return this;
    }

    public Collection<Vertex> get_dependents()
    {
        return _dependents;
    }

    /*TODO : public void RemoveRedundantDependencies()
    {
        Collection<Vertex> redundants = new ArrayList<Vertex>();

        for(Vertex dependency : _dependencies)


        Dependencies.Where(dependency => Dependencies.FirstOrDefault(d => d != dependency && d.IsDependency(dependency)) != null).ToList();

        for (Vertex redundancy : redundants)
            _dependencies.remove (redundancy);

        for (Vertex dependent : _dependencies)
            dependent.RemoveRedundantDependencies ();
    }*/

    private void CheckForCircularDependency(Collection<Vertex> dependencies) throws CircularDependencyException
    {
        if (dependencies.contains(this))
            throw new CircularDependencyException(this);

        for (Vertex dependency : dependencies)
            CheckForCircularDependency(dependency.get_dependencies());
    }

    public void run()
    {
        _doWorkAction.run();
    }
}