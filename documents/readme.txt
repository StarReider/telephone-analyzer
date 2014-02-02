1)Install JDK7 if it's needed(http://www.oracle.com/technetwork/java/javase/downloads/index.html)
2)Replace "C:/Program Files/Java/jdk1.7.0_04/db/lib/derby.jar" by your path to DERBY jar in run.bat file(it will be {JDK_PATH}/db/lib/derby.jar)
3)Open windows shell and navigate to application root directory (cd {path_to_unzipped_archive/TelephoneAnalyzer}) and run run.bat

If updating case, remove all files/folders from app root folder except folder "exampledb" (it's db) and place all files/folders from archive (which are under root folder) into app root folder.