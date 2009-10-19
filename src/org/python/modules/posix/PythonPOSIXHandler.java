/* Copyright (c) Jython Developers */
package org.python.modules.posix;

import com.kenai.constantine.Constant;
import com.kenai.constantine.ConstantSet;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

import org.jruby.ext.posix.POSIX;
import org.jruby.ext.posix.POSIXHandler;

import org.python.core.imp;
import org.python.core.Py;
import org.python.core.PyObject;

/**
 * Jython specific hooks for our underlying POSIX library.
 */
public class PythonPOSIXHandler implements POSIXHandler {

    private ConstantSet errnos = ConstantSet.getConstantSet("Errno");

    public void error(POSIX.ERRORS error, String extraData) {
        Constant errno = errnos.getConstant(error.name());
        if (errno == null) {
            throw Py.OSError(extraData);
        }
        throw Py.OSError(errno, extraData);
    }

    public void unimplementedError(String methodName) {
        throw Py.NotImplementedError(methodName);
    }

    public void warn(WARNING_ID id, String message, Object... data) {
    }

    public boolean isVerbose() {
        return false;
    }

    public File getCurrentWorkingDirectory() {
        return new File(Py.getSystemState().getCurrentWorkingDir());
    }

    public String[] getEnv() {
        PyObject items = imp.load("os").__getattr__("environ").invoke("items");
        String[] env = new String[items.__len__()];
        int i = 0;
        for (PyObject item : items.asIterable()) {
            env[i++] = String.format("%s=%s", item.__getitem__(0), item.__getitem__(1));
        }
        return env;
    }

    public InputStream getInputStream() {
        return System.in;
    }

    public PrintStream getOutputStream() {
        return System.out;
    }

    public int getPID() {
        return 0;
    }

    public PrintStream getErrorStream() {
        return System.err;
    }
}