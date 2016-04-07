package com.company;

import com.sun.deploy.util.StringUtils;
import com.sun.tools.javac.util.Assert;

import java.io.*;
import java.util.*;

public class Main {

    String theInput = "DEPEND TELNET TCPIP NETCARD\n"+
            "DEPEND TCPIP NETCARD\n"+
            "DEPEND DNS TCPIP NETCARD\n"+
            "DEPEND BROWSER TCPIP HTML\n"+
            "INSTALL NETCARD\n"+
            "INSTALL TELNET\n"+
            "INSTALL foo\n"+
            "REMOVE NETCARD\n"+
            "INSTALL BROWSER\n"+
            "INSTALL DNS\n"+
            "LIST\n"+
            "REMOVE TELNET\n"+
            "REMOVE NETCARD\n"+
            "REMOVE DNS\n"+
            "REMOVE NETCARD\n"+
            "INSTALL NETCARD\n"+
            "REMOVE TCPIP\n"+
            "REMOVE BROWSER\n"+
            "REMOVE TCPIP\n"+
            "END";

    public class Dependency {
        protected String moduleName;
        protected  List<Dependency> dependencyList;

        public Dependency(String moduleName) {
            this.moduleName = moduleName;
            dependencyList = new ArrayList<>();
        }
    };

    public class DependencyInstance  {

        protected int refCount;
        protected String installAgent;
        protected Dependency dependency;
        public DependencyInstance(Dependency dependency) {
            this.dependency = dependency;
            this.installAgent = null;
            refCount = 0;

        }
    };
    public final static String COMMAND_END = "END";
    public final static String COMMAND_DEPEND = "DEPEND";
    public final static String COMMAND_INSTALL = "INSTALL";
    public final static String COMMAND_REMOVE = "REMOVE";
    public final static String COMMAND_LIST = "LIST";

    protected HashMap<String,Dependency> dependencyHashMap;
    protected HashMap<String,DependencyInstance> installManifest;


