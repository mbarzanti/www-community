package verify;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

// ruleid:insecure-hostname-verifier
class AllHostsIHV implements HostnameVerifier {
    public boolean verify(final String hostname, final SSLSession session) {
        return true;
    }
}

// ok:insecure-hostname-verifier
class LocalHostIHV implements HostnameVerifier {
    public boolean verify(final String hostname, final SSLSession session) {
        return hostname.equals("localhost");
    }
}


// cf. https://stackoverflow.com/questions/2642777/trusting-all-certificates-using-httpclient-over-https
class InlineVerifierIHV {
    public void InlineVerifierIHV() {
        // ruleid:insecure-hostname-verifier
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }
}
