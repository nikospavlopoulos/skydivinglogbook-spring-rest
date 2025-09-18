import { fetchWithAuth, handleUnauthorized } from './api.js';

export async function loadLookups() {
    const endpoints = ['dropzones', 'aircraft', 'jumptypes', 'users'];

    for (const ep of endpoints) {
        try {
            const resp = await fetchWithAuth(`http://localhost:8080/api/lookups/${ep}`);
            if (!resp.ok) return handleUnauthorized(resp);
            const data = await resp.json();
            document.getElementById(ep).innerHTML = `<h3>${ep.charAt(0).toUpperCase() + ep.slice(1)}</h3><pre>${JSON.stringify(data, null, 2)}</pre>`;
        } catch (err) {
            console.error(`Error loading ${ep}:`, err);
            document.getElementById(ep).innerHTML = '<div>Network error.</div>';
        }
    }
}