    public Main() {
        dependencyHashMap = new HashMap<>();
        installManifest = new HashMap<>();
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

        if(moduleName == null || moduleName.length() == 0) {
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
             emit(" depends on " + dependentName + " ignoring command.\n");
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
    protected  boolean removeModule(String moduleName) {

        if(!installManifest.containsKey(moduleName)) {
            return false;
        }
        DependencyInstance dependencyInstance = installManifest.get(moduleName);

        if(dependencyInstance.refCount > 1) {
            return false;
        }

        boolean removed = true;
        for(Dependency d : dependencyInstance.dependency.dependencyList) {
            DependencyInstance dependencyInstance1 = installManifest.get(d.moduleName);

            dependencyInstance1.refCount--;
            if(dependencyInstance1.refCount == 0) {
                emit("\tremoving " + d.moduleName + "\n" );
                installManifest.remove(d.moduleName);
            }
        }


        return removed;
    }
    protected  boolean processRemoveLine(String line) {

        StringTokenizer st = new StringTokenizer(line);

        st.nextElement(); //EAT REMOVE :)
        String moduleName = (String)st.nextElement();

        if(moduleName == null || moduleName.length() == 0) {
            return false;
        }

        emit("REMOVE " + moduleName + "\n");
        if(!installManifest.containsKey(moduleName)) {

            emit("\t" + moduleName + " is not installed\n");
            return false;
        }

        boolean retval =  removeModule(moduleName);

        if(retval == false) {
            emit("\t" + moduleName + " is still needed\n");
        }
        else {
            emit("\tremoving " + moduleName + "\n");
        }
        return retval;
    }
    protected boolean installModule(String module, String agent) {
        Dependency dependency = dependencyHashMap.get(module);
        DependencyInstance dependencyInstance = installManifest.get(module);

        boolean reallyDidIt = false;
        if(dependency == null) {
            dependency = new Dependency(module);
        }
        if(dependencyInstance == null) {
            dependencyInstance = new DependencyInstance(dependency);
            reallyDidIt = true;

        }
        dependencyInstance.refCount++;
        dependencyInstance.installAgent = agent;
        installManifest.put(module, dependencyInstance);

        for(Dependency d: dependency.dependencyList) {

            installModule(d.moduleName, agent);

        }
        if(reallyDidIt) {
            emit("\tInstalling " + module + "\n");
        }


        return reallyDidIt;
    }
    protected  int processInstall(String module, String agent) {
        emit("INSTALL " + module + "\n");
        if(installManifest.containsKey(module)) {

            /*
            Question: if agent is commandline, does this replace with a force? easy out for now.
             */
            emit("\t" + module + " is already installed \n");
            return 0;
        }

        installModule(module, agent);
        return 0;
    }
    protected int processLineInstallLine(String line, String agent) {
        StringTokenizer st = new StringTokenizer(line);

        st.nextElement(); //EAT DEPEND :)
        String moduleName = (String)st.nextElement();

        if(moduleName == null || moduleName.length() == 0) {
            return -1;
        }

        return processInstall(moduleName,agent);

    }
    protected void processListLine(String line) {
        emit("LIST\n");
        for(DependencyInstance dependencyInstance : installManifest.values()) {
            emit("\t" + dependencyInstance.dependency.moduleName + "\n");
        }
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
            processLineInstallLine(line,"commandline");
        } else if(line.startsWith(COMMAND_LIST)) {
            processListLine(line);
        }
        else if(line.startsWith(COMMAND_REMOVE)) {
            processRemoveLine(line);
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
                    emit("END\n");
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
    static public void testDependFourLine() {
        Main m = new Main();
        StringBuffer sbf = new StringBuffer("DEPEND TELNET TCPIP NETCARD\nDEPEND TCPIP NETCARD\nDEPEND NETCARD TCPIP\nDEPEND DNS TCPIP NETCARD\nEND");
        byte[] bytes = sbf.toString().getBytes();
        /*
         * Get ByteArrayInputStream from byte array.
         */
        InputStream inputStream = new ByteArrayInputStream(bytes);

        int retval = m.run(inputStream);
        Assert.check(retval == 0);
    }
    static public void testDependSixLine() {
        Main m = new Main();
        StringBuffer sbf = new StringBuffer("DEPEND TELNET TCPIP NETCARD\nDEPEND TCPIP NETCARD\nDEPEND NETCARD TCPIP\nDEPEND DNS TCPIP NETCARD\nINSTALL NETCARD\nINSTALL TELNET\nEND");
        byte[] bytes = sbf.toString().getBytes();
        /*
         * Get ByteArrayInputStream from byte array.
         */
        InputStream inputStream = new ByteArrayInputStream(bytes);

        int retval = m.run(inputStream);
        Assert.check(retval == 0);
    }
    static public void testDependSevenLine() {
        Main m = new Main();
        StringBuffer sbf = new StringBuffer("DEPEND TELNET TCPIP NETCARD\nDEPEND TCPIP NETCARD\nDEPEND NETCARD TCPIP\nDEPEND DNS TCPIP NETCARD\nINSTALL NETCARD\nINSTALL TELNET\nINSTALL foo\nEND");
        byte[] bytes = sbf.toString().getBytes();
        /*
         * Get ByteArrayInputStream from byte array.
         */
        InputStream inputStream = new ByteArrayInputStream(bytes);

        int retval = m.run(inputStream);
        Assert.check(retval == 0);
    }
    static public void testDependEightLine() {
        Main m = new Main();
        StringBuffer sbf = new StringBuffer("DEPEND TELNET TCPIP NETCARD\nDEPEND TCPIP NETCARD\nDEPEND NETCARD TCPIP\nDEPEND DNS TCPIP NETCARD\nINSTALL NETCARD\nINSTALL TELNET\nINSTALL FOO\nREMOVE NETCARD\nREMOVE TELNET\nEND");
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
    static public void fulltest() {
        Main m = new Main();
        StringBuffer sbf = new StringBuffer(m.theInput);
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


        fulltest();


    }
}
