// src/main/resources/static/js/login.js
import { fetchWithAuth } from './api.js';

export async function handleLogin(event) {
    event.preventDefault();

    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;
    const messageDiv = document.getElementById('login-message');

    messageDiv.textContent = '';

    const payload = { username, password };

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('jwtToken', data.token);
            window.location.href = 'dashboard.html';
        } else {
            const error = await response.json();
            messageDiv.textContent = error.message || 'Login failed';
        }
    } catch (err) {
        messageDiv.textContent = 'Network error';
        console.error(err);
    }
}
