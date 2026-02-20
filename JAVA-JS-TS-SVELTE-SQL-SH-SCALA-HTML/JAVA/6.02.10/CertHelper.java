package it.poste.fdbp.web.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.CertificatePolicies;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.poste.fdbp.web.models.users.X509CertInfo;

public class CertHelper {
	static final Logger log = LoggerFactory.getLogger(CertHelper.class);
	
	public static final String OID_SUBJECT_KEY_IDENTIFIER = "2.5.29.14";
	public static final String OID_KEY_USAGE = "2.5.29.15";
	public static final String OID_PRIVATE_KEY_USAGE = "2.5.29.16";
	public static final String OID_SUBJECT_ALTERNATIVE_NAME = "2.5.29.17";
	public static final String OID_ISSUER_ALTERNATIVE_NAME = "2.5.29.18";
	public static final String OID_BASIC_CONSTRAINTS = "2.5.29.19";
	public static final String OID_NAME_CONSTRAINTS = "2.5.29.30";
	public static final String OID_CERTIFICATE_POLICIES = "2.5.29.32";
	public static final String OID_POLICY_MAPPINGS = "2.5.29.33";
	public static final String OID_AUTHORIY_KEY_IDENTIFIER = "2.5.29.35";
	public static final String OID_POLICY_CONSTRAINTS = "2.5.29.36";
	public static final String OID_USER_NOTICE = "1.3.6.1.5.5.7.2.2";

	private CertHelper() {		
	}

	public static X509Certificate x509FromPem(InputStream is) throws CertificateException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		return (X509Certificate)cf.generateCertificate(is);
	}
	
	public static X509CertInfo getCertInfoFromPem(InputStream is) throws CertificateException, InvalidNameException, IOException {
		return getCertInfo(x509FromPem(is));
	}

	public static X509CertInfo getCertInfo(X509Certificate cert) throws InvalidNameException, IOException {
		X509CertInfo certInfo = new X509CertInfo();
		
		certInfo.setVersion(cert.getVersion());
		certInfo.setSerialNumber(cert.getSerialNumber().toString());
		certInfo.setSubject(getCNfromRDN(cert.getSubjectX500Principal().getName()));
		certInfo.setIssuer(getCNfromRDN(cert.getIssuerX500Principal().getName()));		
		Instant instant = Instant.ofEpochMilli(cert.getNotAfter().getTime());		
		certInfo.setNotAfter(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
		instant = Instant.ofEpochMilli(cert.getNotBefore().getTime());
		certInfo.setNotBefore(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
		certInfo.setUserNotice(getUserNotice(cert));
		
		return certInfo;
	}
	
	public static String getRDNs(String dn, String type) throws InvalidNameException {
		LdapName ldapDN = new LdapName(dn);
		for(Rdn rdn: ldapDN.getRdns()) {
		    log.debug("{} -> {}", rdn.getType(), rdn.getValue());
		    if (rdn.getType().equalsIgnoreCase(type))
		    	return rdn.getValue().toString();
		}		
		return null;
	}
	
	public static String getCNfromRDN(String dn) throws InvalidNameException {
		return getRDNs(dn, "CN");
	}
	
	public static String getExtensionValue(X509Certificate cert, String oid) throws IOException {
		byte[] encodedExtensionValue = cert.getExtensionValue(oid);
		if (encodedExtensionValue != null) {
		    ASN1Primitive extensionValue = JcaX509ExtensionUtils
		            .parseExtensionValue(encodedExtensionValue);
		    return extensionValue.toString();          
		}	
		return null;
	}
			
	public static String getUserNotice(X509Certificate cert) throws IOException {
		byte[] policyBytes = cert.getExtensionValue(OID_CERTIFICATE_POLICIES);
		if (policyBytes != null) {
		    CertificatePolicies policies = CertificatePolicies.getInstance(JcaX509ExtensionUtils.parseExtensionValue(policyBytes));
		    PolicyInformation[] policyInformation = policies.getPolicyInformation();
		    for (PolicyInformation pInfo : policyInformation) {
		    	if (pInfo.getPolicyQualifiers() != null) {
			        ASN1Sequence policyQualifiers = (ASN1Sequence) pInfo.getPolicyQualifiers().getObjectAt(0);
			        ASN1Encodable policyIdentifier = policyQualifiers.getObjectAt(0);
			        ASN1Encodable policyValue = policyQualifiers.getObjectAt(1);
			        log.info("{} {}", policyIdentifier, policyValue);
			        if (OID_USER_NOTICE.equals(policyIdentifier.toString()))
			        	return policyValue.toString();
		    	}
		    }
		}
		return null;
	}
}
