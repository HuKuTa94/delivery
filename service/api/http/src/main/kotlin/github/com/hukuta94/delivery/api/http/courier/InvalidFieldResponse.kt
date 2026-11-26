package github.com.hukuta94.delivery.api.http.courier

/**
 * Invalid field of a request in the `errors` of a problem detail, as in the example of RFC 9457 section 3:
 * the [field] is a JSON pointer to the field, e.g. `#/name`; the [detail] is why the field is invalid.
 */
data class InvalidFieldResponse(
    val field: String,
    val detail: String,
)
