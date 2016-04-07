

import java.io.*;
import java.util.*;



public class Main {

    String theInput2 = "DEPEND TELNET TCPIP NETCARD\n"+
            "DEPEND TCPIP NETCARD\n"+
            "DEPEND DNS TCPIP NETCARD\n"+
            "DEPEND BROWSER TCPIP HTML\n"+
            "INSTALL NETCARD\n"+
            "INSTALL TELNET\n"+
            "LIST\n"+
            "INSTALL foo\n"+
            "REMOVE NETCARD\n"+
            "INSTALL BROWSER\n"+
            "LIST\n"+
            "INSTALL DNS\n"+
            "LIST\n"+
            "REMOVE TELNET\n"+
            "LIST\n"+
            "REMOVE NETCARD\n"+
            "REMOVE DNS\n"+
            "LIST\n"+
            "REMOVE NETCARD\n"+
            "INSTALL NETCARD\n"+
            "LIST\n"+
            "REMOVE TCPIP\n"+
            "REMOVE BROWSER\n"+
            "REMOVE TCPIP\n"+
            "REMOVE NETCARD\n"+
            "LIST\n"+
            "END";
    
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

        protected HashMap<String,Integer> refCount;
        protected String installAgent;
        protected Dependency dependency;
        public DependencyInstance(Dependency dependency) {
            this.dependency = dependency;
            this.installAgent = null;
            this.refCount = new HashMap();
            

        }
        public boolean anyRef() {
        	for(String from: refCount.keySet()) {
        		if(refCount.get(from) > 0) {
        			return true;
        		}
        	}
        	return false;
        }
        public int totalRefExcept(String str) {
        	int cnt = 0;
        	for(String from: refCount.keySet()) {
        		if(!str.equals(from)) {
        			cnt += refCount.get(from);
        		}
        		
        	}
        	return cnt;
        }
        public int totalRef() {
        	int cnt = 0;
        	for(String from: refCount.keySet()) {
        		cnt += refCount.get(from);
        	}
        	return cnt;
        }
        public boolean hasRefFrom(String from) {
        	if(refCount.containsKey(from) && refCount.get(from) > 0) {
        		return true;
        	}
        	
        	return false;
        }
        public int addRefFrom(String from) {
        	Integer i;
        	if(refCount.containsKey(from))
        	{
        		i = refCount.get(from);
        		
        	}
        	else {
        		i = new Integer(0);
        		
        		
        	}
        	i++;
        	if(i >= 0) {
        		refCount.put(from, i);
        	}
        	
        	return i;
        }
        public int removeRefFrom(String from) {
        	Integer i;
        	if(hasRefFrom(from) == false) {
        		return 0;
        	}
        	i = refCount.get(from);
        	i--;
        	
        	refCount.put(from, i);
        	
        	return i;
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

        emit("DEPEND " + moduleName + " ");
        return 0;

    }

