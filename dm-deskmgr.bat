SET PATH=%JAVA_HOME%\bin
set CLASSPATH=./build/classes;./mylibs/commons-logging-1.2.jar;./mylibs/gson-2.6.2.jar;./mylibs/httpclient-4.5.3.jar;./mylibs/httpcore-4.4.6.jar;./mylibs/json-20170516.jar;./mylibs/log4j.jar
start javaw com.shuishou.deskmgr.ui.MainFrame -Xms256m -Xmx256m

