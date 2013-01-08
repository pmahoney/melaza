Melaza
======

Java profiler with JRuby extensions.

**This is a work in progress and unlikely to be usuable, unfortunately.**

Melaza was forked from [JIP](http://jiprof.sourceforge.net/).  Major
changes from the original JIP include:

* build converted to use [Maven](https://maven.apache.org/)
* bytecode library upgraded to [ASM 4](http://asm.ow2.org/)
* code split into core, agent, and console packages

Building
--------

    $ mvn package

Profiling
---------

    $ melaza_root=/path/to/melaza
    $ version=1.0.0-SNAPSHOT
    $ agent=${melaza_root}/melaza-agent/target/melaza-agent-${version}.jar
    $ alias melaza-console="java -jar ${melaza_root}/melaza-console/target/melaza-console-${version}.jar"
    $ jruby -J-javaagent:${agent} prog.rb
    $ melaza-console profile.xml

TODO
----

* Finish migrating config engine to own class
* Add tests of default instrumentation
* Add tests of JRuby instrumentation
* Improve logging configuration
* Improve configuration documentation
* Convert call tree in GUI to use a tree table
* Or, create HTML GUI instead
* Investigate state of interactive control
* Allow merging of thread pool roots, manually or by detection
* Lots more
