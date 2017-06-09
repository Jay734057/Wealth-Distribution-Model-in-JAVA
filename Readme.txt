----------------------------------------------------------------------

Wealth Distribution Model in JAVA

Authors: Jianyu Zhu 	734057 
	 Jianan Hu 	748138
----------------------------------------------------------------------

CONTENTS
1. List of Files
2. Running the JAVA Model
3. Microsoft Excel Compatibility

======================================================================
1. List of Files

  Program files:
    WealthDistribution folder - The whole JAVA project file  
    WealthDistribution for CMD folder - Command execution version for the model
  Documentation:
    Readme.txt - This document
    Readme.html - An HTML version of this document.

======================================================================
2. Running the JAVA Model
  I.Running in eclipse: a.Import the whole JAVA project file folder to the eclipse platform                             b.Set up the run configurations
                        c.Click the run button.

     * run configuration(9 numbers + 1 string): numOfPeople, maxVision, maxMetabolism, 				minLifespan, maxLifespan, bestLand, growInterval, growAmount, numOfTurns,
		exportFileName

  II.Running in command line: Execute the following command from the WealthDistribution for                                CMD folder
                                 
		java -cp jxl.jar; RunModel 250 5 15 1 83 10 1 4 500 exportFileName

     * number representation(9 numbers + 1 string): numOfPeople, maxVision, maxMetabolism, 			minLifespan, maxLifespan, bestLand, growInterval, growAmount, numOfTurns,
		exportFileName  
  
  III. Running by using *.bat file in the WealthDistribution for CMD folder: double Clike on 			the Exp.1 .bat - Exp.11 .bat to run the 11 experiments mentioned in the report. 		The results for experiments are exported to the same dictionary automatically
      
======================================================================
3. Export File

  An Excel form file contains:
     a.Raw data for wealth each turn
     b.Statistic for rich, medium and poor for each turn
     c.Gini index for each turn
     d.Lorenz-points for each turn

======================================================================
4. Microsoft Excel Compatibility

  While jxl.jar has a high degree of compatibility with Microsoft Excel, there are a few          differences between the products.

  jxl.jar only supports  Microsoft Excel97 and above.

  jxl.jar currently does not support Macros(can keep it from template).
