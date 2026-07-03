package com.crosspaste.paste

import kotlin.math.log2

/**
 * Conservative heuristics for spotting secrets at capture time so they never
 * enter the clipboard history. A false positive silently drops a capture the
 * user expected to keep, so every rule targets high-confidence patterns only.
 *
 * Deliberately not detected (false-positive risk outweighs the win for now):
 * - 2FA codes: any 6-digit number matches, and source-app context is not
 *   reliable enough to narrow it down.
 * - Recovery phrases: without bundling the BIP39 word list, a 12/24-word
 *   heuristic flags ordinary lowercase sentences.
 */
object SecretDetector {

    private const val MAX_SCAN_LENGTH = 10_000

    private const val MIN_TOKEN_LENGTH = 16

    private const val ENTROPY_MIN_LENGTH = 32
    private const val ENTROPY_MAX_LENGTH = 512
    private const val ENTROPY_BITS_PER_CHAR = 4.0
    private const val ENTROPY_MIN_CHAR_CLASSES = 3

    private val pemPrivateKeyRegex = Regex("-----BEGIN [A-Z ]*PRIVATE KEY-----")

    private val jwtRegex = Regex("^eyJ[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]*$")

    private val awsAccessKeyRegex = Regex("^(AKIA|ASIA)[0-9A-Z]{16}$")

    private val googleApiKeyRegex = Regex("^AIza[0-9A-Za-z_-]{35}$")

    // Well-known secret prefixes from popular services (OpenAI, Stripe,
    // GitHub, Slack, GitLab, npm, DigitalOcean, Shopify, Square).
    private val knownSecretPrefixes =
        listOf(
            "sk-",
            "sk_live_",
            "sk_test_",
            "rk_live_",
            "rk_test_",
            "ghp_",
            "gho_",
            "ghu_",
            "ghs_",
            "ghr_",
            "github_pat_",
            "xoxb-",
            "xoxp-",
            "xoxa-",
            "xoxr-",
            "xapp-",
            "glpat-",
            "npm_",
            "dop_v1_",
            "shpat_",
            "shpss_",
            "sq0atp-",
            "sq0csp-",
        )

    // Content hashes look like high-entropy tokens but are routinely copied
    // by developers and are not secrets.
    private val nonSecretPrefixes = listOf("sha256-", "sha384-", "sha512-")

    // KEY=value / key: value assignments whose name marks the value as secret.
    private val secretAssignmentRegex =
        Regex(
            "(?im)^[^\\n\\r]{0,40}?(password|passwd|secret|api[_-]?key|access[_-]?token|private[_-]?key)" +
                "[\"']?\\s*[=:]\\s*[\"']?\\S{8,}",
        )

    fun isLikelySecret(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || trimmed.length > MAX_SCAN_LENGTH) {
            return false
        }

        if (pemPrivateKeyRegex.containsMatchIn(trimmed)) {
            return true
        }

        if (trimmed.none { it.isWhitespace() }) {
            if (isSecretToken(trimmed)) {
                return true
            }
        }

        return secretAssignmentRegex.containsMatchIn(trimmed)
    }

    private fun isSecretToken(token: String): Boolean {
        if (nonSecretPrefixes.any { token.startsWith(it) }) {
            return false
        }
        if (token.length >= MIN_TOKEN_LENGTH &&
            knownSecretPrefixes.any { token.startsWith(it) }
        ) {
            return true
        }
        if (jwtRegex.matches(token)) {
            return true
        }
        if (awsAccessKeyRegex.matches(token)) {
            return true
        }
        if (googleApiKeyRegex.matches(token)) {
            return true
        }
        return isHighEntropyToken(token)
    }

    private fun isHighEntropyToken(token: String): Boolean {
        if (token.length !in ENTROPY_MIN_LENGTH..ENTROPY_MAX_LENGTH) {
            return false
        }
        // URLs and paths are long single tokens users copy constantly.
        if (token.contains("://") ||
            token.startsWith("www.") ||
            token.startsWith("/") ||
            token.startsWith("~")
        ) {
            return false
        }
        var hasLower = false
        var hasUpper = false
        var hasDigit = false
        var hasSymbol = false
        for (ch in token) {
            when {
                ch.isLowerCase() -> hasLower = true
                ch.isUpperCase() -> hasUpper = true
                ch.isDigit() -> hasDigit = true
                else -> hasSymbol = true
            }
        }
        val charClasses =
            listOf(hasLower, hasUpper, hasDigit, hasSymbol).count { it }
        if (charClasses < ENTROPY_MIN_CHAR_CLASSES) {
            return false
        }
        return shannonEntropy(token) >= ENTROPY_BITS_PER_CHAR
    }

    private fun shannonEntropy(s: String): Double {
        val length = s.length.toDouble()
        return s
            .groupingBy { it }
            .eachCount()
            .values
            .sumOf { count ->
                val p = count / length
                -p * log2(p)
            }
    }
}
