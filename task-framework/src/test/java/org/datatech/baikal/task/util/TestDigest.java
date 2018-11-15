package org.datatech.baikal.task.util;

import org.bouncycastle.jcajce.provider.digest.Skein;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDigest {
    private static final Logger logger = LoggerFactory.getLogger(TestDigest.class);

    @Test
    public void testSha3() {
        String input = "Hello world !";
        Skein.DigestSkein1024 digestSHA3 = new Skein.DigestSkein1024(32768 * 8);
        byte[] digest = digestSHA3.digest(input.getBytes());
        logger.info("Skein-32K : [{}]", Hex.toHexString(digest));
    }
}
