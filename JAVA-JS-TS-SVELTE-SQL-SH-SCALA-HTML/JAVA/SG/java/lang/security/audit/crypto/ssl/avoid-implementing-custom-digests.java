// ruleid: avoid-implementing-custom-digests
class MyProprietaryMessageDigest extends MessageDigest {

    @Override
    protected byte[] engineDigest() {
        return "";
    }
}

// ok: avoid-implementing-custom-digests
class NotMessageDigest {
    public NotMessageDigest() {
        System.out.println("");
    }
}
