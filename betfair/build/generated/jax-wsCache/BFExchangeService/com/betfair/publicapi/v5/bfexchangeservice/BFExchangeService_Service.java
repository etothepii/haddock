
package com.betfair.publicapi.v5.bfexchangeservice;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2-hudson-752-
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "BFExchangeService", targetNamespace = "http://www.betfair.com/publicapi/v5/BFExchangeService/", wsdlLocation = "https://api.betfair.com/exchange/v5/BFExchangeService.wsdl")
public class BFExchangeService_Service
    extends Service
{

    private final static URL BFEXCHANGESERVICE_WSDL_LOCATION;
    private final static WebServiceException BFEXCHANGESERVICE_EXCEPTION;
    private final static QName BFEXCHANGESERVICE_QNAME = new QName("http://www.betfair.com/publicapi/v5/BFExchangeService/", "BFExchangeService");

    static {
        BFEXCHANGESERVICE_WSDL_LOCATION = com.betfair.publicapi.v5.bfexchangeservice.BFExchangeService_Service.class.getResource("https://api.betfair.com/exchange/v5/BFExchangeService.wsdl");
        WebServiceException e = null;
        if (BFEXCHANGESERVICE_WSDL_LOCATION == null) {
            e = new WebServiceException("Cannot find 'https://api.betfair.com/exchange/v5/BFExchangeService.wsdl' wsdl. Place the resource correctly in the classpath.");
        }
        BFEXCHANGESERVICE_EXCEPTION = e;
    }

    public BFExchangeService_Service() {
        super(__getWsdlLocation(), BFEXCHANGESERVICE_QNAME);
    }

    public BFExchangeService_Service(WebServiceFeature... features) {
        super(__getWsdlLocation(), BFEXCHANGESERVICE_QNAME, features);
    }

    public BFExchangeService_Service(URL wsdlLocation) {
        super(wsdlLocation, BFEXCHANGESERVICE_QNAME);
    }

    public BFExchangeService_Service(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, BFEXCHANGESERVICE_QNAME, features);
    }

    public BFExchangeService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public BFExchangeService_Service(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns BFExchangeService
     */
    @WebEndpoint(name = "BFExchangeService")
    public BFExchangeService getBFExchangeService() {
        return super.getPort(new QName("http://www.betfair.com/publicapi/v5/BFExchangeService/", "BFExchangeService"), BFExchangeService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns BFExchangeService
     */
    @WebEndpoint(name = "BFExchangeService")
    public BFExchangeService getBFExchangeService(WebServiceFeature... features) {
        return super.getPort(new QName("http://www.betfair.com/publicapi/v5/BFExchangeService/", "BFExchangeService"), BFExchangeService.class, features);
    }

    private static URL __getWsdlLocation() {
        if (BFEXCHANGESERVICE_EXCEPTION!= null) {
            throw BFEXCHANGESERVICE_EXCEPTION;
        }
        return BFEXCHANGESERVICE_WSDL_LOCATION;
    }

}
