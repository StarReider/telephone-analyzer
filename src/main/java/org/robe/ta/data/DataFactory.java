package org.robe.ta.data;

import org.robe.ta.conf.RunMode;
import org.robe.ta.data.jdbc.JDBCModule;
import org.robe.ta.data.jpa.JPAModule;

public class DataFactory 
{
        private static DataFactory instance;
        //private static final String JDBC_MODULE_NAME = "org.robe.ta.jdbc.JDBCModule";
        //private static final String JPA_MODULE_NAME = "org.robe.ta.jdbc.JPAModulw";
        
        private DataFactory() {}
        
        public static DataFactory getInstance()
        {
                if(instance == null)
                {
                        instance = new DataFactory();
                }
                return instance;
        }
        
        public DataProvider getDataFacade(RunMode runMode, String jdbcURL) throws Exception
        {
                if(runMode == RunMode.StandAlone)
                {
//                      Class<?> clazz = Class.forName(JDBC_MODULE_NAME);
//                      Constructor<?> constructor = clazz.getConstructor(String.class);
//                      DataFacade module = (DataFacade)constructor.newInstance(jdbcURL);
                        return new JDBCModule(jdbcURL);
                }
                else
                {
//                      Class<?> clazz = Class.forName(JPA_MODULE_NAME);
//                      Constructor<?> constructor = clazz.getConstructor(String.class);
//                      DataFacade module = (DataFacade)constructor.newInstance(jdbcURL);
                        return new JPAModule(jdbcURL);
                }
        }
}