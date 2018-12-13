/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.fha.nhinc.audit.ejb.impl;

import gov.hhs.fha.nhinc.audit.AuditMessageLogger;
import gov.hhs.fha.nhinc.auditrepository.nhinc.proxy.AuditRepositoryProxyObjectFactory;
import gov.hhs.fha.nhinc.common.auditlog.LogEventRequestType;
import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tjafri
 */
@Stateless
@TransactionManagement(value = TransactionManagementType.CONTAINER)
public class AuditMessageLoggerImpl implements AuditMessageLogger {

    private static final Logger LOG = LoggerFactory.getLogger(AuditMessageLoggerImpl.class);

    @Asynchronous
    @Override
    public void auditLogMessages(LogEventRequestType auditLogMsg, AssertionType assertion) {
        if (auditLogMsg == null || auditLogMsg.getAuditMessage() == null) {
            LOG.error("auditLogMsg is null, no auditing will take place.");
        } else {
            new AuditRepositoryProxyObjectFactory().getAuditRepositoryProxy().auditLog(auditLogMsg, assertion);
        }
    }
}
