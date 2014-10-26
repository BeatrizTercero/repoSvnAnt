xooki.util.mix({debug:true, 
	jira: {ids: ['IVY'], url: 'http://issues.apache.org/jira'}, 
	shortcuts: {
		ant: {pre: xooki.c.relativeRoot+'history/latest-milestone/use/', post:'.html'},
		doc: {pre: xooki.c.relativeRoot+'history/latest-milestone/', post:'.html'}
	}
}, xooki.c, false);
