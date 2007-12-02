/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.ivy.plugins.latest;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.ivy.core.IvyContext;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.plugins.version.VersionMatcher;




public class LatestRevisionStrategy extends ComparatorLatestStrategy {
    public static class SpecialMeaning {
        private String _name;
        private Integer _value;
        public String getName() {
            return _name;
        }
        public void setName(String name) {
            _name = name;
        }
        public Integer getValue() {
            return _value;
        }
        public void setValue(Integer value) {
            _value = value;
        }
        public void validate() {
            if (_name == null) {
                throw new IllegalStateException("a special meaning should have a name");
            }
            if (_value == null) {
                throw new IllegalStateException("a special meaning should have a value");
            }            
        }
    }

    private static final Map DEFAULT_SPECIAL_MEANINGS;
    static {
        DEFAULT_SPECIAL_MEANINGS = new HashMap();
        DEFAULT_SPECIAL_MEANINGS.put("dev", new Integer(-1));
        DEFAULT_SPECIAL_MEANINGS.put("rc", new Integer(1));
        DEFAULT_SPECIAL_MEANINGS.put("final", new Integer(2));
    }

    /**
     * Compares two ModuleRevisionId by their revision.
     * Revisions are compared using an algorithm inspired by PHP
     * version_compare one. 
     */ 
    public final Comparator STATIC_COMPARATOR = new Comparator() {
    	public int compare(Object o1, Object o2) {
            String rev1 = ((ModuleRevisionId)o1).getRevision();
            String rev2 = ((ModuleRevisionId)o2).getRevision();
            
            rev1 = rev1.replaceAll("([a-zA-Z])(\\d)", "$1.$2");
            rev1 = rev1.replaceAll("(\\d)([a-zA-Z])", "$1.$2");
            rev2 = rev2.replaceAll("([a-zA-Z])(\\d)", "$1.$2");
            rev2 = rev2.replaceAll("(\\d)([a-zA-Z])", "$1.$2");
            
            String[] parts1 = rev1.split("[\\._\\-\\+]");
            String[] parts2 = rev2.split("[\\._\\-\\+]");
            
            int i = 0;
            for (; i < parts1.length && i <parts2.length; i++) {
                if (parts1[i].equals(parts2[i])) {
                    continue;
                }
                boolean is1Number = isNumber(parts1[i]);
                boolean is2Number = isNumber(parts2[i]);
                if (is1Number && !is2Number) {
                    return 1;
                }
                if (is2Number && !is1Number) {
                    return -1;
                }
                if (is1Number && is2Number) {
                    return Long.valueOf(parts1[i]).compareTo(Long.valueOf(parts2[i]));
                }
                // both are strings, we compare them taking into account special meaning
                Map specialMeanings = getSpecialMeanings();
                Integer sm1 = (Integer)specialMeanings.get(parts1[i].toLowerCase());
                Integer sm2 = (Integer)specialMeanings.get(parts2[i].toLowerCase());
                if (sm1 != null) {
                    sm2 = sm2==null?new Integer(0):sm2;
                    return sm1.compareTo(sm2);
                }
                if (sm2 != null) {
                    return new Integer(0).compareTo(sm2);
                }
                return parts1[i].compareTo(parts2[i]);
            }
            if (i < parts1.length) {
                return isNumber(parts1[i])?1:-1;
            }
            if (i < parts2.length) {
                return isNumber(parts2[i])?-1:1;
            }
            return 0;
    	}

        private boolean isNumber(String str) {
            return str.matches("\\d+");
        }
    };

    
    /**
     * Compares two ArtifactInfo by their revision.
     * Revisions are compared using an algorithm inspired by PHP
     * version_compare one, unless a dynamic revision is given,
     * in which case the version matcher is used to perform the comparison. 
     */ 
    public Comparator COMPARATOR = new Comparator() {

        public int compare(Object o1, Object o2) {
            String rev1 = ((ArtifactInfo)o1).getRevision();
            String rev2 = ((ArtifactInfo)o2).getRevision();

            /* The revisions can still be not resolved, so we use the current 
             * version matcher to know if one revision is dynamic, and in this 
             * case if it should be considered greater or lower than the other one.
             * 
             * Note that if the version matcher compare method returns 0, it's because
             * it's not possible to know which revision is greater. In this case we 
             * consider the dynamic one to be greater, because most of the time
             * it will then be actually resolved and a real comparison will occur.
             */  
            VersionMatcher vmatcher = IvyContext.getContext().getSettings().getVersionMatcher();
            ModuleRevisionId mrid1 = ModuleRevisionId.newInstance("", "", rev1);
            ModuleRevisionId mrid2 = ModuleRevisionId.newInstance("", "", rev2);
            if (vmatcher.isDynamic(mrid1)) {
            	int c = vmatcher.compare(mrid1, mrid2, STATIC_COMPARATOR);
				return c >= 0? 1 : -1;
            } else if (vmatcher.isDynamic(mrid2)) {
            	int c = vmatcher.compare(mrid2, mrid1, STATIC_COMPARATOR);
				return c >= 0? -1 : 1;
            }
            
            return STATIC_COMPARATOR.compare(mrid1, mrid2);
        }
    
    };
    
    private Map _specialMeanings = null;
    private boolean _usedefaultspecialmeanings = true;
    
    public LatestRevisionStrategy() {
    	setComparator(COMPARATOR);
        setName("latest-revision");
    }
    
    public void addConfiguredSpecialMeaning(SpecialMeaning meaning) {
        meaning.validate();
        getSpecialMeanings().put(meaning.getName().toLowerCase(), meaning.getValue());
    }

    public synchronized Map getSpecialMeanings() {
        if (_specialMeanings == null) {
            _specialMeanings = new HashMap();
            if (isUsedefaultspecialmeanings()) {
                _specialMeanings.putAll(DEFAULT_SPECIAL_MEANINGS);
            }
        }
        return _specialMeanings;
    }

    public boolean isUsedefaultspecialmeanings() {
        return _usedefaultspecialmeanings;
    }

    public void setUsedefaultspecialmeanings(boolean usedefaultspecialmeanings) {
        _usedefaultspecialmeanings = usedefaultspecialmeanings;
    }
}
