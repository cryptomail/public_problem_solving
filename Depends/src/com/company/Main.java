package com.company;

import com.sun.deploy.util.StringUtils;
import com.sun.tools.javac.util.Assert;

import java.io.*;
import java.util.*;

public class Main {

    public class Dependency {
        protected String moduleName;
        protected  List<Dependency> dependencyList;

        public Dependency(String moduleName) {
            this.moduleName = moduleName;
            dependencyList = new ArrayList<>();
        }
    };

    public final static String COMMAND_END = "END";
    public final static String COMMAND_DEPEND = "DEPEND";
    public final static String COMMAND_INSTALL = "INSTALL";
    public final static String COMMAND_REMOVE = "REMOVE";
    public final static String COMMAND_LIST = "LIST";

    protected HashMap<String,Dependency> dependencyHashMap;


    public Main() {
        dependencyHashMap = new HashMap<>();
    }
    protected boolean isValidLine(String line) {
        if(line == null) {
            return false;
        }

        if(
            line.startsWith(COMMAND_END) ||
            line.startsWith(COMMAND_DEPEND) ||
            line.startsWith(COMMAND_INSTALL) ||
            line.startsWith(COMMAND_REMOVE) ||
                    line.startsWith(COMMAND_LIST)

                )
        {
            return true;
        }

        return false;
    }

    protected  boolean isStopLine(String line) {
        return line != null && line.startsWith(COMMAND_END);
    }

    protected  void emit(String str) {
        System.out.print(str);
    }
    protected  int processDependencyModule(String moduleName) {

        if(moduleName == null || moduleName.length() == 0 ) {
            return -1;
        }
        if(!dependencyHashMap.containsKey(moduleName)) {
            Dependency dependency = new Dependency(moduleName);
            dependencyHashMap.put(moduleName,dependency);
        }

        // BUG: THIS PREMATURELY EMITS MAYBE
        emit("DEPEND " + moduleName + " ");
        return 0;

    }

    protected  boolean findDescendent(String rootModule, String target) {
        if(!dependencyHashMap.containsKey((rootModule))) {
            return false;
        }

        Stack<Dependency> stack = new Stack<>();
        Dependency module = dependencyHashMap.get(rootModule);
        stack.push(module);
        while(stack.size() > 0 ) {
            Dependency mod = (Dependency)stack.pop();
            if(mod.moduleName.equals(target)) {
                return true;
            }

            for(Dependency d : mod.dependencyList) {
                stack.push(d);
            }
        }

        return false;
    }
    protected  int processDependency(String moduleName, String dependentName) {

        if(!dependencyHashMap.containsKey(moduleName)) {
            return -1;
        }

        Dependency moduleRoot = dependencyHashMap.get(moduleName);

        if(moduleRoot == null) {
            return -1;
        }

        Dependency dependentModuleEntry = dependencyHashMap.get(dependentName);

        if(findDescendent(dependentName,moduleName)) {
             emit(" depends on " + moduleName + " ignoring command.");
            return -1;
        }
        if(dependentModuleEntry != null) {


            /*
            If we find that dependentName contains the parent we have to back off
             */

            moduleRoot.dependencyList.add(dependentModuleEntry);

        }
        else {

            dependentModuleEntry = new Dependency(dependentName);
            dependencyHashMap.put(dependentName, dependentModuleEntry);
            moduleRoot.dependencyList.add(dependentModuleEntry);
        }

        emit(dependentName);
        emit(" ");
        return 0;
    }
    protected  int processDependLine(String line) {

        StringTokenizer st = new StringTokenizer(line);

        st.nextElement(); //EAT DEPEND :)
        String moduleName = (String)st.nextElement();

        if(moduleName == null || moduleName.length() == 0) {
            return -1;
        }
        if(processDependencyModule(moduleName) < 0) {
            return -1;
        }
        while (st.hasMoreElements()) {
            String dep = (String)st.nextElement();
            int ret;
            ret = processDependency(moduleName,dep);

            if(ret == -1) {
                return -1;
            }

        }

        emit("\n");
        return 0;
    }
    protected  void processLine(String line) {
        if(line == null || line.length() == 0) {
            Assert.check(false);
            return;
        }

        if(isStopLine(line)) {
            return;
        }

        if(!isValidLine(line)) {
            Assert.check(false);
            return;
        }

        if(line.startsWith((COMMAND_DEPEND))) {
            processDependLine(line);
        } else if(line.startsWith(COMMAND_INSTALL)) {
            //processLineInstallLine(line);
        } else if(line.startsWith(COMMAND_LIST)) {
            //processListLine(line);
        }


    }
    public int run(InputStream is) {

        if(is == null) {
            return -1;
        }
        ArrayList<String> lines = new ArrayList<>();

        BufferedReader r1 = new BufferedReader(new InputStreamReader(is));

        boolean bStop = false;
        boolean bErrorState = false;

        while(!bStop) {
            String line;
            try {
                line = r1.readLine();

                if(!isValidLine(line)) {
                    bStop = true;
                    bErrorState = true;
                    break;
                }
                if(isStopLine(line)) {
                    bStop = true;
                    break;
                }
                processLine(line);

            }
            catch (Exception e) {
                bStop = true;
                bErrorState = true;
            }

        }

        if(bErrorState)
        {
            System.err.println("ABNORMAL EXIT DID NOT MEET END");
            return -1;
        }

       return 0;
    }

