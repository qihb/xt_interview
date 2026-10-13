package com.xt.interview.util;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @Test
    void md5EncodingIsStable() {
        assertEquals("e10adc3949ba59abbe56e057f20f883e", MD5Util.encode("123456"));
    }

    @Test
    void jwtRoundTripPreservesClaims() {
        JwtUtil jwt = new JwtUtil();
        ReflectionTestUtils.setField(jwt, "secret", "test-secret-key-that-is-long-enough-123456");
        ReflectionTestUtils.setField(jwt, "expiration", 60_000L);

        String token = jwt.generateToken(7L, "alice", 2);

        assertTrue(jwt.isTokenValid(token));
        assertEquals(7L, jwt.getUserIdFromToken(token));
        assertEquals("alice", jwt.getUsernameFromToken(token));
        assertEquals(2, jwt.getRoleLevelFromToken(token));
        assertFalse(jwt.isTokenValid("invalid-token"));
    }
}
