package uk.matvey.utka.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.kit.random.RandomKit.randomAlphanumeric
import uk.matvey.kit.time.TimeKit.instant
import uk.matvey.utka.jwt.AuthJwt.ValidationResult.COULD_NOT_DECODE
import uk.matvey.utka.jwt.AuthJwt.ValidationResult.INVALID
import uk.matvey.utka.jwt.AuthJwt.ValidationResult.VALID
import java.time.temporal.ChronoUnit.SECONDS
import kotlin.time.Duration.Companion.minutes

class AuthJwtTest {

    private val algorithm = Algorithm.HMAC256("secret")
    private val issuer = "utka"
    private val audience = "audience"
    private val subject = "subject"

    @Test
    fun `should create jwt`() {
        // given
        val auth = AuthJwt(algorithm, issuer)
        val issuedAt = instant().truncatedTo(SECONDS)
        val claim = randomAlphanumeric(8)

        // when / then
        AuthJwt(algorithm).issueJwt(expiration = 10.minutes).also {
            JWT.decode(it)
        }
        auth.issueJwt(expiration = 10.minutes).also {
            val decoded = JWT.decode(it)
            assertThat(decoded.issuer).isEqualTo(issuer)
        }
        auth.issueJwt(
            expiration = 10.minutes,
            audience = audience,
            subject = subject,
            issuedAt = issuedAt,
        ) {
            withClaim("claim", claim)
        }.also {
            val decoded = JWT.decode(it)
            assertThat(decoded.audience).containsExactly(audience)
            assertThat(decoded.subject).isEqualTo(subject)
            assertThat(decoded.issuedAtAsInstant).isEqualTo(issuedAt)
            assertThat(decoded.getClaim("claim").asString()).isEqualTo(claim)
        }
    }

    @Test
    fun `should validate jwt`() {
        // given
        val auth = AuthJwt(algorithm, issuer)

        // when / then
        assertThat(auth.validateJwt(auth.issueJwt(expiration = 10.minutes))).isEqualTo(VALID)
        assertThat(auth.validateJwt("wrong")).isEqualTo(COULD_NOT_DECODE)
        assertThat(auth.validateJwt(AuthJwt(Algorithm.HMAC256("wrong")).issueJwt(expiration = 10.minutes)))
            .isEqualTo(INVALID)
    }
}