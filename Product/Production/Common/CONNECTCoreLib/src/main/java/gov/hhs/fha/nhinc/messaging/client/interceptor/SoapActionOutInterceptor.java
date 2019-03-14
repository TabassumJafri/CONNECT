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
package gov.hhs.fha.nhinc.messaging.client.interceptor;

import java.util.StringJoiner;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tjafri
 */
public class SoapActionOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger LOG = LoggerFactory.getLogger(SoapActionOutInterceptor.class);

    public SoapActionOutInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    @Override
    public void handleMessage(Message message) {
        LOG.info("Begin SoapActionOutInterceptor");
        String contentType = (String) message.get(Message.CONTENT_TYPE);

        LOG.info("CONTENT-TYPE: " + contentType);
        if (contentType.contains("action=")) {
            String[] strArr = contentType.split(";");
            StringJoiner joiner = new StringJoiner(";");
            for (String contentPart : strArr) {
                if (contentPart.contains("\"") && contentPart.chars().filter(ch -> ch == '\"').count() == 1) {
                    joiner.add(contentPart + "\"");
                } else if (contentPart.trim().startsWith("action=")) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("action=\"");
                    String str = StringUtils.substringAfter(contentPart, "action=");
                    if (StringUtils.isNotEmpty(str)) {
                        str = StringUtils.remove(str, "\\");
                        str = StringUtils.remove(str, "\"");
                        buffer.append(str);
                    }
                    buffer.append("\"");
                    joiner.add(buffer.toString());
                } else {
                    joiner.add(contentPart);
                }
            }
            message.remove(Message.CONTENT_TYPE);
            LOG.info("Fixed Content-Type: " + joiner.toString());
            message.put(Message.CONTENT_TYPE, joiner.toString());
        }
    }
}
