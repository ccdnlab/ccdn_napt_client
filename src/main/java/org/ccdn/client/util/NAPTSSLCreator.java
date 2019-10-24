package org.ccdn.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang.StringUtils;
import org.ccdn.client.constant.NAPTConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @Class Name : NAPTSSLCreator 
* @Description: CCDN NAPT SSL context creator 
 * @Author : CCDN
 * @Version : CCDN NAPT V1.1
*/
public class NAPTSSLCreator {
	private static Logger m_log = LoggerFactory.getLogger(NAPTSSLCreator.class);

    private NAPTConfig m_config = NAPTConfig.getConfig();

    private SSLContext m_sslCtx = null;

    private String m_jksPath = null;

    private static NAPTSSLCreator m_creator = null;

    public static NAPTSSLCreator getCreator()
    {
        if (null == m_creator)
        {
        	m_creator = new NAPTSSLCreator();
        }
        return m_creator;
    }

    public static NAPTSSLCreator getCreator(String jksPath)
    {
        if (null == m_creator)
        {
        	m_creator = new NAPTSSLCreator(jksPath);
        }
        return m_creator;
    }
    
    public NAPTSSLCreator()
    {
        this.m_sslCtx = this.init();
    }

    public NAPTSSLCreator(String jksPath)
    {
        this.m_jksPath = jksPath;
        this.m_sslCtx = this.init();
    }

    public SSLContext getSSLContext()
    {
        return this.m_sslCtx;
    }
    
    private InputStream loadJks(String jksPath) throws FileNotFoundException
    {
        ClassLoader loader = NAPTSSLCreator.class.getClassLoader();
        URL ju = loader.getResource(jksPath);
        if (null != ju)
        {
        	m_log.info("Starting with jks file: {}.", jksPath);
            return loader.getResourceAsStream(jksPath);
        }

        m_log.warn("No keystore has been found in the bundled resources. Scanning filesystem...");
        File jf = new File(jksPath);
        if (jf.exists())
        {
        	m_log.info("Loading external keystore. jks file: {}.", jksPath);
            return new FileInputStream(jf);
        }

        m_log.warn("The keystore file does not exist. jks file: {}.", jksPath);
        return null;
    }

    public SSLContext init()
    {
        String jks = m_config.strValue(NAPTConst.CCDN_NAPT_SSL_JKS, NAPTConst.CCDN_NAPT_SSL_JKS_DEFAULT);
        if (StringUtils.isNotBlank(this.m_jksPath))
        {
            jks = this.m_jksPath;
        }

        // If the jks is existed, then key store and key manager password is required
        final String sslPaswd = m_config.strValue(NAPTConst.CCDN_NAPT_SSL_PASSWD, NAPTConst.CCDN_NAPT_SSL_PASSWD_DEFAULT);

        try
        {
        	m_log.info("Loading keystore. Keystore path: {}.", jks);
            final KeyStore ks = KeyStore.getInstance(NAPTConst.JKS);
            ks.load(this.loadJks(jks), sslPaswd.toCharArray());

            m_log.info("Initializing key manager...");
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, sslPaswd.toCharArray());

            // A trust manager needs to be added to the server context.
            // Use key store as trust store, as server needs to trust
            // Certificates signed by the server certificates
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            // Initialize SSl context
            m_log.info("Initializing SSL context...");
            SSLContext sslCtx = SSLContext.getInstance(NAPTConst.TLS);
            sslCtx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            m_log.info("The SSL context has been initialized successfully.");

            return sslCtx;
        }
        catch(Exception e)
        {
        	m_log.error("Unable to initialize SSL context. Cause: {}, error message: {}.", e.getCause(), e.getMessage());
            return null;
        }
    }
}
