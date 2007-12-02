xooki.util.mix({debug:true, 
	jira: {ids: ['IVY'], url: 'http://issues.apache.org/jira'}, 
	shortcuts: {
		svn: {pre: 'https://svn.apache.org/repos/asf/ant/ivy/core/trunk/'},
		ant: {pre: xooki.c.relativeRoot+'history/trunk/use/', post:'.html'},
		doc: {pre: xooki.c.relativeRoot+'history/trunk/', post:'.html'}
	}
}, xooki.c, false);
