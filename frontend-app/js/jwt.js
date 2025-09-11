// src/main/resources/static/js/jwt.js
export function parseJwt(token) {
    if (!token) return null;
    try {
        const base64Url = token.split('.')[1]; // payload is the middle part
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split('')
                .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                .join('')
        );
        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error('Invalid JWT', e);
        return null;
    }
}
