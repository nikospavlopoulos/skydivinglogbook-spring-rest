// src/main/resources/static/js/dashboard.js
import { fetchWithAuth } from './api.js';
import { parseJwt } from './jwt.js';

export async function loadDashboard() {
    const container = document.getElementById('dashboard-container');
    const token = localStorage.getItem('jwtToken');

    if (!token) {
        window.location.href = 'index.html';
        return;
    }

    const payload = parseJwt(token);
    if (!payload || !payload.id) {
        localStorage.removeItem('jwtToken');
        window.location.href = 'index.html';
        return;
    }

    const userId = payload.id; // extract user ID from JWT

    try {
        const response = await fetchWithAuth(`/api/users/${userId}`);
        if (response.ok) {
            const data = await response.json();
            container.innerHTML = `<div>Welcome ${data.username}!</div>`;
        } else {
            container.innerHTML = `<div>Unauthorized. Please login again.</div>`;
            localStorage.removeItem('jwtToken');
            setTimeout(() => window.location.href = 'index.html', 2000);
        }
    } catch (err) {
        console.error(err);
        container.innerHTML = `<div>Network error.</div>`;
    }
}
