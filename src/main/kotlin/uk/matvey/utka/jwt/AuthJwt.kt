package uk.matvey.utka.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.Verification
import uk.matvey.kit.time.TimeKit.instant
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class AuthJwt(
    private val algorithm: Algorithm,
    private val issuer: String? = null,
) {

    enum class ValidationResult {
        VALID,
        COULD_NOT_DECODE,
        INVALID,
        ;

        fun isValid() = this == VALID
    }

    fun issueJwt(
        expiration: Duration,
        audience: String? = null,
        subject: String? = null,
        issuedAt: Instant = instant(),
        config: JWTCreator.Builder.() -> Unit = {},
    ): String {
        return JWT.create()
            .withIssuedAt(issuedAt)
            .withExpiresAt(issuedAt.plus(expiration.toJavaDuration()))
            .apply {
                issuer?.let { withIssuer(it) }
                audience?.let { withAudience(it) }
                subject?.let { withSubject(subject) }
                config()
            }
            .sign(algorithm)
    }

    fun validateJwt(
        token: String,
        configure: Verification.() -> Unit = {},
    ): ValidationResult {
        val decoded = try {
            JWT.decode(token)
        } catch (e: JWTDecodeException) {
            return ValidationResult.COULD_NOT_DECODE
        }
        try {
            JWT.require(algorithm)
                .apply {
                    issuer?.let { withIssuer(it) }
                    configure()
                }
                .build()
                .verify(decoded)
        } catch (e: JWTVerificationException) {
            return ValidationResult.INVALID
        }
        return ValidationResult.VALID
    }
}
