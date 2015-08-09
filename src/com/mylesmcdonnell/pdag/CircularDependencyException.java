package com.mylesmcdonnell.pdag;

/**
 * Created by myles on 09/08/2015.
 */
public class CircularDependencyException extends Throwable {

    private final Vertex _dependency;

    public CircularDependencyException(Vertex dependency) {
        _dependency = dependency;
    }

    public Vertex get_dependency() {
        return _dependency;
    }
}
