// src/main/resources/static/js/login.js

export async function handleLogin(event) {
    console.log('handleLogin called');
    event.preventDefault();

    const usernameInput = document.getElementById('login-username');
    const passwordInput = document.getElementById('login-password');
    const messageDiv = document.getElementById('login-message');

    if (!usernameInput || !passwordInput || !messageDiv) {
        console.error('Form elements not found:', {
            usernameInput: !!usernameInput,
            passwordInput: !!passwordInput,
            messageDiv: !!messageDiv
        });
        return;
    }

    const username = usernameInput.value;
    const password = passwordInput.value;
    messageDiv.textContent = '';

    console.log('Payload:', { username, password });

    try {
        console.log('Sending POST to /api/auth/login');
        const response = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        console.log('Response status:', response.status);
        if (response.ok) {
            const data = await response.json();
            console.log('Login successful:', data);
            localStorage.setItem('jwtToken', data.token);
            window.location.href = 'dashboard.html';
        } else {
            const error = await response.json();
            messageDiv.textContent = error.message || 'Login failed';
        }
    } catch (err) {
        console.error('Fetch error:', err);
        messageDiv.textContent = 'Network error';
    }
}
