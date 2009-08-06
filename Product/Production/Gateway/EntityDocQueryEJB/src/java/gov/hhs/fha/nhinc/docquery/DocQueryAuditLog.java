/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.fha.nhinc.docquery;

import gov.hhs.fha.nhinc.auditrepository.AuditRepositoryLogger;
import gov.hhs.fha.nhinc.auditrepository.proxy.AuditRepositoryProxy;
import gov.hhs.fha.nhinc.auditrepository.proxy.AuditRepositoryProxyObjectFactory;
import gov.hhs.fha.nhinc.common.auditlog.AdhocQueryMessageType;
import gov.hhs.fha.nhinc.common.auditlog.AdhocQueryResponseMessageType;
import gov.hhs.fha.nhinc.common.auditlog.LogEventRequestType;
import gov.hhs.fha.nhinc.common.nhinccommon.AcknowledgementType;
import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.common.nhinccommonentity.RespondingGatewayCrossGatewayQuerySecuredRequestType;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author jhoppesc
 */
public class DocQueryAuditLog {

    private static Log log = LogFactory.getLog(DocQueryAuditLog.class);

    /**
     * This method will log Audit Query Requests received on the Entity Interface
     *
     * @param auditMsg The Audit Query Request message to be audit logged.
     * @param assertion Assertion received
     * @return An acknowledgement of whether or not the message was successfully logged.
     */
    public AcknowledgementType audit(RespondingGatewayCrossGatewayQuerySecuredRequestType auditMsg, AssertionType assertion) {
        log.debug("Entering DocQueryAuditLog.audit (entity)...");

        AdhocQueryMessageType auditReqMsg = new AdhocQueryMessageType();
        auditReqMsg.setAssertion(assertion);
        auditReqMsg.setAdhocQueryRequest(auditMsg.getAdhocQueryRequest());

        AcknowledgementType ack = logDocQuery(auditReqMsg, NhincConstants.AUDIT_LOG_INBOUND_DIRECTION, NhincConstants.AUDIT_LOG_ENTITY_INTERFACE);

        log.debug("Exiting DocQueryAuditLog.audit (entity)...");
        return ack;
    }

    /**
     * This method will log Document Query Requests sent on the Nhin Interface
     *
     * @param auditMsg The Document Query Request message to be audit logged.
     * @return An acknowledgement of whether or not the message was successfully logged.
     */
    public AcknowledgementType audit(
            gov.hhs.fha.nhinc.common.nhinccommonproxy.RespondingGatewayCrossGatewayQueryRequestType auditMsg) {
        log.debug("Entering DocQueryAuditLog.audit (proxy)...");

        AdhocQueryMessageType auditReqMsg = new AdhocQueryMessageType();
        auditReqMsg.setAssertion(auditMsg.getAssertion());
        auditReqMsg.setAdhocQueryRequest(auditMsg.getAdhocQueryRequest());

        AcknowledgementType ack = logDocQuery(auditReqMsg, NhincConstants.AUDIT_LOG_OUTBOUND_DIRECTION, NhincConstants.AUDIT_LOG_NHIN_INTERFACE);

        log.debug("Exiting DocQueryAuditLog.audit (proxy)...");
        return ack;
    }

    /**
     * This method will log Document Query Requests received/sent on a particular public interface
     *
     * @param message The Document Query Request message to be audit logged.
     * @param direction  The direction this message is going (Inbound or Outbound)
     * @param _interface The interface this message is being received/sent on (Entity, Adapter, or Nhin)
     * @return An acknowledgement of whether or not the message was successfully logged.
     */
    private AcknowledgementType logDocQuery(AdhocQueryMessageType message, String direction, String _interface) {
        AcknowledgementType ack = new AcknowledgementType();
        AuditRepositoryLogger auditLogger = new AuditRepositoryLogger();
        LogEventRequestType auditLogMsg = auditLogger.logAdhocQuery(message, direction, _interface);

        if (auditLogMsg != null) {
            AuditRepositoryProxyObjectFactory auditRepoFactory = new AuditRepositoryProxyObjectFactory();
            AuditRepositoryProxy proxy = auditRepoFactory.getAuditRepositoryProxy();
            ack = proxy.auditLog(auditLogMsg);
        }
        return ack;
    }

    /**
     * This method will log Document Query Responses received/sent on a particular public interface
     *
     * @param message The Document Query Response message to be audit logged.
     * @param direction  The direction this message is going (Inbound or Outbound)
     * @param _interface The interface this message is being received/sent on (Entity, Adapter, or Nhin)
     * @return An acknowledgement of whether or not the message was successfully logged.
     */
    public AcknowledgementType auditResponse(AdhocQueryResponseMessageType message, String direction, String _interface) {
        AcknowledgementType ack = new AcknowledgementType();
        AuditRepositoryLogger auditLogger = new AuditRepositoryLogger();
        LogEventRequestType auditLogMsg = auditLogger.logAdhocQueryResult(message, direction, _interface);

        if (auditLogMsg != null) {
            AuditRepositoryProxyObjectFactory auditRepoFactory = new AuditRepositoryProxyObjectFactory();
            AuditRepositoryProxy proxy = auditRepoFactory.getAuditRepositoryProxy();
            ack = proxy.auditLog(auditLogMsg);
        }

        return ack;
    }
}
