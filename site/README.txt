
In this part of the svn tree is maintained the website published at http://ant.apache.org

The website is composed of three part:
 * the Ant main site published at http://ant.apache.org
 * the Ivy site published at http://ant.apache.org/ivy
 * the IvyDE site published at http://ant.apache.org/ivy/ivyde

Each website part has a 'sources' folder which contain the sources used to generate the final html files. The generated files are placed into the 'production' folder.

To generate the site for Ant:
$ cd site/ant
$ ant docs

To generate the site for Ivy or IvyDE:
$ cd site/ivy(de)
$ ant generate-site
To force the generation of the entire website (usefull when we change the template)
$ ant /all generate-site
To generate a 'history' sub part of the website (here tunk):
$ ant generate-history -Dhistory.version=trunk

Once files are generated in the 'production' folder, commit them and they will go live in few seconds.
