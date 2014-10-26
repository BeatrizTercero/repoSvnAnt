xooki.util.mix({debug:true, 
	jira: {ids: ['IVY'], url: 'http://issues.apache.org/jira'}, 
	shortcuts: {
		gitdir: {pre: 'https://git-wip-us.apache.org/repos/asf?p=ant-ivy.git;a=tree;f='},
		gitfile: {pre: 'https://git-wip-us.apache.org/repos/asf?p=ant-ivy.git;a=blob;f='},
		ant: {pre: xooki.c.relativeRoot+'history/latest-milestone/use/', post:'.html'},
		doc: {pre: xooki.c.relativeRoot+'history/latest-milestone/', post:'.html'}
	}
}, xooki.c, false);
