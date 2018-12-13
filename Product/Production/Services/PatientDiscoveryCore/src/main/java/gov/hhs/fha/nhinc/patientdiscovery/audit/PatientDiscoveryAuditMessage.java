/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.fha.nhinc.patientdiscovery.audit;

import gov.hhs.fha.nhinc.audit.AuditMessage;
import gov.hhs.fha.nhinc.audit.transform.AuditTransforms;
import gov.hhs.fha.nhinc.patientdiscovery.audit.transform.PatientDiscoveryAuditTransforms;
import org.hl7.v3.PRPAIN201305UV02;
import org.hl7.v3.PRPAIN201306UV02;

/**
 *
 * @author tjafri
 */
public class PatientDiscoveryAuditMessage extends AuditMessage<PRPAIN201305UV02, PRPAIN201306UV02> {

    @Override
    protected AuditTransforms<PRPAIN201305UV02, PRPAIN201306UV02> getAuditTransforms() {
        return new PatientDiscoveryAuditTransforms();
    }
}
