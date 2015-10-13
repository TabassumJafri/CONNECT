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
package gov.hhs.fha.nhinc.patientdiscovery.parser;

import java.util.List;
import org.apache.log4j.Logger;
import org.hl7.v3.II;
import org.hl7.v3.PRPAIN201306UV02;
import org.hl7.v3.PRPAIN201306UV02MFMIMT700711UV01Subject1;

/**
 * This is a utility class that parses the PRPAIN201306UV02 message and extract patientId and participantObjectId from
 * it.
 *
 * @author tjafri
 */
public class PRPAIN201306UV02Parser {

    private static final Logger LOG = Logger.getLogger(PRPAIN201306UV02Parser.class);

    public static String getQueryId(PRPAIN201306UV02 response) {
        LOG.info("getQueryId()");
        String oid = null;

        if (response != null
            && response.getControlActProcess() != null
            && response.getControlActProcess().getQueryByParameter() != null
            && response.getControlActProcess().getQueryByParameter().getValue() != null
            && response.getControlActProcess().getQueryByParameter().getValue().getQueryId() != null) {

            oid = response.getControlActProcess().getQueryByParameter().getValue().getQueryId().getExtension();
        }

        return oid;
    }

    public static List<II> getPatientIds(PRPAIN201306UV02 response) {
        LOG.info("getPatientIds()");
        List<II> oIIs = null;
        if (response != null && response.getControlActProcess() != null
            && response.getControlActProcess().getSubject() != null) {

            for (PRPAIN201306UV02MFMIMT700711UV01Subject1 subject : response.getControlActProcess().getSubject()) {
                if (subject.getRegistrationEvent() != null && subject.getRegistrationEvent().getSubject1() != null
                    && subject.getRegistrationEvent().getSubject1().getPatient() != null
                    && subject.getRegistrationEvent().getSubject1().getPatient().getId() != null) {

                    oIIs = subject.getRegistrationEvent().getSubject1().getPatient().getId();
                } else {
                    LOG.error("PatientId doesn't exist in the received PRPAIN201306UV02 message");
                }
            }
        }
        return oIIs;
    }
}