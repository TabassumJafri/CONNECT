/*
 * Copyright (c) 2009-2019, United States Government, as represented by the Secretary of Health and Human Services.
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
package gov.hhs.fha.nhinc.docsubmission._20.entity.deferred.request;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetCommunitiesType;
import gov.hhs.fha.nhinc.common.nhinccommon.UrlInfoType;
import gov.hhs.fha.nhinc.common.nhinccommonentity.RespondingGatewayProvideAndRegisterDocumentSetSecuredRequestType;
import gov.hhs.fha.nhinc.docsubmission.DocSubmissionUtils;
import gov.hhs.fha.nhinc.docsubmission.outbound.deferred.request.OutboundDocSubmissionDeferredRequest;
import gov.hhs.fha.nhinc.event.error.ErrorEventException;
import gov.hhs.fha.nhinc.messaging.server.BaseService;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants.UDDI_SPEC_VERSION;
import gov.hhs.healthit.nhin.XDRAcknowledgementType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import javax.xml.ws.WebServiceContext;

/**
 *
 * @author Neil Webb
 */
public class EntityDocSubmissionDeferredRequestImpl_g1 extends BaseService {

    private OutboundDocSubmissionDeferredRequest outboundDocSubmissionRequest;
    EntityDocSubmissionDeferredRequestImpl_g1(OutboundDocSubmissionDeferredRequest outboundDocSubmissionRequest) {
        this.outboundDocSubmissionRequest = outboundDocSubmissionRequest;
    }

    public XDRAcknowledgementType provideAndRegisterDocumentSetBRequest(
            RespondingGatewayProvideAndRegisterDocumentSetSecuredRequestType provideAndRegisterRequestRequest,
            WebServiceContext context) {
        AssertionType assertion = getAssertion(context, null);

        return provideAndRegisterDocumentSetBAsyncRequest(
                provideAndRegisterRequestRequest.getProvideAndRegisterDocumentSetRequest(), assertion,
                provideAndRegisterRequestRequest.getNhinTargetCommunities(), provideAndRegisterRequestRequest.getUrl());
    }

    public gov.hhs.healthit.nhin.XDRAcknowledgementType provideAndRegisterDocumentSetBAsyncRequest(
            gov.hhs.fha.nhinc.common.nhinccommonentity.RespondingGatewayProvideAndRegisterDocumentSetRequestType provideAndRegisterAsyncReqRequest,
            WebServiceContext context) {
        AssertionType assertion = getAssertion(context, provideAndRegisterAsyncReqRequest.getAssertion());

        return provideAndRegisterDocumentSetBAsyncRequest(
                provideAndRegisterAsyncReqRequest.getProvideAndRegisterDocumentSetRequest(), assertion,
                provideAndRegisterAsyncReqRequest.getNhinTargetCommunities(),
                provideAndRegisterAsyncReqRequest.getUrl());

    }
    /**
     * Route service to either standard or passthrough depend on configuration
     * @param request Document Submission request
     * @param assertion DS assertion
     * @param targets DS gateway targets
     * @param urlInfo DS gateway target urls
     * @return XDRAcknowledgementType
     */
    private XDRAcknowledgementType provideAndRegisterDocumentSetBAsyncRequest(
            ProvideAndRegisterDocumentSetRequestType request, AssertionType assertion,
            NhinTargetCommunitiesType targets, UrlInfoType urlInfo) {
        XDRAcknowledgementType response = null;
        try {
            DocSubmissionUtils.getInstance().setTargetCommunitiesVersion(targets, UDDI_SPEC_VERSION.SPEC_2_0);
            response = outboundDocSubmissionRequest.provideAndRegisterDocumentSetBAsyncRequest(request, assertion,
                    targets, urlInfo);
        } catch (Exception e) {
            throw new ErrorEventException(e,"Unable to call Nhin Doc Submission");
        }
        return response;
    }

}
