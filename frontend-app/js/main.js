// src/main/resources/static/js/main.js
import { handleRegister } from './register.js';
import { handleLogin } from './login.js';
import { loadDashboard } from './dashboard.js';

// Central loader and event attachment

document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('register-form');
    if (registerForm) registerForm.addEventListener('submit', handleRegister);

    console.log('DOM loaded, initializing...');
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        console.log('Login form found, attaching event listener');
        loginForm.addEventListener('submit', (event) => {
            console.log('Submit event triggered');
            handleLogin(event);
        }, { once: true });
    } else {
        console.error('Login form not found');
    }

    const dashboardContainer = document.getElementById('dashboard-container');
    if (dashboardContainer) loadDashboard();
});
