// frontend-app/js/api.js

/**
 * Fetch wrapper that attaches JWT from localStorage if available.
 * @param {string} url
 * @param {object} options - fetch options
 * @returns {Promise<Response>}
 */



export async function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem('jwtToken');

    // Merge headers, always include Content-Type and Authorization if token exists
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
        ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    };

    const fetchOptions = {
        ...options,
        headers
        // No credentials needed for JWT in headers
    };

    return fetch(url, fetchOptions);
}












/*


export async function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem('jwtToken');

    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(url, { ...options, headers });
    return response;
}

*/
