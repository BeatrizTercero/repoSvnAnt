package org.apache.tools.ant.taskdefs;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.taskdefs.PreSetDef.PreSetDefinition;
import org.apache.tools.ant.types.EnumeratedAttribute;


/**
 * <p>Starts or stops autoconfiguration for tasks and datatypes.
 * Autoconfiguration applies properties to tasks and datatypes if
 * their names fit to the tasks/datatype and attribute names and the 
 * attribute is not set.</p>
 * 
 * <p>The property name must match the following rule:<br>
 * <tt>propertyname ::= prefix? taskname "." attributename</tt></p> 
 * 
 * <p><b>Example:</b><br>
 * You have defined properties like <tt>javac.debug=on, javac.target=1.5,
 * javac.source=1.5</tt>. Instead of applying these values manually you
 * could write <tt>&lt;autoconf/&gt; &lt;javac&gt;</tt></p>
 * 
 * @since Ant 1.8.0
 */
public class AutoconfTask extends Task {
	
	/** Start or stop the AutoconfListener? */
	boolean start = true;
	
	/** Optional name prefix (before the task name). */
	String prefix = null;
	
    public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void execute() {
		if (start) {
			addAutoconfListener();
		} else {
			removeAutoconfListener();
		}
	}

	/**
	 * Searches all registered AutoconfListener and removes them.
	 */
	private void removeAutoconfListener() {
		Iterator it = getProject().getBuildListeners().iterator();
		while(it.hasNext()) {
			Object next = it.next();
			if (next instanceof AutoconfListener) {
				getProject().removeBuildListener(((AutoconfListener) next));
			}
		}
	}

	/**
	 * Adds a new AutoconfListener.
	 */
	private void addAutoconfListener() {
		getProject().addBuildListener(new AutoconfListener(prefix));
	}

    /**
     * Sets the action for the autoconf task.
     *
     * @param action The action for the entry to take: start or stop.
     */
    public void setAction(ActionChoices action) {
        start = action.getValue().equalsIgnoreCase("start");
    }
	
    
    //-------------------------------------------------------------------------
	
    
    /**
     * A list of possible values for the <code>setAction()</code> method.
     * Possible values include: start and stop.
     */
    public static class ActionChoices extends EnumeratedAttribute {
        private static final String[] VALUES = {"start", "stop"};

        /**
         * @see EnumeratedAttribute#getValues()
         */
        /** {@inheritDoc}. */
        public String[] getValues() {
            return VALUES;
        }
    }
    
    
    //-------------------------------------------------------------------------

    
    /**
     * Implements the <i>AOP</i>-part of the autoconf task (the real autoconf logic).
     * When starting a task, it searches for the properties and applies them.
     */
    public class AutoconfListener implements BuildListener {

    	/** Prefix for the property name. */
        private String prefix;

		public AutoconfListener(String prefix) {
			this.prefix = prefix;
		}

		public void buildStarted(BuildEvent event) {
            // no-op
        }

        public void buildFinished(BuildEvent event) {
            // no-op
        }

        public void targetStarted(BuildEvent event) {
            // no-op
        }

        public void targetFinished(BuildEvent event) {
            // no-op
        }

        public void taskFinished(BuildEvent event) {
            // no-op
        }

        public void messageLogged(BuildEvent event) {
            // no-op
        }

        public void taskStarted(BuildEvent event) {
            UnknownElement ue = (UnknownElement) event.getTask();
            
            // Access to the properties which are available at 'call time'.
            Hashtable properties = ue.getProject().getProperties();
            
            // Name of the task, like 'echo', 'javac'
            String name = ue.getTag();
            
            // Access to the task object before configuration
            RuntimeConfigurable rc = ue.getWrapper();
            
            // Attributes set by parsing the build file: must not overwrite these!
            Set alreadySetAttributes = rc.getAttributeMap().keySet();

            // All supported attributes by the task
            Set allAttributes = getTaskAttributes(name, ue.getProject());
            
            // Try to apply all properties with prefix 'prefix'+'taskname' to the task object
            String propPrefix = prefix == null ? name + "." : prefix + name + ".";
            for (Iterator keys = properties.keySet().iterator(); keys.hasNext(); ) {
            	String propName = (String) keys.next();
            	if (!propName.startsWith(propPrefix)) {
            		// skip properties which don't apply to the task
            		continue;
            	}
    			String attributeName = propName.substring(propPrefix.length());
        		if (!alreadySetAttributes.contains(attributeName) && allAttributes.contains(attributeName)) {
        			// Register the key-value pair for later setting via IntrospectionHelper
        			// while doing UnknownElement.configure() --> RuntimeConfigurable.maybeConfigure()
        			// --> IntrospectionHelper.setAttribute()
				    rc.setAttribute(attributeName, String.valueOf(properties.get(propName)));
        		} else {
        			// do not overwrite already set attributes
        			// do not try to set attributes which do not exist
        		}
            }
        }

