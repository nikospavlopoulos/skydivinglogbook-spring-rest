// src/main/resources/static/js/dashboard.js
import { fetchWithAuth } from './api.js';
import { parseJwt } from './jwt.js';

export async function loadDashboard() {
    const container = document.getElementById('dashboard-container');
    if (!container) {
            console.error('Dashboard container not found');
            return;
        }

    const token = localStorage.getItem('jwtToken');
    console.log('Token:', token ? 'Found' : 'Not found');

    if (!token) {
        console.log('No token, redirecting to index.html');
        window.location.href = 'index.html';
        return;
    }

    const payload = parseJwt(token);
    console.log('JWT Payload:', payload);
    if (!payload || !payload.sub) {
        console.log('Invalid payload or no user ID, clearing token and redirecting');
        localStorage.removeItem('jwtToken');
        window.location.href = 'index.html';
        return;
    }

    const username = payload.sub; // extract user ID from JWT
    console.log('Fetching user data for Username:', username);

    try {
        const response = await fetchWithAuth(`http://localhost:8080/api/jumps/all`, {
            method: 'GET'
        });
        console.log('Fetch response status:', response.status);
        if (response.ok) {
            const data = await response.json();
            console.log('User data:', data);
            container.innerHTML = `<div>Welcome ${username}!</div>`;
        } else {
            console.error('Fetch failed with status:', response.status);
            container.innerHTML = `<div>Unauthorized. Please login again.</div>`;
            localStorage.removeItem('jwtToken');
            setTimeout(() => window.location.href = 'index.html', 2000);
        }
    } catch (err) {
        console.error('Fetch error:', err);
        container.innerHTML = `<div>Network error.</div>`;
    }
}