    /*
    In order, non recursive implmentation of find.  Because I like it!
     */
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
    protected  boolean removeModule(String moduleName, String agent) {

        if(!installManifest.containsKey(moduleName)) {
            return false;
        }
        DependencyInstance dependencyInstance = installManifest.get(moduleName);



        boolean removed = false;
        for(Dependency d : dependencyInstance.dependency.dependencyList) {
            DependencyInstance dependencyInstance1 = installManifest.get(d.moduleName);
            if(dependencyInstance1 == null) {
                continue;
            }
            removeModule(dependencyInstance1.dependency.moduleName,moduleName);
            
        }

        dependencyInstance.removeRefFrom(agent);
        
        if(dependencyInstance.anyRef() == false) {
            emit("\tremoving " + dependencyInstance.dependency.moduleName + "\n" );
            installManifest.remove(dependencyInstance.dependency.moduleName);
            removed = true;

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

        

        boolean retval;
        if(installManifest.containsKey(moduleName) && installManifest.get(moduleName).totalRefExcept("commandline")  > 0 ) {
            emit("\t" + moduleName + " is still needed " + installManifest.get(moduleName).refCount +  " \n");
            retval = false;
        }
        else {
        	 retval = removeModule(moduleName,"commandline");
        }
        return retval;
    }
    public String dump(HashMap<String,Integer> map) {
    	String s = "";
    	Iterator iterator = map.keySet().iterator();

    	while (iterator.hasNext()) {
    	   String key = iterator.next().toString();
    	   Integer value = map.get(key);

    	   s += (key + " " + value);
    	}
    	return s;
    }
    protected boolean installModule(String module, String agent) {
        Dependency dependency = dependencyHashMap.get(module);
        DependencyInstance dependencyInstance = installManifest.get(module);
        
        if(dependency == null) {
        	dependency = new Dependency(module);
        }
        boolean reallyDidIt = false;
       
        if(dependencyInstance == null) {
            dependencyInstance = new DependencyInstance(dependency);
            reallyDidIt = true;
        }
        dependencyInstance.addRefFrom(agent);
        
        dependencyInstance.installAgent = agent;
        emit("\tDEBUG: " + module + " " +  dependencyInstance.refCount + "\n");
        installManifest.put(module, dependencyInstance);

        for(Dependency d: dependency.dependencyList) {

            installModule(d.moduleName, module);

        }
        
        if(reallyDidIt) {
        	emit("\t installing " + module + "\n");
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
            emit("\t" + dependencyInstance.dependency.moduleName + " " + dependencyInstance.refCount + "\n");
        }
    }
    protected  void processLine(String line) {
        if(line == null || line.length() == 0) {
            
            return;
        }

        if(isStopLine(line)) {
            return;
        }

        if(!isValidLine(line)) {
           
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
                e.printStackTrace();
                bErrorState = true;
            }

        }

        if(bErrorState)
        {
            //System.err.println("ABNORMAL EXIT DID NOT MEET END");
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
        assert(retval == 0);
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
        assert(retval == 0);
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
        assert(retval == 0);
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
        assert(retval == 0);
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
        assert(retval == 0);
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
        assert(retval == 0);
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
        assert(retval == 0);
    }
    /*
    just end success. nonce run.
     */
    static public void testNegativeUnknowncommand() {
        Main m = new Main();
        StringBuffer sbf = new StringBuffer("LIST\nFOOBAR SOMETHING\nEND");
        byte[] bytes = sbf.toString().getBytes();
        /*
         * Get ByteArrayInputStream from byte array.
         */
        InputStream inputStream = new ByteArrayInputStream(bytes);

        int retval = m.run(inputStream);
        assert(retval == -1);
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
        assert(retval == 0);
    }
    static public void fulltest_positive() {
        Main m = new Main();
        StringBuffer sbf = new StringBuffer(m.theInput);
        byte[] bytes = sbf.toString().getBytes();
        /*
         * Get ByteArrayInputStream from byte array.
         */
        InputStream inputStream = new ByteArrayInputStream(bytes);

        int retval = m.run(inputStream);
        assert(retval == 0);
    }
    static public void fulltest_positive2() {
        Main m = new Main();
        StringBuffer sbf = new StringBuffer(m.theInput2);
        byte[] bytes = sbf.toString().getBytes();
        /*
         * Get ByteArrayInputStream from byte array.
         */
        InputStream inputStream = new ByteArrayInputStream(bytes);

        int retval = m.run(inputStream);
        assert(retval == 0);
    }

    static public void testListOnly() {
        Main m = new Main();
        StringBuffer sbf = new StringBuffer("LIST\nEND");
        byte[] bytes = sbf.toString().getBytes();
        /*
         * Get ByteArrayInputStream from byte array.
         */
        InputStream inputStream = new ByteArrayInputStream(bytes);

        int retval = m.run(inputStream);
        
    }
    public static void main(String[] args) {
	// write your code here

        /*test00();
        test0();
        test1();
        testDependTwoLine();
        testDependThreeLine();
        testDependFourLine();
        testDependSixLine();
        testDependSevenLine();
        testDependEightLine();
        testNegativeUnknowncommand();
        testListOnly();*/

        /*
        I think this is the final test
         */
        fulltest_positive();
        //fulltest_positive2();



    }
}