    	/**
    	 * Tries to analyse a task and retrieve the attributes.
    	 * The algorithm creates a dummy instance as template and analyses
    	 * that.
    	 * @param name name of the task/datatype
    	 * @param project Ant Project instance
    	 * @return (notnull) list of attributes
    	 */
    	private Set getTaskAttributes(String name, Project project) {
    		Object templateObject = createTemplateObject(name, project);
    		
    		if (templateObject == null) {
    			// can not create a template object, so give up
    			return new HashSet();
    		}
    		
    		if (templateObject instanceof MacroInstance) {
    			// we can ask <macrodef> directly for its attributes
    			return getMacrodefAttributes((MacroInstance) templateObject);
    		}
    		
    		// analyse class via reflection
    		Class templateClass = createTemplateClass(project, templateObject);
    		return getSetter(templateClass);
    	}

		/**
		 * Returns a list of attributes with public setter for a given class. 
		 * @param templateClass class to analyze
		 * @return (notnull) list of attribute names
		 */
		private Set getSetter(Class templateClass) {
			Set rv = new HashSet();
			Method[] methods = templateClass.getDeclaredMethods();
    		for (int i = 0; i < methods.length; i++) {
    			Method m = methods[i];
    			if (isSetter(m)) {
    				String attributeName = m.getName().substring(3);
    				attributeName = attributeName.substring(0,1).toLowerCase() + attributeName.substring(1);
    				rv.add( attributeName );
    			}
    		}
    		return rv;
		}

		/**
		 * Checks if a given method is a public setter,
		 * means is public, starts with <tt>set</tt> and requires one
		 * argument.
		 * This method allows return values.
		 * @param method method to check
		 * @return <tt>true</tt> if the method is a setter
		 */
		private boolean isSetter(Method method) {
			return method.getName().startsWith("set") 
			    && method.getParameterTypes().length == 1 
			    && method.getModifiers()==Modifier.PUBLIC;
		}

		/**
		 * Creates a template class to a given object.
		 * Depending on the object type, it is asked differently.
		 * @param project Ant project instance
		 * @param templateObject template object
		 * @return template class for later analyze
		 */
		private Class createTemplateClass(Project project, Object templateObject) {
			Class templateClass = null;
    		if (templateObject instanceof PreSetDefinition) {
    			PreSetDefinition preset = (PreSetDefinition) templateObject;
    			templateClass = preset.getTypeClass(project);
    		} else {
    			templateClass = templateObject.getClass();
    		}
			return templateClass;
		}

		/**
		 * Returns the attributes of a <macrodef>.
		 * @param macro the macro instance
		 * @return attribute list
		 */
		private Set getMacrodefAttributes(MacroInstance macro) {
			Set rv = new HashSet();
			MacroDef macroDef = macro.getMacroDef();
			List attributes = macroDef.getAttributes();
			for (Iterator it = attributes.iterator(); it.hasNext();) {
				MacroDef.Attribute object = (MacroDef.Attribute) it.next();
				rv.add(object.getName());
			}
			return rv;
		}

		/**
		 * Creates a template object for the task/datatype of the given name.
		 * @param name name of the task/datatype
		 * @param project Ant project instance for resolving the name
		 * @return template object
		 */
		private Object createTemplateObject(String name, Project project) {
			Object templateObject = null;
    		try {
    			templateObject = project.createTask(name);
    		} catch (Exception e) {
    			// the task name is recognised but task creation fails.
    			try {
    				// retry as data type, e.g. <presetdef>
    				templateObject = project.createDataType(name);
    			} catch(Exception e2) {
    				// give it up
    			}
    		}
			return templateObject;
		}
        
    }
}