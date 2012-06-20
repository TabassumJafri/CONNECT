/*
 * Copyright (c) 2012, United States Government, as represented by the Secretary of Health and Human Services. 
 * All rights reserved. 
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met: 
 *     * Redistributions of source code must retain the above 
 *       copyright notice, this list of conditions and the following disclaimer. 
 *     * Redistributions in binary form must reproduce the above copyright 
 *       notice, this list of conditions and the following disclaimer in the documentation 
 *       and/or other materials provided with the distribution. 
 *     * Neither the name of the United States Government nor the 
 *       names of its contributors may be used to endorse or promote products 
 *       derived from this software without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY 
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package gov.hhs.fha.nhinc.docquery.entity.deferred.response;

import gov.hhs.fha.nhinc.common.nhinccommon.AcknowledgementType;
import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.common.nhinccommon.HomeCommunityType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetCommunitiesType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetSystemType;
import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerCache;
import gov.hhs.fha.nhinc.connectmgr.UrlInfo;
import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerException;
import gov.hhs.fha.nhinc.docquery.DocQueryAuditLog;
import gov.hhs.fha.nhinc.docquery.DocQueryPolicyChecker;
import gov.hhs.fha.nhinc.docquery.passthru.deferred.response.proxy.PassthruDocQueryDeferredResponseProxy;
import gov.hhs.fha.nhinc.docquery.passthru.deferred.response.proxy.PassthruDocQueryDeferredResponseProxyObjectFactory;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import gov.hhs.fha.nhinc.perfrepo.PerformanceManager;
import gov.hhs.fha.nhinc.util.HomeCommunityMap;
import gov.hhs.healthit.nhin.DocQueryAcknowledgementType;
import java.sql.Timestamp;
import java.util.List;

import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author jhoppesc
 */
public class EntityDocQueryDeferredResponseOrchImpl {

    private static final Log log = LogFactory.getLog(EntityDocQueryDeferredResponseOrchImpl.class);

    public DocQueryAcknowledgementType respondingGatewayCrossGatewayQuery(AdhocQueryResponse msg,
            AssertionType assertion, NhinTargetCommunitiesType targets) {
        DocQueryAcknowledgementType respAck = new DocQueryAcknowledgementType();
        RegistryResponseType regResp = new RegistryResponseType();
        respAck.setMessage(regResp);
        String responseCommunityId = HomeCommunityMap.getCommunityIdFromTargetCommunities(targets);
        // Audit the incoming Entity Message
        AcknowledgementType ack = auditResponse(msg, assertion, NhincConstants.AUDIT_LOG_INBOUND_DIRECTION,
                NhincConstants.AUDIT_LOG_ENTITY_INTERFACE, responseCommunityId);

        try {
            List<UrlInfo> urlInfoList = getEndpoints(targets);

            if (urlInfoList != null && NullChecker.isNotNullish(urlInfoList) && urlInfoList.get(0) != null
                    && NullChecker.isNotNullish(urlInfoList.get(0).getHcid())
                    && NullChecker.isNotNullish(urlInfoList.get(0).getUrl())) {
                // Log the start of the performance record
                Timestamp starttime = new Timestamp(System.currentTimeMillis());
                Long logId = PerformanceManager.getPerformanceManagerInstance().logPerformanceStart(starttime,
                        "Deferred" + NhincConstants.DOC_QUERY_SERVICE_NAME, NhincConstants.AUDIT_LOG_ENTITY_INTERFACE,
                        NhincConstants.AUDIT_LOG_INBOUND_DIRECTION, responseCommunityId);

                HomeCommunityType targetHcid = new HomeCommunityType();
                targetHcid.setHomeCommunityId(urlInfoList.get(0).getHcid());

                if (isPolicyValid(msg, assertion, targetHcid)) {
                    NhinTargetSystemType target = new NhinTargetSystemType();
                    target.setUrl(urlInfoList.get(0).getUrl());

                    PassthruDocQueryDeferredResponseProxyObjectFactory factory = new PassthruDocQueryDeferredResponseProxyObjectFactory();
                    PassthruDocQueryDeferredResponseProxy proxy = factory.getPassthruDocQueryDeferredResponseProxy();
                    respAck = proxy.respondingGatewayCrossGatewayQuery(msg, assertion, target);
                } else {
                    log.error("Outgoing Policy Check Failed");
                    regResp.setStatus(NhincConstants.DOC_QUERY_DEFERRED_RESP_ACK_FAILURE_STATUS_MSG);
                }

                // Log the end of the performance record
                Timestamp stoptime = new Timestamp(System.currentTimeMillis());
                PerformanceManager.getPerformanceManagerInstance().logPerformanceStop(logId, starttime, stoptime);
            } else {
                log.error("Failed to obtain target URL from connection manager");
                regResp.setStatus(NhincConstants.DOC_QUERY_DEFERRED_RESP_ACK_FAILURE_STATUS_MSG);
            }
        } catch (Exception ex) {
            log.error(ex);
            regResp.setStatus(NhincConstants.DOC_QUERY_DEFERRED_RESP_ACK_FAILURE_STATUS_MSG);
        }

        // Audit the outgoing Entity Message
        ack = auditAck(respAck, assertion, NhincConstants.AUDIT_LOG_OUTBOUND_DIRECTION,
                NhincConstants.AUDIT_LOG_ENTITY_INTERFACE);

        return respAck;
    }

    private AcknowledgementType auditResponse(AdhocQueryResponse msg, AssertionType assertion, String direction,
            String _interface, String responseCommunityId) {
        DocQueryAuditLog auditLogger = new DocQueryAuditLog();
        AcknowledgementType ack = auditLogger.auditDQResponse(msg, assertion, direction, _interface,
                responseCommunityId);

        return ack;
    }

    private AcknowledgementType auditAck(DocQueryAcknowledgementType msg, AssertionType assertion, String direction,
            String _interface) {
        DocQueryAuditLog auditLogger = new DocQueryAuditLog();
        AcknowledgementType ack = auditLogger.logDocQueryAck(msg, assertion, direction, _interface);

        return ack;
    }

    private boolean isPolicyValid(AdhocQueryResponse message, AssertionType assertion, HomeCommunityType target) {
        boolean policyIsValid = new DocQueryPolicyChecker().checkOutgoingResponsePolicy(message, assertion, target);

        return policyIsValid;
    }

    private List<UrlInfo> getEndpoints(NhinTargetCommunitiesType targetCommunities) {
        List<UrlInfo> urlInfoList = null;

        try {
            urlInfoList = ConnectionManagerCache.getInstance().getEndpointURLFromNhinTargetCommunities(
                    targetCommunities, NhincConstants.NHIN_DOCUMENT_QUERY_DEFERRED_RESP_SERVICE_NAME);
        } catch (ConnectionManagerException ex) {
            log.error("Failed to obtain target URLs", ex);
        }

        return urlInfoList;
    }
}