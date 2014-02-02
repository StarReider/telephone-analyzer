package org.robe.ta.conf;

import java.io.File;
import java.util.jar.JarInputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigurationReader
{
    private static final String CONFIG_FILE_NAME = "main_config.xml";
    private static final String PORT_TAG = "db.port";
    private static final String SERVER_TAG = "db.server";
    private static final String PATH_TAG = "db.path";
    private static final String VERSION_TAG = "version";
    private static final String THIRD_TAB_TAG = "ui.third_tab";

    private final Log log;

    private String path;
    private String server;
    private Integer port;
    private String version;
    private RunMode mode;
    private boolean third_tab_on;

    public ConfigurationReader() throws Exception
    {
        log = LogFactory.getLog(ConfigurationReader.class);

        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        builder.setURL(this.getClass().getClassLoader().getResource("META-INF/main_config.xml"));
        Configuration conf = builder.getConfiguration(true);

        path = conf.getString(PATH_TAG);
        version = conf.getString(VERSION_TAG);

        try
        {
            port = conf.getInt(PORT_TAG);
        } 
        catch (Exception e)
        {
            log.info(e);
        }

        try
        {
            server = conf.getString(SERVER_TAG);
        } 
        catch (Exception e)
        {
            log.info(e);
        }

        try
        {
            third_tab_on = conf.getBoolean(THIRD_TAB_TAG);
        } 
        catch (Exception e)
        {
            log.info(e);
        }

        verifyInput();

        mode = whatRumMode();

        if (mode == RunMode.ClientServer)
        {
            XMLConfiguration configuration = new XMLConfiguration(this.getClass()
                    .getResource("/META-INF/persistence.xml"));
            configuration.setExpressionEngine(new XPathExpressionEngine());
            configuration
                    .clearProperty("persistence-unit/properties/property[@name='javax.persistence.jdbc.url']/@value");
            configuration.addProperty(
                    "persistence-unit/properties/property[@name='javax.persistence.jdbc.url']/@value", getJDBCURL());
            configuration.save();
        }

        log.info("Mode is " + mode);
    }

    private void verifyInput()
    {
        if (StringUtils.isEmpty(path))
            throw new RuntimeException("Path to bd must be filled. See META-INF/main_config file.");

        if (StringUtils.isEmpty(version))
            throw new RuntimeException("Install is not correct.");

        if ((StringUtils.isEmpty(server) && port != null) || (port == null && !StringUtils.isEmpty(server)))
            throw new RuntimeException("Port/server must be filled. See META-INF/main_config file.");
    }

    private RunMode whatRumMode()
    {
        if (!StringUtils.isEmpty(server) && port != null)
            return RunMode.ClientServer;
        else
            return RunMode.StandAlone;
    }

    public int getPort()
    {
        return port;
    }

    public String getPath()
    {
        return path;
    }

    public String getServer()
    {
        return server;
    }

    public String getVersion()
    {
        return version;
    }

    public RunMode getMode()
    {
        return mode;
    }

    public boolean isThird_tab_on()
    {
        return third_tab_on;
    }

    public String getJDBCURL()
    {
        if (mode == RunMode.StandAlone)
            return "jdbc:derby:" + path;
        else
            return "jdbc:derby:" + "//" + server + ":" + port + "/" + path;
    }
}