    /*
    No input should fail
     */
    static public void test0() {
        Main m = new Main();
        StringBuffer sbf = new StringBuffer("");
        byte[] bytes = sbf.toString().getBytes();
        /*
         * Get ByteArrayInputStream from byte array.
         */
        InputStream inputStream = new ByteArrayInputStream(bytes);

        int retval = m.run(inputStream);
        Assert.check(retval == -1);
    }
    /*
    No input ultra jerk input
     */
    static public void test00() {
        Main m = new Main();

        /*
         * Get ByteArrayInputStream from byte array.
         */

        int retval = m.run(null);
        Assert.check(retval == -1);
    }

    static public void testDependOneLine() {
        Main m = new Main();
        StringBuffer sbf = new StringBuffer("DEPEND TELNET TCPIP NETCARD\nEND");
        byte[] bytes = sbf.toString().getBytes();
        /*
         * Get ByteArrayInputStream from byte array.
         */
        InputStream inputStream = new ByteArrayInputStream(bytes);

        int retval = m.run(inputStream);
        Assert.check(retval == 0);
    }
    static public void testDependTwoLine() {
        Main m = new Main();
        StringBuffer sbf = new StringBuffer("DEPEND TELNET TCPIP NETCARD\nDEPEND TCPIP NETCARD\nEND");
        byte[] bytes = sbf.toString().getBytes();
        /*
         * Get ByteArrayInputStream from byte array.
         */
        InputStream inputStream = new ByteArrayInputStream(bytes);

        int retval = m.run(inputStream);
        Assert.check(retval == 0);
    }
    static public void testDependThreeLine() {
        Main m = new Main();
        StringBuffer sbf = new StringBuffer("DEPEND TELNET TCPIP NETCARD\nDEPEND TCPIP NETCARD\nDEPEND NETCARD TCPIP\nEND");
        byte[] bytes = sbf.toString().getBytes();
        /*
         * Get ByteArrayInputStream from byte array.
         */
        InputStream inputStream = new ByteArrayInputStream(bytes);

        int retval = m.run(inputStream);
        Assert.check(retval == 0);
    }
    /*
    just end success. nonce run.
     */
    static public void test1() {
        Main m = new Main();
        StringBuffer sbf = new StringBuffer("END");
        byte[] bytes = sbf.toString().getBytes();
        /*
         * Get ByteArrayInputStream from byte array.
         */
        InputStream inputStream = new ByteArrayInputStream(bytes);

        int retval = m.run(inputStream);
        Assert.check(retval == 0);
    }

    public static void main(String[] args) {
	// write your code here


        testDependThreeLine();


    }
}
