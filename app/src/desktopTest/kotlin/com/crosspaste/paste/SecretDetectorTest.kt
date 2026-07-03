package com.crosspaste.paste

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SecretDetectorTest {

    // --- Should detect ---

    @Test
    fun `detects PEM private keys`() {
        val pem =
            """
            -----BEGIN RSA PRIVATE KEY-----
            MIIEpAIBAAKCAQEA7bq7x
            -----END RSA PRIVATE KEY-----
            """.trimIndent()
        assertTrue(SecretDetector.isLikelySecret(pem))
        assertTrue(
            SecretDetector.isLikelySecret(
                "-----BEGIN OPENSSH PRIVATE KEY-----\nb3BlbnNzaC1rZXk=\n-----END OPENSSH PRIVATE KEY-----",
            ),
        )
    }

    @Test
    fun `detects known provider key prefixes`() {
        assertTrue(SecretDetector.isLikelySecret("sk-proj-Ab1Cd2Ef3Gh4Ij5Kl6Mn7Op8"))
        assertTrue(SecretDetector.isLikelySecret("sk_live_4eC39HqLyjWDarjtT1zdp7dc"))
        assertTrue(SecretDetector.isLikelySecret("ghp_16C7e42F292c6912E7710c838347Ae178B4a"))
        assertTrue(SecretDetector.isLikelySecret("github_pat_11ABCDEFG0abcdefghijkl"))
        assertTrue(SecretDetector.isLikelySecret("xoxb-1234567890-abcdefghijkl"))
        assertTrue(SecretDetector.isLikelySecret("glpat-XyZ123abc456def789gh"))
    }

    @Test
    fun `detects JWTs`() {
        val jwt =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIn0" +
                ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJVadQssw5c"
        assertTrue(SecretDetector.isLikelySecret(jwt))
    }

    @Test
    fun `detects AWS access key ids`() {
        assertTrue(SecretDetector.isLikelySecret("AKIAIOSFODNN7EXAMPLE"))
        assertTrue(SecretDetector.isLikelySecret("ASIAY34FZKBOKMUTVV7A"))
    }

    @Test
    fun `detects google api keys`() {
        assertTrue(SecretDetector.isLikelySecret("AIzaSyA-1234567890abcdefghijklmnopqrstu_-"))
    }

    @Test
    fun `detects high entropy single tokens`() {
        assertTrue(SecretDetector.isLikelySecret("aB3dE5fG7hJ9kL1mN2pQ4rS6tU8vW0xYzZ+cD/eF"))
    }

    @Test
    fun `detects secret assignments`() {
        assertTrue(SecretDetector.isLikelySecret("DB_PASSWORD=hunter2hunter2"))
        assertTrue(SecretDetector.isLikelySecret("api_key: 9f8e7d6c5b4a3210"))
        assertTrue(SecretDetector.isLikelySecret("export ACCESS_TOKEN=abcdef123456"))
        assertTrue(SecretDetector.isLikelySecret("\"client_secret\": \"abcd1234efgh5678\""))
    }

    // --- Should NOT detect (ordinary developer/user content) ---

    @Test
    fun `ignores ordinary prose`() {
        assertFalse(SecretDetector.isLikelySecret("Hello, this is a normal sentence."))
        assertFalse(SecretDetector.isLikelySecret("Meeting at 3pm tomorrow, bring the slides"))
    }

    @Test
    fun `ignores urls`() {
        assertFalse(
            SecretDetector.isLikelySecret(
                "https://example.com/some/Long_Path-1234?query=Value2&other=Thing3",
            ),
        )
        assertFalse(SecretDetector.isLikelySecret("www.example.com/a/b/c/d/e/f/g/h/i/j/k/1234567"))
    }

    @Test
    fun `ignores file paths`() {
        assertFalse(SecretDetector.isLikelySecret("/Users/rob/Documents/Some-Project_2024/Notes1.md"))
        assertFalse(SecretDetector.isLikelySecret("~/Library/Application Support/PasteFlow Dev"))
    }

    @Test
    fun `ignores git commit hashes`() {
        assertFalse(SecretDetector.isLikelySecret("c8f7dabd502ddab70a301edbbebbabef0db85513"))
    }

    @Test
    fun `ignores uuids`() {
        assertFalse(SecretDetector.isLikelySecret("30653de4-65b7-4d2c-98aa-d2649f5c5226"))
    }

    @Test
    fun `ignores content hashes with known prefixes`() {
        assertFalse(
            SecretDetector.isLikelySecret(
                "sha512-WfC7wZ9x1oQ2rT4uV6yA8bD0eF1gH3jK5mN7pQ9rS1tU3vW5xY7zA9bC1dE3fG5h==",
            ),
        )
    }

    @Test
    fun `ignores six digit numbers`() {
        // 2FA detection is deliberately out of scope: any 6-digit number matches.
        assertFalse(SecretDetector.isLikelySecret("482913"))
    }

    @Test
    fun `ignores empty and oversized content`() {
        assertFalse(SecretDetector.isLikelySecret(""))
        assertFalse(SecretDetector.isLikelySecret("   "))
        assertFalse(SecretDetector.isLikelySecret("a".repeat(20_001)))
    }

    @Test
    fun `ignores short tokens with secret-like prefixes`() {
        // Too short to be a real key; "sk-1" could be an ordinary label.
        assertFalse(SecretDetector.isLikelySecret("sk-1"))
    }
}
