/* 
   Security Manager
   should get as restrictive as possible
   atm it is totally insecure !!!!


   is based upon:

   java.lang.SecurityManager
   Copyright (C) 1998, 1999, 2001 Free Software Foundation, Inc.

   This file is part of GNU Classpath.

   GNU Classpath is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.
 
   GNU Classpath is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with GNU Classpath; see the file COPYING.  If not, write to the
   Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
   02111-1307 USA.

   Linking this library statically or dynamically with other modules is
   making a combined work based on this library.  Thus, the terms and
   conditions of the GNU General Public License cover the whole
   combination.

   As a special exception, the copyright holders of this library give you
   permission to link this library with independent modules to produce an
   executable, regardless of the license terms of these independent
   modules, and to copy and distribute the resulting executable under
   terms of your choice, provided that you also meet, for each linked
   independent module, the terms and conditions of the license of that
   module.  An independent module is a module which is not derived from
   or based on this library.  If you modify this library, you may extend
   this exception to your version of the library, but you are not
   obligated to do so.  If you do not wish to do so, delete this
   exception statement from your version. */

package org.jgachine;

import java.io.*;
import java.security.Permission;

public class MySecurityManager extends SecurityManager {
    /** Check if the current thread is allowed to read or
     ** write a particular system property.<P>
     **
     ** This method is called by System.getProperty() and
     ** setProperty().<P>
     **
     ** SecurityManager's implementation always denies access.
     **
     ** @exception SecurityException is the operation is not
     **            permitted.
     ** @see java.lang.System#getProperty(java.lang.String)
     ** @see java.lang.System#setProperty(java.lang.String,java.lang.String)
     **/
    public void checkPropertyAccess(String name) {
	// todo this is very specific to the jvm used
	if (name.equals("path.separator")) return;
	if (name.equals("java.io.tmpdir")) return;
	if (name.equals("file.separator")) return;
	if (name.equals("file.encoding")) return;
	if (name.equals("java.class.path")) return;
	if (name.equals("line.separator")) return;
	if (name.equals("user.dir")) return;
	if (name.equals("gnu.classpath.home.url")) return;
	if (name.equals("gnu.classpath.vm.shortname")) return;
	if (name.startsWith("gnu.gcj.runtime.NameFinder.")) return;
	if (name.startsWith("gnu.java.io.ByteArrayOutputStream")) return;
	debug("checkPropertyAccess: "+name);
	throw new SecurityException("Cannot access individual system property:"+name);
    }

    /** Check if the current thread is allowed to read the
     ** given file.<P>
     **
     ** This method is called from
     ** FileInputStream.FileInputStream(),
     ** RandomAccessFile.RandomAccessFile(), File.exists(),
     ** canRead(), isFile(), isDirectory(), lastModified(),
     ** length() and list().<P>
     **
     ** SecurityManager's implementation always denies access.
     **
     ** @param filename the full name of the file to access
     ** @exception SecurityException if the operation is not
     **            permitted.
     ** @see java.io.File
     ** @see java.io.FileInputStream#FileInputStream(java.lang.String)
     ** @see java.io.RandomAccessFile#RandomAccessFile(java.lang.String)
     **/
    public void checkRead(String filename) {
	// todo security hole
	debug("fileread: \""+filename+"\"");
    }
    public void checkRead(String filename, Object context)
    {
	checkRead(filename);
    }

    /** Check if the current thread is allowed to exit the
     ** JVM with the given status.<P>
     **
     ** This method is called from Runtime.exit().<P>
     **
     ** SecurityManager's implementation always denies access.
     **
     ** @param status the status to exit with
     ** @exception SecurityException if the operation is not
     **            permitted.
     ** @see java.lang.Runtime#exit()
     ** @see java.lang.Runtime#exit(int)
     **/
    public void checkExit(int status) {
    }


    /**
     * Check if the current thread is allowed to perform an operation that
     * requires the specified <code>Permission</code>. This defaults to
     * <code>AccessController.checkPermission</code>.
     *
     * @param perm the <code>Permission</code> required
     * @throws SecurityException if permission is denied
     * @throws NullPointerException if perm is null
     * @since 1.2
     */
    public void checkPermission(Permission perm)
    {
	// todo this was not implemented int gcc 3.3.2 yet
	// security hole
	debug("checkPermission");
    }

    /**
     * Check if the current thread is allowed to perform an operation that
     * requires the specified <code>Permission</code>. This is done in a
     * context previously returned by <code>getSecurityContext()</code>. The
     * default implementation expects context to be an AccessControlContext,
     * and it calls <code>AccessControlContext.checkPermission(perm)</code>.
     *
     * @param perm the <code>Permission</code> required
     * @param context a security context
     * @throws SecurityException if permission is denied, or if context is
     *         not an AccessControlContext
     * @throws NullPointerException if perm is null
     * @see #getSecurityContext()
     * @see AccessControlContext#checkPermission(Permission)
     * @since 1.2
     */
    public void checkPermission(Permission perm, Object context)
    {
	checkPermission(perm);
    }


    static protected void debug(String s){
	System.out.println("Security: "+s);
    }
}
