export const pkce = {
    base64UrlEncode(buffer) {
        return btoa(String.fromCharCode(...buffer))
            .replace(/=/g, '')
            .replace(/\+/g, '-')
            .replace(/\//g, '_')
    },

    codeVerifier() {
        const array = new Uint8Array(32)
        crypto.getRandomValues(array)
        return this.base64UrlEncode(array)
    },

    async codeChallenge(verifier) {
        const encoder = new TextEncoder()
        const data = encoder.encode(verifier)
        const digest = await crypto.subtle.digest('SHA-256', data)
        return this.base64UrlEncode(new Uint8Array(digest))
    }
}
