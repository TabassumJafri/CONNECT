/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.hhs.fha.nhinc.fta;

import javax.ejb.Stateless;
import javax.jws.WebService;

/**
 *
 * @author svalluripalli
 */
@WebService(serviceName = "AdapterNotificationConsumerSecured", portName = "AdapterNotificationConsumerPortSecureType", endpointInterface = "gov.hhs.fha.nhinc.adapternotificationconsumersecured.AdapterNotificationConsumerPortSecureType", targetNamespace = "urn:gov:hhs:fha:nhinc:adapternotificationconsumersecured", wsdlLocation = "META-INF/wsdl/AdapterFTAServiceSecured/AdapterNotificationConsumerSecured.wsdl")
@Stateless
public class AdapterFTAServiceSecured {

    public gov.hhs.fha.nhinc.common.nhinccommon.AcknowledgementType notifySubscribersOfDocument(gov.hhs.fha.nhinc.common.nhinccommonadapter.NotifySubscribersOfDocumentRequestSecuredType notifySubscribersOfDocumentRequestSecured) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public gov.hhs.fha.nhinc.common.nhinccommon.AcknowledgementType notify(org.oasis_open.docs.wsn.b_2.Notify notifyRequestSecured) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

}
