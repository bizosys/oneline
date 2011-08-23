package com.bizosys.oneline.plugin;

import java.lang.reflect.Method;

import com.bizosys.oneline.conf.Configuration;
import com.bizosys.oneline.plugin.PluginDescriptor;
import com.bizosys.oneline.plugin.PluginRepository;

public class PluginRepositoryTest {
	
	  /**
	   * Loads all necessary dependencies for a selected plugin, and then runs one
	   * of the classes' main() method.
	   * 
	   * @param args
	   *          plugin ID (needs to be activated in the configuration), and the
	   *          class name. The rest of arguments is passed to the main method of
	   *          the selected class.
	   * @throws Exception
	   */
	  @SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
	    if (args.length < 2) {
	      System.err.println("Usage: PluginRepository pluginId className [arg1 arg2 ...]");
	      return;
	    }
	    Configuration conf = new Configuration();
	    PluginRepository repo = new PluginRepository(conf);
	    // args[0] - plugin ID
	    PluginDescriptor d = repo.getPluginDescriptor(args[0]);
	    if (d == null) {
	      System.err.println("Plugin '" + args[0] + "' not present or inactive.");
	      return;
	    }
	    ClassLoader cl = d.getClassLoader();
	    // args[1] - class name
	    Class clazz = null;
	    try {
	      clazz = Class.forName(args[1], true, cl);
	    } catch (Exception e) {
	      System.err.println("Could not load the class '" + args[1] + ": "
	          + e.getMessage());
	      return;
	    }
	    Method m = null;
	    try {
	      m = clazz.getMethod("main", new Class[] { args.getClass() });
	    } catch (Exception e) {
	      System.err.println("Could not find the 'main(String[])' method in class "
	          + args[1] + ": " + e.getMessage());
	      return;
	    }
	    String[] subargs = new String[args.length - 2];
	    System.arraycopy(args, 2, subargs, 0, subargs.length);
	    m.invoke(null, new Object[] { subargs });
	  }
}
