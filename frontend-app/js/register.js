// src/main/resources/static/js/register.js
import { fetchWithAuth } from './api.js';

export async function handleRegister(event) {
    event.preventDefault();

    const username = document.getElementById('register-username').value;
    const firstname = document.getElementById('register-firstname').value || null;
    const lastname = document.getElementById('register-lastname').value || null;
    const password = document.getElementById('register-password').value;
    const confirmPassword = document.getElementById('register-confirm-password').value;
    const messageDiv = document.getElementById('register-message');

    messageDiv.textContent = '';

    if (password !== confirmPassword) {
        messageDiv.textContent = 'Passwords do not match';
        return;
    }

    const payload = { username, password, firstname, lastname };

    try {
        const response = await fetch('http://localhost:8080/api/users', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            messageDiv.textContent = 'Registration successful! Redirecting to login...';
            messageDiv.classList.add('success');
            setTimeout(() => window.location.href = 'index.html', 2000);
        } else {
            const error = await response.json();
            messageDiv.textContent = error.message || 'Registration failed';
        }
    } catch (err) {
        messageDiv.textContent = 'Network error';
        console.error(err);
    }
}
