# Cobertura

This repository aims to be a contribution to the Cobertura project.

Our objectives

* tests should run once. Due to original Cobertura design, users were forced to run instrumentation and tests immediately, and later run tests again to see if something was broken.
* expose more data. Ex.: expose coverage thresholds and coverage data on reports, allow to build custom metrics. Serialize data in a readable format.
* decouple from Ant and provide a DSL to set parameters, run code instrumentation and provide programatic access to results
* provide means to add new metrics
* provide means to support multiple JVM languages

Do we break compatibility?

Original xml and html reports were not removed and old ant tasks can still be used.
From the DSL we use another xml report format, and inclusion/exclusion ant-like patterns (ex.: "**/A.class") are not properly handled.

## Quick example

	Arguments args =
        	         new Arguments().setBaseDirectory(origClassesDir.getAbsolutePath())
				.setDataFile("cobertura.ser")
				.setDestinationFile(intrumentedCodeDestDir.getAbsolutePath())
				.setEncoding("UTF-8");
	Cobertura cobertura = new Cobertura(args).instrumentCode();
	//run tests
	cobertura.report().export(new XmlReportFormatStrategy());


## Overview

When using Cobertura, we
* *compile code* to get .class files;
* *instrument code*; touch the bytecode, so that each accessed line is reported;
* *run tests* using instrumented bytecode which will hit ProjectData to store coverage data;
* *aggregate collected data* according to the JVM language it belongs to, since a source file may be translated to multiple .class files at non-Java JVM languages. We use an IReportBuilderStrategy implementations for this. Aggregated data is presented in a GenericReport object.
* *expose aggregated data* from a GenericReport in different formats by implementing an IReportFormatStrategy interface.

Compile the code and run tests are requirements, not Cobertura's resposibility.

New metrics can be defined implementing ICustomMetric interface.

## Status

* we benefit from git and Maven tools.
* a DSL was created. Arguments can be set and results can accessed programatically. 
* code instrumentation, tests and report building phases were decoupled. User is no longer forced to run tests twice. Just needs to run code instrumentation, then run the tests, and build coverage reports after that.
* project data information can be aggregated to build reports for other JVM languages
* an interface (ICustomMetric) for custom metrics was created. Implementations are loaded by reflection and automatically display on reports.
* xml and threshold violations report strategies were implemented.

## Todos

* review parameters Cobertura accepts. See how to deal with classes inclusion/exclusion when receiving Ant like patterns (ex.: "**/A.class")
* create and test html report strategy. Refactor html report strategy using a library like http://code.google.com/p/jatl/
* allow to assign a name to a project (currently a hash is calculated, based on project files)
* create a Maven plugin
* make Jenkins plugin leverage this code
* see how to handle war/jar/ear files instrumentation
* create IReportBuilderStrategies for other JVM languages

Contributions are welcome!


# Copyright

See Cobertura ["COPYRIGHT"](https://github.com/code54/cobertura-chocolate/blob/master/cobertura/COPYRIGHT) file


# License

Cobertura is free software.  Most of it is licensed under the GNU
GPL, and you can redistribute it and/or modify it under the terms
of the GNU General Public License as published by the Free Software
Foundation; either version 2 of the License, or (at your option)
any later version.  Please review the file COPYING included in this
distribution for further details.

Parts of Cobertura are licensed under the Apache Software License,
Version 1.1.


# Warranty

Cobertura is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.
