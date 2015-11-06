/*
 * Copyright (c) 2009-2015, United States Government, as represented by the Secretary of Health and Human Services.
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
package gov.hhs.fha.nhinc.corex12.docsubmission.genericbatch.response.outbound;

import gov.hhs.fha.nhinc.audit.ejb.AuditEJBLogger;
import gov.hhs.fha.nhinc.audit.ejb.impl.AuditEJBLoggerImpl;
import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.common.nhinccommon.HomeCommunityType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetCommunitiesType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetCommunityType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetSystemType;
import gov.hhs.fha.nhinc.common.nhinccommon.UrlInfoType;
import gov.hhs.fha.nhinc.connectmgr.NhinEndpointManager;
import gov.hhs.fha.nhinc.corex12.docsubmission.audit.CORE_X12BatchSubmissionAuditLogger;
import gov.hhs.fha.nhinc.corex12.docsubmission.genericbatch.response.entity.OutboundCORE_X12DSGenericBatchResponseDelegate;
import gov.hhs.fha.nhinc.corex12.docsubmission.genericbatch.response.entity.OutboundCORE_X12DSGenericBatchResponseOrchestratable;
import gov.hhs.fha.nhinc.corex12.docsubmission.genericbatch.response.entity.OutboundCORE_X12DSGenericBatchResponseOrchestrationContextBuilder_g0;
import gov.hhs.fha.nhinc.corex12.docsubmission.genericbatch.response.entity.OutboundCORE_X12DSGenericBatchResponseStrategyImpl_g0;
import gov.hhs.fha.nhinc.corex12.docsubmission.genericbatch.response.nhin.proxy.NhinCORE_X12DGenericBatchResponseProxyObjectFactory;
import gov.hhs.fha.nhinc.corex12.docsubmission.genericbatch.response.nhin.proxy.NhinCORE_X12DSGenericBatchResponseProxy;
import gov.hhs.fha.nhinc.corex12.docsubmission.genericbatch.response.nhin.proxy.NhinCORE_X12DSGenericBatchResponseProxyWebServiceSecuredImpl;
import gov.hhs.fha.nhinc.corex12.docsubmission.genericbatch.response.orchestration.OrchestrationContextFactory;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.orchestration.Orchestratable;
import gov.hhs.fha.nhinc.orchestration.OrchestrationContext;
import gov.hhs.fha.nhinc.orchestration.OrchestrationContextBuilder;
import gov.hhs.fha.nhinc.orchestration.OutboundDelegate;
import java.util.Properties;
import org.caqh.soap.wsdl.corerule2_2_0.COREEnvelopeBatchSubmission;
import org.caqh.soap.wsdl.corerule2_2_0.COREEnvelopeBatchSubmissionResponse;
import org.junit.Test;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author tjafri
 */
public class PassthroughOutboundCORE_X12DSGenericBatchResponseTest {

    private final AuditEJBLoggerImpl mockEJBLogger = mock(AuditEJBLoggerImpl.class);
    OutboundCORE_X12DSGenericBatchResponseOrchestratable mockOrchestratable
        = mock(OutboundCORE_X12DSGenericBatchResponseOrchestratable.class);
    OutboundCORE_X12DSGenericBatchResponseDelegate mockDelegate = mock(OutboundCORE_X12DSGenericBatchResponseDelegate.class);

    @Test
    public void auditLoggingOnForInboundX12BatchResponse() {
        COREEnvelopeBatchSubmission request = new COREEnvelopeBatchSubmission();
        AssertionType assertion = new AssertionType();
        NhinTargetCommunitiesType targets = new NhinTargetCommunitiesType();
        NhinTargetSystemType target = createNhinTarget();
        UrlInfoType urlInfo = new UrlInfoType();
        final OutboundCORE_X12DSGenericBatchResponseOrchestratable orch
            = createOutboundBatchResponse(mockDelegate, request, target, assertion);
        final CORE_X12BatchSubmissionAuditLogger auditLogger = getAuditLogger(true);

        OutboundCORE_X12DSGenericBatchResponseOrchestrationContextBuilder_g0 contextBuilder
            = new OutboundCORE_X12DSGenericBatchResponseOrchestrationContextBuilder_g0();
        COREEnvelopeBatchSubmissionResponse successResponse = new COREEnvelopeBatchSubmissionResponse();
        PassthroughOutboundCORE_X12DSGenericBatchResponse batchResp
            = new PassthroughOutboundCORE_X12DSGenericBatchResponse() {
            @Override
            protected CORE_X12BatchSubmissionAuditLogger getAuditLogger() {
                return auditLogger;
            }

            @Override
            protected OutboundCORE_X12DSGenericBatchResponseDelegate getDelegate() {
                return mockDelegate;
            }

            @Override
            protected OutboundCORE_X12DSGenericBatchResponseOrchestratable createOrchestratable(
                OutboundCORE_X12DSGenericBatchResponseDelegate delegate, COREEnvelopeBatchSubmission request,
                NhinTargetSystemType targetSystem, AssertionType assertion) {
                return mockOrchestratable;
            }
        };
        when(mockDelegate.process(any(Orchestratable.class))).thenReturn(mockOrchestratable);
        when(mockOrchestratable.getResponse()).thenReturn(successResponse);

        batchResp.batchSubmitTransaction(request, assertion, targets, urlInfo);
    }

    private CORE_X12BatchSubmissionAuditLogger getAuditLogger(final boolean isLoggingOn) {
        return new CORE_X12BatchSubmissionAuditLogger() {
            @Override
            protected AuditEJBLogger getAuditLogger() {
                return mockEJBLogger;
            }

            @Override
            protected boolean isAuditLoggingOn(String serviceName) {
                return isLoggingOn;
            }
        };
    }

    private NhinTargetCommunitiesType createNhinTargetCommunities() {
        NhinTargetCommunitiesType targets = new NhinTargetCommunitiesType();
        NhinTargetCommunityType target = new NhinTargetCommunityType();
        HomeCommunityType home = new HomeCommunityType();
        home.setHomeCommunityId("1.1");
        target.setHomeCommunity(home);
        targets.getNhinTargetCommunity().add(target);
        return targets;
    }

    private NhinTargetSystemType createNhinTarget() {
        NhinTargetSystemType target = new NhinTargetSystemType();
        HomeCommunityType home = new HomeCommunityType();
        home.setHomeCommunityId("1.1");
        target.setHomeCommunity(home);
        return target;
    }

    private OutboundCORE_X12DSGenericBatchResponseOrchestratable createOutboundBatchResponse(OutboundDelegate delegate,
        COREEnvelopeBatchSubmission request, NhinTargetSystemType target, AssertionType assertion) {
        OutboundCORE_X12DSGenericBatchResponseOrchestratable outboundBatchResponse
            = new OutboundCORE_X12DSGenericBatchResponseOrchestratable(delegate, request, target, assertion);
        //COREEnvelopeBatchSubmissionResponse coreResponse = new COREEnvelopeBatchSubmissionResponse();
        //outboundBatchResponse.setResponse(coreResponse);
        return outboundBatchResponse;
    }
}
